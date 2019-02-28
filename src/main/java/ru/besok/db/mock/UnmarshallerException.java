package ru.besok.db.mock;

/**
 * Created by Boris Zhguchev on 27/02/2019
 */
public class UnmarshallerException extends IllegalStateDbMockException {
  public UnmarshallerException() {
  }

  public UnmarshallerException(String message) {
	super(message);
  }

  public UnmarshallerException(String message, Throwable cause) {
	super(message, cause);
  }
}
