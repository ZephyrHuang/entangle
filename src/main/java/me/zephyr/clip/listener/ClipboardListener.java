package me.zephyr.clip.listener;

import me.zephyr.clip.event.ClipboardEvent;

import java.util.EventListener;

public interface ClipboardListener extends EventListener {
    <T> void onClipboardChange(ClipboardEvent<T> event);

    boolean isAcceptable(ClipboardEvent event);
}
