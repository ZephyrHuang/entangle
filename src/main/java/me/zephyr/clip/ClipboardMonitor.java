package me.zephyr.clip;

import me.zephyr.clip.event.ClipboardEvent;
import me.zephyr.clip.event.ClipboardEventWithString;
import me.zephyr.clip.listener.ClipboardListener;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ClipboardMonitor implements ClipboardOwner {
  private static final Logger logger = LoggerFactory.getLogger(ClipboardMonitor.class);
  private List<ClipboardListener> listeners;

  @Autowired
  public ClipboardMonitor(List<ClipboardListener> listeners) {
    this.setListeners(listeners);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(clipboard.getContents(null), this);
  }

  public static void main(String[] args) {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    Transferable str = new StringSelection("qwer");
    clipboard.setContents(str, null);
  }

  @Override
  public void lostOwnership(Clipboard clipboard, Transferable contents) {
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    String content = null;
    Transferable newTransferable = clipboard.getContents(null);
    clipboard.setContents(newTransferable, this);
    if (newTransferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
      try {
        content = (String) clipboard.getData(DataFlavor.stringFlavor);
      } catch (UnsupportedFlavorException e) {
        logger.error("", e);
      } catch (IOException e) {
        logger.error("", e);
      }
      ClipboardEvent event = new ClipboardEventWithString(content);
      for (ClipboardListener listener : listeners) {
        if (listener.isAcceptable(event)) {
          listener.onClipboardChange(event);
        }
      }
    }
  }

  public void setListeners(List<ClipboardListener> listeners) {
    this.listeners = CollectionUtils.isEmpty(listeners) ? new ArrayList<ClipboardListener>() : listeners;
  }
}
