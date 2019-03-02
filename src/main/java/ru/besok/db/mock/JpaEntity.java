package ru.besok.db.mock;


import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static ru.besok.db.mock.JpaDependency.Type.*;
/**
 * Major model class for maintaining pojo information.
 * Created by Boris Zhguchev on 21/02/2019
 */
public class JpaEntity {
// TODO: 01.03.2019 Add wrapper for ReflectionUtils methods to this class for getting unified logic.

  private JpaId id;


  private Class<?> entityClass;
  private Header header;
  private List<JpaColumn> columns;
  private JpaDependencies dependencies;

  public JpaEntity() {
	columns = new ArrayList<>();
	dependencies = new JpaDependencies();
  }

  /**
   * new instance for pojo
   * @return object with this type
   * @throws UnmarshallerException when we can't instantiate entity
   */
  Object newInstance() {
	try {
	  return entityClass.newInstance();
	} catch (InstantiationException | IllegalAccessException e) {
	  throw new UnmarshallerException(" can not instantiate entity " + entityClass.getSimpleName(), e);
	}
  }

  List<JpaColumn> getColumns() {
	return columns;
  }

  Object getIdValue(Object entity) {
	return ReflectionUtils.getValue(entity, this.getId().getField());
  }

  Object setIdValue(Object entity, Object idValue) {
	return ReflectionUtils.setValue(entity, this.getId().getField(), idValue);
  }

  /**
   * Set object to field containing dependency object
   *
   * @param entityValue  object containing the needed field
   * @param column       field column or field name
   * @param fieldEntity  dep entity
   * @param value        value for setting
   * @return value
   * @throws IllegalStateDbMockException for reflection exceptions
   */
  Object setDependencyValue(Object entityValue, String column, JpaEntity fieldEntity, Object value) {
	dependencies
	  .get(column)
	  .filter(d -> d.getEntity().equals(fieldEntity))
	  .ifPresent(d -> ReflectionUtils.setValue(entityValue, d.getField(), value));
	return value;
  }

  /**
   * reverse process for {@link javax.persistence.OneToMany} collections. When we set entity to {@link javax.persistence.ManyToOne} field
   * we must check if this relation is bidirectional(has OneToMany) and in this case will set entity to collection.
   * @param entity entity containing collection
   * @param fieldName field name is pointed from another class by mappedBy field
   * @param value value for setting
   * @return value
   */
  @SuppressWarnings("unchecked")
  Object setDependencyValueToCol(Object entity, String fieldName, Object value) {
	this.getDependenciesByType(O2M)
	  .stream()
	  .filter(d -> Objects.equals(d.getMappedBy(), fieldName)).findAny()
	  .ifPresent(d -> {
		Field field = d.getField();
		Object col = ReflectionUtils.getValue(entity, field);
		if (Objects.isNull(col)) {
		  col = ReflectionUtils.newCollection(field);
		}
		if (Objects.nonNull(col)) {
		  ((Collection) col).add(value);
		  ReflectionUtils.setValue(entity, field, col);
		}
	  });
	return value;
  }

  boolean isId(Field field) {
	return this.id.getField().equals(field);
  }

  Set<JpaDependency> dependencySet() {
	return dependencies.getDependencies();
  }

  public Set<JpaDependency> getDependenciesByType(JpaDependency.Type type) {
	return dependencies.getDependencies().stream().filter(d -> d.getType() == type).collect(Collectors.toSet());
  }

  private boolean replaceDependency(JpaDependency dependency) {
	return dependencies.replace(dependency);
  }

  boolean replaceDependency(Collection<JpaDependency> dependencies) {
	for (JpaDependency dependency : dependencies) {
	  replaceDependency(dependency);
	}
	return true;
  }

  public JpaId getId() {
	return id;
  }

  public JpaEntity setId(JpaId id) {
	this.id = id;
	return this;
  }

  JpaEntity addDep(JpaDependency d) {
	dependencies.put(d);
	return this;
  }

  Optional<JpaDependency> findDep(Field field) {
	return dependencies.get(field);
  }

  boolean isPlain() {
	return dependencies.getDependencies().isEmpty();
  }

  public Class<?> getEntityClass() {
	return entityClass;
  }

  public void setEntityClass(Class<?> entityClass) {
	this.entityClass = entityClass;
  }

  public JpaEntity addCol(JpaColumn column) {
	columns.add(column);
	return this;
  }

  public Optional<JpaColumn> findCol(Field field) {
	return columns.stream().filter(e -> e.getField().equals(field)).findAny();
  }

  public Optional<JpaColumn> findCol(String column) {
	return columns.stream().filter(e -> e.getName().equals(column)).findAny();
  }

  public void setHeader(String name, String table, String schema) {
	header = new Header(name, table, schema);
  }

  public String dbHeader() {
	return header.formatForDb();
  }

  public String fullHeader() {
	return header.getName() + "." + dbHeader();
  }

  public boolean compareByDbHeader(String schema, String table) {
	String sch = header.getSchema();
	String tbl = header.getTable();
	return Objects.equals(sch, schema) && Objects.equals(tbl, table);
  }

  private static class Header {
	String name;
	String table;
	String schema;

	Header(String name, String table, String schema) {
	  this.name = name;
	  this.table = table;
	  this.schema = schema;
	}

	String formatForDb() {
	  if (schema.isEmpty())
		return table;
	  return schema + "." + table;
	}

	String getName() {
	  return name;
	}

	String getTable() {
	  return table;
	}

	String getSchema() {
	  return schema;
	}
  }


}
