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

import static me.zephyr.util.ThreadUtil.sleep;

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

  @Override
  public void lostOwnership(Clipboard clipboard, Transferable contents) {
    String newContent = getNewContent(clipboard, 10);
    if (newContent == null) {
      logger.error("操作系统剪贴板的过程中出现异常，无法获取其中的最新内容，并且已经不再对剪贴板进行监听。");
    }
    ClipboardEvent event = new ClipboardEventWithString(newContent);
    for (ClipboardListener listener : listeners) {
      if (listener.isAcceptable(event)) {
        listener.onClipboardChange(event);
      }
    }
  }

  public void setListeners(List<ClipboardListener> listeners) {
    this.listeners = CollectionUtils.isEmpty(listeners) ? new ArrayList<ClipboardListener>() : listeners;
  }

  private String getNewContent(Clipboard clipboard, int timesLeftToRetry) {
    if (timesLeftToRetry < 1) {
      return null;
    }
    try {
      Transferable newTransferable = clipboard.getContents(null);
      clipboard.setContents(newTransferable, this);
      if (newTransferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        try {
          return (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException e) {
          logger.error("", e);
        } catch (IOException e) {
          logger.error("", e);
        }
      }
    } catch (IllegalStateException ise) {
      sleep(50);
      return getNewContent(clipboard, timesLeftToRetry - 1);
    }
    return null;
  }
}
