package me.zephyr.entangle.clipboard.event;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.ApplicationEvent;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Objects;

public abstract class ClipboardUpdatedEvent extends ApplicationEvent {

  private static final long serialVersionUID = 1L;
  private DataFlavor[] dataFlavors;

  public static ClipboardUpdatedEvent of(Transferable transferable) {
    Objects.requireNonNull(transferable, "transferable must not be null!");
    if (ArrayUtils.contains(transferable.getTransferDataFlavors(), DataFlavor.stringFlavor)) {
      return new ClipboardUpdatedTextEvent(transferable);
    } else {
      return new ClipboardUpdatedUnknownEvent(transferable);
    }
  }

  public ClipboardUpdatedEvent(Transferable source) {
    super(source);
    dataFlavors = source.getTransferDataFlavors();
  }

  @Override
  public Transferable getSource() {
    return (Transferable) super.getSource();
  }

  public DataFlavor[] getDataFlavors() {
    return dataFlavors;
  }
}
