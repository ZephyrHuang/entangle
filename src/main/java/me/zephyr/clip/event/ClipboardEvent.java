package me.zephyr.clip.event;

import java.util.EventObject;

public abstract class ClipboardEvent<T> extends EventObject {
    ClipboardEvent(T source) {
        super(source);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getSource() {
        return (T) super.getSource();
    }
}
