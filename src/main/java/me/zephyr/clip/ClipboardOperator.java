package me.zephyr.clip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

@Component
public class ClipboardOperator {
  private static final Logger logger = LoggerFactory.getLogger(ClipboardOperator.class);
  private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
  @Autowired
  private ClipboardMonitor clipboardMonitor;

  public void pushIntoSystemClipboard(String content) {
    clipboard.setContents(new StringSelection(content), clipboardMonitor);
  }

  public static String getStringData(Transferable transferable) {
    if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
      try {
        return (String) transferable.getTransferData(DataFlavor.stringFlavor);
      } catch (UnsupportedFlavorException | IOException e) {
        logger.error("", e);
      }
    }
    return null;
  }

  public String pullSystemClipboardContent() {
    return getStringData(clipboard.getContents(null));
  }
}
