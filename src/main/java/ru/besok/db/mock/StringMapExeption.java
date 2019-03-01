package ru.besok.db.mock;

/**
 * Exception signals error for converting string to entity
 * Created by Boris Zhguchev on 23/02/2019
 */
public class StringMapExeption extends IllegalStateDbMockException{
  public StringMapExeption() {
  }

  public StringMapExeption(String message) {
	super(message);
  }

  public StringMapExeption(String message, Throwable cause) {
	super(message, cause);
  }
}
