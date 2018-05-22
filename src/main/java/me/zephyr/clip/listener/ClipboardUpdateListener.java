package me.zephyr.clip.listener;

import me.zephyr.clip.ClipboardStatusManager;
import me.zephyr.clip.event.ClipboardEvent;
import org.springframework.stereotype.Component;

@Component
public class ClipboardUpdateListener implements ClipboardListener {

  @Override
  public <T> void onClipboardChange(ClipboardEvent<T> event) {
    ClipboardStatusManager.clipboardHasBeenUpdated();
  }

  @Override
  public <T> boolean isAcceptable(ClipboardEvent<T> event) {
    return event != null && String.class.equals(event.getSourceType());
  }
}
