package me.zephyr.entangle.clipboard;

import me.zephyr.entangle.clipboard.event.ClipboardUpdatedEvent;
import me.zephyr.entangle.config.property.ClipboardProperties;
import me.zephyr.util.thread.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

/**
 * 监听系统剪贴板是否被更新。
 */
@Component
public class ClipboardMonitor implements ClipboardOwner, ApplicationEventPublisherAware {
  private static final Logger logger = LoggerFactory.getLogger(ClipboardMonitor.class);

  @Autowired
  private ClipboardProperties clipboardProps;

  private ApplicationEventPublisher eventPublisher;

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.eventPublisher = applicationEventPublisher;
  }

  /**
   * 系统剪贴板被更新后，重新把此对象注册为监听器，并发布 {@link ClipboardUpdatedEvent}。
   */
  @Override
  public void lostOwnership(Clipboard clipboard, Transferable contents) {
    Transferable latestContent = getLatestAndReregister(clipboard, clipboardProps.retry.getTimes());
    if (latestContent == null) {
      logger.error("未获取剪贴板中的最新内容，并且已经不再对剪贴板进行监听。");
      return;
    } else {
      if (logger.isDebugEnabled()) {
        logger.debug("剪贴板内容：{}", ClipboardOperator.getStringData(latestContent));
      }
    }
    publish(latestContent);
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
      ThreadUtil.sleepWithoutThrow(clipboardProps.retry.getInterval());
      return getLatestAndReregister(clipboard, timesLeftToRetry - 1);
    }
    return result;
  }

  /**
   * 发布事件
   */
  private void publish(Transferable latestContent) {
    ClipboardUpdatedEvent event = ClipboardUpdatedEvent.of(latestContent);
    eventPublisher.publishEvent(event);
  }
}
