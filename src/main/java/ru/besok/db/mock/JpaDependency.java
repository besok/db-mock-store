package ru.besok.db.mock;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Boris Zhguchev on 21/02/2019
 */
public class JpaDependency {
  private Field field;
  private JpaEntity entity;
  private JpaEntity parent;
  private String column;
  private Type type;
  private String mappedBy;
  private Map<Property, Boolean> properties=new HashMap<>();

  public Map<Property, Boolean> getProperties() {
	return properties;
  }

  public JpaDependency(Field field, JpaEntity entity, JpaEntity parent, String column, Type type, String mappedBy, Map<Property, Boolean> properties) {
	this.field = field;
	this.entity = entity;
	this.parent = parent;
	this.column = column;
	this.type = type;
	this.mappedBy = mappedBy;
	this.properties = properties;
  }

  public JpaDependency(Field field, JpaEntity entity, JpaEntity parent, String column, Type type, String mappedBy) {

	this.field = field;
	this.entity = entity;
	this.parent = parent;
	this.column = column;
	this.type = type;
	this.mappedBy = mappedBy;
  }

  public JpaDependency(Field field, JpaEntity entity, JpaEntity parent, String column, String mappedBy) {
	this.field = field;
	this.entity = entity;
	this.parent = parent;
	this.column = column;
	this.mappedBy = mappedBy;
  }

  public Field getField() {
	return field;
  }

  public void setField(Field field) {
	this.field = field;
  }

  public JpaEntity getEntity() {
	return entity;
  }

  public void setEntity(JpaEntity entity) {
	this.entity = entity;
  }

  public JpaEntity getParent() {
	return parent;
  }

  public void setParent(JpaEntity parent) {
	this.parent = parent;
  }

  public String getColumn() {
	return column;
  }

  public void setColumn(String column) {
	this.column = column;
  }

  public Type getType() {
	return type;
  }

  public void setType(Type type) {
	this.type = type;
  }

  public void setProperties(Map<Property, Boolean> properties) {
	this.properties = properties;
  }

  JpaDependency property(Property p, boolean flag) {
	properties.put(p, flag);
	return this;
  }

  public String getMappedBy() {
	return mappedBy;
  }

  public void setMappedBy(String mappedBy) {
	this.mappedBy = mappedBy;
  }

  boolean property(Property p) {
	Boolean f = properties.get(p);
	return f == null ? p.defaultVal : f;

  }

  enum Type {
	O2O, M2O, O2M
  }

  enum Property {
	OPTIONAL(true), ALWAYS_NEW(false), JOIN_PRIMARY_KEYS(false);
	private boolean defaultVal;

	Property(boolean defaultVal) {
	  this.defaultVal = defaultVal;
	}
  }
}
