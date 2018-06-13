package me.zephyr.clip.sender;

public interface ClipContentSender {
    <T> void send(T content);
}
