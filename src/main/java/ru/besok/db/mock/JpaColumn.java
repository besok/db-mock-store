package ru.besok.db.mock;

import java.lang.reflect.Field;

/**
 * Created by Boris Zhguchev on 21/02/2019
 */
public class JpaColumn {
  private Field field;
  private String name;
  private boolean nullable;
  private int length;
  private int precision;
  private int scale;

  public JpaColumn(Field field, String name, boolean nullable, int length, int precision, int scale) {
	this.field = field;
	this.name = name;
	this.nullable = nullable;
	this.length = length;
	this.precision = precision;
	this.scale = scale;
  }

  public Field getField() {
	return this.field;
  }

  public String getName() {
	return this.name;
  }

  public boolean isNullable() {
	return this.nullable;
  }

  public int getLength() {
	return this.length;
  }

  public int getPrecision() {
	return this.precision;
  }

  public int getScale() {
	return this.scale;
  }

  public void setField(Field field) {
	this.field = field;
  }

  public void setName(String name) {
	this.name = name;
  }

  public void setNullable(boolean nullable) {
	this.nullable = nullable;
  }

  public void setLength(int length) {
	this.length = length;
  }

  public void setPrecision(int precision) {
	this.precision = precision;
  }

  public void setScale(int scale) {
	this.scale = scale;
  }

  protected boolean canEqual(Object other) {
	return other instanceof JpaColumn;
  }

  public String toString() {
	return "JpaColumn(field=" + this.getField() + ", name=" + this.getName() + ", nullable=" + this.isNullable() + ", length=" + this.getLength() + ", precision=" + this.getPrecision() + ", scale=" + this.getScale() + ")";
  }
}
