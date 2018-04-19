package me.zephyr.clip.listener;

import me.zephyr.clip.event.ClipboardEvent;
import me.zephyr.clip.event.ClipboardEventWithString;
import me.zephyr.clip.sender.ClipContentSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClipboardContentSyncer implements ClipboardListener {
  @Autowired
  private ClipContentSender sender;

  @Override
  public <T> void onClipboardChange(ClipboardEvent<T> event) {
    T content = event.getSource();
    sender.send(content);
  }

  @Override
  public boolean isAcceptable(ClipboardEvent event) {
    return event != null && event instanceof ClipboardEventWithString;
  }
}
