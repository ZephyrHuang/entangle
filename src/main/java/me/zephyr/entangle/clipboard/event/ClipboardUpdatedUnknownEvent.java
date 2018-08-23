package me.zephyr.entangle.clipboard.event;

import java.awt.datatransfer.Transferable;

public class ClipboardUpdatedUnknownEvent extends ClipboardUpdatedEvent {
  private static final long serialVersionUID = 1L;

  public ClipboardUpdatedUnknownEvent(Transferable source) {
    super(source);
  }
}
