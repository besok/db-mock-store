package ru.besok.db.mock;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static ru.besok.db.mock.JpaDependency.Type.*;

/**
 * Created by Boris Zhguchev on 21/02/2019
 */
public class JpaEntity {

  private JpaId id;


  private Class<?> entityClass;
  private Header header;
  private List<JpaColumn> columns;
  private JpaDependencies dependencies;

  public JpaEntity() {
	columns = new ArrayList<>();
	dependencies = new JpaDependencies();
  }


  public List<JpaColumn> getColumns() {
	return columns;
  }

  public Object getIdValue(Object entity) {
	return ReflectionUtils.getValue(entity, this.getId().getField());
  }

  public Object setIdValue(Object entity, Object idValue) {
	return ReflectionUtils.setValue(entity, this.getId().getField(), idValue);
  }

  /**
   * @param entity      - object containing the needed field
   * @param column      - field column or field name
   * @param fieldEntity - dep entity
   * @param value       - value for setting
   * @return value
   */
  public Object setDependencyValue(Object entity, String column, JpaEntity fieldEntity, Object value) {

	dependencies
	  .get(column)
	  .filter(d -> d.getEntity().equals(fieldEntity))
	  .ifPresent(d -> ReflectionUtils.setValue(entity, d.getField(), value));

	return value;
  }


  @SuppressWarnings("unchecked")
  public Object setDependencyValueToCol(Object entity, String fieldName, Object value) {
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

  boolean isId(Field field){
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

  @AllArgsConstructor
  @Getter
  private static class Header {
	String name;
	String table;
	String schema;

	String formatForDb() {
	  if (schema.isEmpty())
		return table;
	  return schema + "." + table;
	}
  }


}
