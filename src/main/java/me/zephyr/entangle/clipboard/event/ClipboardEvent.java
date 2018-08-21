package me.zephyr.entangle.clipboard.event;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.EventObject;

public class ClipboardEvent extends EventObject {
  private static final long serialVersionUID = 1L;

  private DataFlavor[] dataFlavors;

  public ClipboardEvent(Transferable source) {
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
