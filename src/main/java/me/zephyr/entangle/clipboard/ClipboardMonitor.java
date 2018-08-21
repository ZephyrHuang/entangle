package me.zephyr.entangle.clipboard;

import me.zephyr.entangle.clipboard.event.ClipboardEvent;
import me.zephyr.entangle.clipboard.listener.ClipboardListener;
import me.zephyr.util.thread.ThreadUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

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
  public void setListeners(List<ClipboardListener> listeners) {
    this.listeners = CollectionUtils.isEmpty(listeners) ? new ArrayList<>() : listeners;
  }

  /**
   * 系统剪贴板被更新后，重新把此对象注册为监听器，同时若剪贴板中的新内容为文本对话，就通知所有 {@link ClipboardListener}。
   */
  @Override
  public void lostOwnership(Clipboard clipboard, Transferable contents) {
    Transferable latestContent = getLatestAndReregister(clipboard, retryTimes);
    if (latestContent == null) {
      logger.error("未获取剪贴板中的最新内容，并且已经不再对剪贴板进行监听。");
      return;
    }
    ClipboardEvent event = new ClipboardEvent(latestContent);
    for (ClipboardListener listener : listeners) {
      if (listener.isAcceptable(event)) {
        listener.onClipboardChange(event);
      }
    }
  }

  /**
   * 获取剪贴板中最新的内容并重新将此对象注册为监视器。
   *
   * <p>操作系统剪贴板时可能会因为剪贴板正被占用而抛出异常，这里会递归重试。重试次数为 {@code timesLeftToRetry}。</p>
   *
   * @param clipboard        系统剪贴板对象
   * @param timesLeftToRetry 剩余的重试次数
   * @return 系统剪贴板中的最新内容，可能为 null。
   */
  private Transferable getLatestAndReregister(Clipboard clipboard, int timesLeftToRetry) {
    if (timesLeftToRetry < 1) {
      return null; // 重试次数耗尽
    }
    Transferable result;
    try {
      result = clipboard.getContents(null);
      clipboard.setContents(result, this); // 在剪贴板新内容上注册为监听器
    } catch (IllegalStateException ise) {
      ThreadUtil.sleepWithoutException(retryInterval);
      return getLatestAndReregister(clipboard, timesLeftToRetry - 1);
    }
    return result;
  }
}
