package me.zephyr.entangle.clipboard.listener;

import me.zephyr.entangle.clipboard.event.ClipboardEvent;

import java.util.EventListener;

public interface ClipboardListener extends EventListener {
    void onClipboardChange(ClipboardEvent event);

    boolean isAcceptable(ClipboardEvent event);
}
