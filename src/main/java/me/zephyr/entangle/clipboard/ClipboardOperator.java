package me.zephyr.entangle.clipboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Optional;

@Component
public class ClipboardOperator {
  private static final Logger logger = LoggerFactory.getLogger(ClipboardOperator.class);

  @Autowired
  private Clipboard clipboard;
  @Autowired
  private ClipboardMonitor clipboardMonitor;

  /**
   * 将文本存入剪贴板。
   */
  public void pushIntoSystemClipboard(String content) {
    clipboard.setContents(new StringSelection(content), clipboardMonitor);
  }

  /**
   * 从 {@link Transferable} 中获取文本内容。
   */
  public static Optional<String> getStringData(Transferable transferable) {
    if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
      try {
        return Optional.of((String) transferable.getTransferData(DataFlavor.stringFlavor));
      } catch (UnsupportedFlavorException | IOException e) {
        logger.error("从系统剪贴板读取字符串内容时发生异常！", e);
      }
    }
    return Optional.empty();
  }
}
