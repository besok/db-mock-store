package ru.besok.db.mock;

import javax.persistence.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static ru.besok.db.mock.JpaDependency.Property.ALWAYS_NEW;
import static ru.besok.db.mock.JpaDependency.Property.JOIN_PRIMARY_KEYS;
import static ru.besok.db.mock.JpaDependency.Property.OPTIONAL;
import static ru.besok.db.mock.JpaDependency.Type.M2O;
import static ru.besok.db.mock.JpaDependency.Type.O2M;
import static ru.besok.db.mock.JpaDependency.Type.O2O;

/**
 * utils for processing jpa annotations
 * Created by Boris Zhguchev on 21/02/2019
 */
class JpaUtils {

    public static String DELIM = ";";

    static String camelToSnake(String field) {
        Queue<Character> q = new ArrayDeque<>();
        int i = 0;
        for (char c : field.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (i != 0) {
                    q.add('_');
                }
                q.add(Character.toLowerCase(c));
            } else {
                q.add(c);
            }
            i++;
        }
        StringBuilder sb = new StringBuilder();
        for (Character ch : q) {
            sb.append(ch);
        }
        return sb.toString();
    }

    static Optional<JpaId> findIdInField(Field field,Class<?> cl) {
        Optional<Id> id = findAnnotation(field, cl, Id.class);

        if (id.isPresent()) {
            String col = camelToSnake(field.getName());
            Optional<Column> column = findAnnotation(field, cl, Column.class);
            if (column.isPresent()) {
                String name = column.get().name();
                if (!name.isEmpty()) {
                    col = name;
                }
            }
            Optional<GeneratedValue> genVal = findAnnotation(field, cl, GeneratedValue.class);
            if (genVal.isPresent()) {
                return Optional.of(new JpaId(field, col, true));
            }
            return Optional.of(new JpaId(field, col, false));
        }

        return Optional.empty();
    }

    static JpaEntity initEntityWithHeader(Class<?> cl) {
        JpaEntity entity = new JpaEntity();
        entity.setEntityClass(cl);
        String className = cl.getSimpleName();
        if (cl.isAnnotationPresent(Table.class)) {
            Table tbl = cl.getAnnotation(Table.class);
            entity.setHeader(className, tbl.name(), tbl.schema());
        } else {
            entity.setHeader(className, camelToSnake(className), "");
        }

        return entity;
    }

    static boolean processManyToOne(JpaEntity entity, Field field) {
        Class<?> parent = entity.getEntityClass();
        Optional<ManyToOne> m2o = findAnnotation(field, parent, ManyToOne.class);
        if (m2o.isPresent()) {
            ManyToOne ann = m2o.get();
            JoinColumn jc = firstJoinColumn(field, parent);
            String mappedBy = Objects.nonNull(jc) ? jc.referencedColumnName() : "";
            JpaDependency d =
                    new JpaDependency(field, null, entity, columnForDependency(field, parent), M2O, mappedBy)
                            .property(OPTIONAL, ann.optional())
                            .property(JOIN_PRIMARY_KEYS, false)
                            .property(ALWAYS_NEW, false);
            entity.addDep(d);
            return true;
        }
        return false;
    }

    static boolean processOneToMany(JpaEntity entity, Field field) {
        Optional<OneToMany> o2m = findAnnotation(field, entity.getEntityClass(), OneToMany.class);
        if (o2m.isPresent()) {
            OneToMany ann = o2m.get();
            entity.addDep(new JpaDependency(field, null, entity, field.getName(), O2M, ann.mappedBy()));
            return true;
        }
        return false;
    }

    static boolean processOneToOne(JpaEntity entity, Field field) {
        Class<?> parent = entity.getEntityClass();
        Optional<OneToOne> o2o = findAnnotation(field, parent, OneToOne.class);
        if (o2o.isPresent()) {
            OneToOne ann = o2o.get();
            JoinColumn jc = firstJoinColumn(field, parent);
            String colName = field.getName();
            if (jc != null) {
                String jcName = jc.name();
                colName = jcName.isEmpty() ? colName : jcName;
            }
            entity.addDep(
                    new JpaDependency(field, null, entity, colName, O2O, ann.mappedBy())
                            .property(OPTIONAL, ann.optional())
                            .property(JOIN_PRIMARY_KEYS, isPrimaryKeyJoinCol(entity, field))
            );
            return true;
        }
        return false;
    }

    private static boolean isPrimaryKeyJoinCol(JpaEntity entity, Field field) {
        return findAnnotation(field,entity.getEntityClass(), PrimaryKeyJoinColumn.class)
                .isPresent();
    }

    static boolean processManyToMany(JpaEntity entity, Field field) {
        Optional<ManyToMany> m2m = findAnnotation(field, entity.getEntityClass(), ManyToMany.class);
        if (m2m.isPresent()) {
            return true;
        }
        return false;
    }

    static boolean processPlain(JpaEntity entity, Field field) {
        Class<?> pr = entity.getEntityClass();
        Optional<Column> col = findAnnotation(field, pr, Column.class);
        if (col.isPresent()) {
            Column ann = col.get();
            String name = ann.name().equals("") ? camelToSnake(field.getName()) : ann.name();
            entity.addCol(new JpaColumn(field, name, ann.nullable(), ann.length(), ann.precision(), ann.scale(), enumflag(field, pr)));
        } else {
            entity.addCol(new JpaColumn(field, camelToSnake(field.getName()), true, 0, 0, 0, enumflag(field, pr)));
        }
        return true;
    }

    private static JpaColumn.EnumFlag enumflag(Field field,Class<?> cl) {
        Class<?> aClass = field.getType();
        if (aClass.isEnum()) {
            Optional<Enumerated> enumerated = findAnnotation(field, cl, Enumerated.class);
            if (enumerated.isPresent()) {
                Enumerated annotation = enumerated.get();
                switch (annotation.value()) {
                    case ORDINAL:
                        return JpaColumn.EnumFlag.ORDINAL;
                    case STRING:
                        return JpaColumn.EnumFlag.STRING;
                }
            }
        }
        return JpaColumn.EnumFlag.NOT_ENUM;
    }

    static String quotesWrap(String e) {
        return "\"" + e + "\"";
    }

    static String concat(String delim, String... values) {
        StringBuilder sb = new StringBuilder();

        for (String value : values) {
            if (!value.isEmpty()) {
                sb.append(value).append(delim);
            }
        }

        String res = sb.toString();
        int idx = res.lastIndexOf(DELIM);
        if (idx == res.length() - 1)
            return res.substring(0, res.length() - 1);
        return res;
    }

    static String concat(String delim, Object... values) {
        String[] res = new String[values.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = values[i].toString();
        }
        return concat(delim, res);
    }

    private static JoinColumn firstJoinColumn(Field field,Class<?> cl) {
        Optional<JoinColumns> joinColumns = findAnnotation(field, cl, JoinColumns.class);
        if (joinColumns.isPresent()) {
            return joinColumns.get().value()[0];
        }
        Optional<JoinColumn> joinColumn = findAnnotation(field, cl, JoinColumn.class);
        return joinColumn.orElse(null);

    }

    private static String columnForDependency(Field f,Class<?> cl) {
        JoinColumn jc = firstJoinColumn(f,cl);
        return Objects.isNull(jc) ? camelToSnake(f.getName()) : jc.name();
    }

    protected static <T extends Annotation> Optional<T> findAnnotation(Field field, Class<?> parentClass, Class<T> annotationClass) {
        if (field.isAnnotationPresent(annotationClass)) {
            return Optional.of(field.getAnnotation(annotationClass));
        }
        try {
            Method getter = new PropertyDescriptor(field.getName(), parentClass).getReadMethod();
            if (getter.isAnnotationPresent(annotationClass)) {
                return Optional.of(getter.getAnnotation(annotationClass));
            }
        } catch (IntrospectionException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }
}
