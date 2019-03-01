package ru.besok.db.mock;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;

/**
 * for {@link javax.persistence.Id} annotation
 * Created by Boris Zhguchev on 21/02/2019
 */
public class JpaId {
  private Field field;
  private String column;
  private boolean generated;

  public JpaId(Field field, String column, boolean generated) {
	this.field = field;
	this.column = column;
	this.generated = generated;
  }

  public Field getField() {
	return field;
  }

  public void setField(Field field) {
	this.field = field;
  }

  public String getColumn() {
	return column;
  }

  public void setColumn(String column) {
	this.column = column;
  }

  public boolean isGenerated() {
	return generated;
  }

  public void setGenerated(boolean generated) {
	this.generated = generated;
  }
}
