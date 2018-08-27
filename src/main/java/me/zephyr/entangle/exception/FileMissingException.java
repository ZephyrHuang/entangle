package me.zephyr.entangle.exception;

public class FileMissingException extends RuntimeException {
  static final long serialVersionUID = 1L;

  public FileMissingException(String message) {
    super(message);
  }

  public FileMissingException(String message, Throwable cause) {
    super(message, cause);
  }
}
