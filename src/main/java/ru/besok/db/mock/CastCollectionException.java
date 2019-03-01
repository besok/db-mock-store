package ru.besok.db.mock;

/**
 * Exception while casting collection
 * Created by Boris Zhguchev on 23/02/2019
 */
public class CastCollectionException extends RuntimeException {
  public CastCollectionException() {
  }

  public CastCollectionException(String message) {
	super(message);
  }

  public CastCollectionException(String message, Throwable cause) {
	super(message, cause);
  }
}
