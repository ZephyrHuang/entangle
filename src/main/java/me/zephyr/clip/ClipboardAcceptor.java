package me.zephyr.clip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

@Component
public class ClipboardAcceptor {
  private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
  @Autowired
  private ClipboardMonitor clipboardMonitor;

  public void pushIntoSystemClipboard(String content) {
    clipboard.setContents(new StringSelection(content), clipboardMonitor);
  }
}
