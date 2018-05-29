package me.zephyr.clip;

import me.zephyr.clip.event.ClipboardEvent;
import me.zephyr.clip.listener.ClipboardListener;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

import static me.zephyr.util.ThreadUtil.sleep;

/**
 * 监听系统剪贴板是否被更新。
 */
@Component
public class ClipboardMonitor implements ClipboardOwner {
  private static final Logger logger = LoggerFactory.getLogger(ClipboardMonitor.class);
  private List<ClipboardListener> listeners;
  @Value("${clipboard.retry.interval:50}")
  private int retryInterval;
  @Value("${clipboard.retry.times:10}")
  private int retryTimes;

  @Autowired
  public ClipboardMonitor(List<ClipboardListener> listeners) {
    this.setListeners(listeners);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(clipboard.getContents(null), this);
  }

  public void setListeners(List<ClipboardListener> listeners) {
    this.listeners = CollectionUtils.isEmpty(listeners) ? new ArrayList<>() : listeners;
  }

  /**
   * 系统剪贴板被更新后，重新把此对象注册为监听器，同时若剪贴板中的新内容为文本对话，就通知所有 {@link ClipboardListener}。
   * @param clipboard
   * @param contents
   */
  @Override
  public void lostOwnership(Clipboard clipboard, Transferable contents) {
    TransferableHolder newContent = getNewContent(clipboard, TransferableHolder.init(), retryTimes);
    if (newContent == null) {
      return;
    } else if (!newContent.isSuccess()) {
      logger.error("操作系统剪贴板的过程中出现异常，无法获取其中的最新内容，并且已经不再对剪贴板进行监听。");
      return;
    }
    ClipboardEvent<String> event = new ClipboardEvent<>(newContent.getData());
    for (ClipboardListener listener : listeners) {
      if (listener.isAcceptable(event)) {
        listener.onClipboardChange(event);
      }
    }
  }

  private TransferableHolder getNewContent(Clipboard clipboard, TransferableHolder holder, int timesLeftToRetry) {
    if (timesLeftToRetry < 1) {
      return holder;
    }
    try {
      Transferable newTransferable = clipboard.getContents(null);
      clipboard.setContents(newTransferable, this); // 在剪贴板新内容上注册为监听器
      if (!newTransferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        return null;
      }
      String stringData = ClipboardOperator.getStringData(newTransferable);
      return StringUtils.isBlank(stringData) ? holder.fail() : holder.succeed(stringData);
    } catch (IllegalStateException ise) {
      sleep(retryInterval);
      return getNewContent(clipboard, holder, timesLeftToRetry - 1);
    }
  }

  //~ inner class ------------------------------------------------------------------------------------------------------

  static class TransferableHolder {
    private boolean success = false;
    private String stringData;

    private TransferableHolder(boolean success, String data) {
      this.success = success;
      this.stringData = data;
    }

    static TransferableHolder init() {
      return new TransferableHolder(false, null);
    }

    boolean isSuccess() {
      return this.success;
    }

    String getData() {
      return this.stringData;
    }

    TransferableHolder succeed(String stringData) {
      Assert.hasText(stringData, "stringData 不能为 null 或空字符串！");
      this.success = true;
      this.stringData = stringData;
      return this;
    }

    TransferableHolder fail() {
      this.success = false;
      return this;
    }
  }
}
