package me.zephyr.entangle.clipboard.sender;

public interface ClipContentSender {
    <T> void send(T content);
}
