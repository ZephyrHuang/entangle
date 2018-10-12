package me.zephyr.entangle.clipboard.event;

import me.zephyr.entangle.clipboard.ClipboardOperator;

import java.awt.datatransfer.Transferable;

public class ClipboardUpdatedTextEvent extends ClipboardUpdatedEvent {
  private static final long serialVersionUID = 1L;

  private String text;

  public ClipboardUpdatedTextEvent(Transferable source) {
    super(source);
    text = ClipboardOperator.getStringData(source).orElse("");
  }

  public String getText() {
    return text;
  }
}
