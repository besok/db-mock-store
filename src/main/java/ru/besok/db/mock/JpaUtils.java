package ru.besok.db.mock;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.*;

import static ru.besok.db.mock.JpaDependency.Property.ALWAYS_NEW;
import static ru.besok.db.mock.JpaDependency.Property.JOIN_PRIMARY_KEYS;
import static ru.besok.db.mock.JpaDependency.Property.OPTIONAL;
import static ru.besok.db.mock.JpaDependency.Type.M2O;
import static ru.besok.db.mock.JpaDependency.Type.O2M;
import static ru.besok.db.mock.JpaDependency.Type.O2O;

/**
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

  static Optional<JpaId> findIdInField(Field field) {
	if (field.isAnnotationPresent(Id.class)) {
	  String col = camelToSnake(field.getName());
	  if (field.isAnnotationPresent(Column.class)) {
		String name = field.getAnnotation(Column.class).name();
		if (!name.isEmpty()) {
		  col = name;
		}
	  }
	  if (field.isAnnotationPresent(GeneratedValue.class)) {
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
	if (field.isAnnotationPresent(ManyToOne.class)) {
	  ManyToOne ann = field.getDeclaredAnnotation(ManyToOne.class);
	  JoinColumn jc = firstJoinColumn(field);
	  String mappedBy = Objects.nonNull(jc) ? jc.referencedColumnName() : "";
	  JpaDependency d =
		new JpaDependency(field, null, entity, columnForDependency(field), M2O, mappedBy)
		  .property(OPTIONAL, ann.optional())
		  .property(JOIN_PRIMARY_KEYS, false)
		  .property(ALWAYS_NEW, false);
	  entity.addDep(d);
	  return true;
	}
	return false;
  }

  static boolean processOneToMany(JpaEntity entity, Field field) {
	if (field.isAnnotationPresent(OneToMany.class)) {
	  OneToMany ann = field.getDeclaredAnnotation(OneToMany.class);
	  entity.addDep(new JpaDependency(field, null, entity, field.getName(), O2M, ann.mappedBy()));
	  return true;
	}
	return false;
  }

  static boolean processOneToOne(JpaEntity entity, Field field) {
	if (field.isAnnotationPresent(OneToOne.class)) {
	  OneToOne ann = field.getDeclaredAnnotation(OneToOne.class);
	  JoinColumn jc = firstJoinColumn(field);
	  String colName = field.getName();
	  if(jc != null){
		String jcName = jc.name();
		colName= jcName.isEmpty()?colName:jcName;
	  }
	  entity.addDep(
		new JpaDependency(field, null, entity, colName, O2O, ann.mappedBy())
		  .property(OPTIONAL, ann.optional())
		  .property(JOIN_PRIMARY_KEYS, field.isAnnotationPresent(PrimaryKeyJoinColumn.class))
	  );
	  return true;
	}
	return false;
  }
  static boolean processManyToMany(JpaEntity entity, Field field) {
	if (field.isAnnotationPresent(ManyToMany.class)) {
	  return true;
	}
	return false;
  }

  static boolean processPlain(JpaEntity entity,Field field){
	if (field.isAnnotationPresent(Column.class)) {
	  Column ann = field.getDeclaredAnnotation(Column.class);
	  String name = ann.name().equals("")?camelToSnake(field.getName()):ann.name();
	  entity.addCol(new JpaColumn(field,name,ann.nullable(),ann.length(),ann.precision(),ann.scale()));
	}
	else{
	  entity.addCol(new JpaColumn(field,camelToSnake(field.getName()),true,0, 0,0));
	}
	return true;
  }

  static String quotesWrap(String e){
    return "\""+e+"\"";
  }

  static String concat(String delim,String... values){
    StringBuilder sb = new StringBuilder();

	for (String value : values) {
	  if(!value.isEmpty()){
	    sb.append(value).append(delim);
	  }
	}

	String res = sb.toString();
	int idx = res.lastIndexOf(DELIM);
	if(idx == res.length()-1)
	  return res.substring(0,res.length()-1);
	return res;
  }
  static String concat(String delim,Object... values){
   String[] res = new String[values.length];
	for (int i = 0; i < res.length; i++) {
	  res[i]=values[i].toString();
	}
   return concat(delim,res);
  }

  private static JoinColumn firstJoinColumn(Field field) {
	if (field.isAnnotationPresent(JoinColumns.class)) {
	  return field.getDeclaredAnnotation(JoinColumns.class).value()[0];
	}
	if (field.isAnnotationPresent(JoinColumn.class)) {
	  return field.getDeclaredAnnotation(JoinColumn.class);
	}

	return null;
  }

  private static String columnForDependency(Field f) {
	JoinColumn jc = firstJoinColumn(f);
	return Objects.isNull(jc) ? camelToSnake(f.getName()) : jc.name();
  }


}
