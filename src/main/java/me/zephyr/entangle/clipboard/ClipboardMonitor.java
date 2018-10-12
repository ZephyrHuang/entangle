package me.zephyr.entangle.clipboard;

import me.zephyr.entangle.clipboard.event.ClipboardUpdatedEvent;
import me.zephyr.entangle.clipboard.listener.ClipboardContentPusher;
import me.zephyr.entangle.config.property.ClipboardProperties;
import me.zephyr.util.thread.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.Optional;

/**
 * 监听系统剪贴板是否被更新。
 */
@Component
public class ClipboardMonitor implements ClipboardOwner {
  private static final Logger logger = LoggerFactory.getLogger(ClipboardMonitor.class);

  @Autowired
  private ClipboardProperties clipboardProps;
  @Autowired
  private ApplicationEventPublisher eventPublisher;

  /**
   * 系统剪贴板被更新后，重新把此对象注册为监听器，并发布 {@link ClipboardUpdatedEvent}。
   */
  @Override
  public void lostOwnership(Clipboard clipboard, Transferable contents) {
    Optional<Transferable> latestContent = getLatestAndReregister(clipboard);
    latestContent.ifPresentOrElse(
        this::logAndPublish,
        () -> logger.error("未获取剪贴板中的最新内容，并且已经不再对剪贴板进行监听。"));
  }

  /**
   * 将获取的剪贴板内容打印日志并发布剪贴板更新事件。
   * @param transferable 最近更新的剪贴板内容
   */
  private void logAndPublish(Transferable transferable) {
    if (logger.isDebugEnabled()) {
      logger.debug("剪贴板内容：{}", ClipboardOperator.getStringData(transferable).orElse(""));
    }
    publish(transferable);
  }

  /**
   * 获取剪贴板中最新的内容并重新将此对象注册为监视器。
   *
   * <p>操作系统剪贴板时可能会因为剪贴板正被占用而抛出异常，这里会反复重试。重试次数为 {@code timesLeftToRetry}。</p>
   *
   * @param clipboard 系统剪贴板对象
   * @return 系统剪贴板中的最新内容，不会是 {@code null}，可能为 {@link Optional#EMPTY}。
   */
  private Optional<Transferable> getLatestAndReregister(Clipboard clipboard) {
    int timesLeftToRetry = clipboardProps.retry.getTimes();
    Transferable rawResult = null;
    do {
      try {
        rawResult = clipboard.getContents(null);
        clipboard.setContents(rawResult, this); // 在剪贴板新内容上注册为监听器
        break;
      } catch (IllegalStateException ise) {
        ThreadUtil.sleepWithoutThrow(clipboardProps.retry.getInterval());
        timesLeftToRetry--;
      }
    } while (timesLeftToRetry > 0);

    return Optional.ofNullable(rawResult);
  }

  /**
   * 发布事件
   *
   * @see ClipboardContentPusher
   */
  private void publish(Transferable latestContent) {
    ClipboardUpdatedEvent event = ClipboardUpdatedEvent.of(latestContent);
    eventPublisher.publishEvent(event);
  }
}
