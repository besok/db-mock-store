package ru.besok.db.mock.store;

/**
 * Created by Boris Zhguchev on 21/02/2019
 */
public class JpaAnnotationScanException extends RuntimeException {
  public JpaAnnotationScanException() {
    this(" getting error while scanning package ");
  }

  public JpaAnnotationScanException(String message) {
	super(message);
  }

  public JpaAnnotationScanException(String message, Throwable cause) {
	super(message, cause);
  }
}
