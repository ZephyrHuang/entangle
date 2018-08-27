package me.zephyr.entangle.exception;

public class FileCreationFailedException extends RuntimeException {
  static final long serialVersionUID = 1L;

  public FileCreationFailedException(String message) {
    super(message);
  }

  public FileCreationFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
