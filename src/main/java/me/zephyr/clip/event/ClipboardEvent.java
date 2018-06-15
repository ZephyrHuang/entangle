package me.zephyr.clip.event;

import java.util.EventObject;

public class ClipboardEvent<T> extends EventObject {
  private static final long serialVersionUID = 1L;

  private Class<T> sourceType;

  @SuppressWarnings("unchecked")
  public ClipboardEvent(T source) {
    super(source);
    sourceType = (Class<T>) source.getClass();
  }

  @Override
  @SuppressWarnings("unchecked")
  public T getSource() {
    return (T) super.getSource();
  }

  public Class<T> getSourceType() {
    return sourceType;
  }
}
