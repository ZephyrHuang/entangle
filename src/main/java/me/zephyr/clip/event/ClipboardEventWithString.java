package me.zephyr.clip.event;

public class ClipboardEventWithString extends ClipboardEvent<String> {
    private static final Class<String> supportedType = String.class;

    public ClipboardEventWithString(String source) {
        super(source);
    }
}
