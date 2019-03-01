package ru.besok.db.mock;

/**
 * Common exception for this package
 * Created by Boris Zhguchev on 23/02/2019
 */
public class IllegalStateDbMockException extends RuntimeException {
  public IllegalStateDbMockException() {
  }

  public IllegalStateDbMockException(String message) {
	super(message);
  }

  public IllegalStateDbMockException(String message, Throwable cause) {
	super(message, cause);
  }
}
