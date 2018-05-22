package me.zephyr.clip.listener;

import me.zephyr.clip.event.ClipboardEvent;
import me.zephyr.clip.sender.ClipContentSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClipboardContentSyncer implements ClipboardListener {
  private static final Logger logger = LoggerFactory.getLogger(ClipboardContentSyncer.class);
  @Autowired
  private ClipContentSender sender;
  /**
   * 开关。true=开，false=关。
   */
  @Value("${clipboard.send.switch:false}")
  private volatile boolean switchh;

  public void switchOff() {
    this.switchh = false;
  }

  public void switchOn() {
    this.switchh = true;
  }

  @Override
  public <T> void onClipboardChange(ClipboardEvent<T> event) {
    T content = event.getSource();
    sender.send(content);
  }

  @Override
  public <T> boolean isAcceptable(ClipboardEvent<T> event) {
    logger.debug("是否发送剪贴板内容：" + switchh);
    return switchh && event != null && String.class.equals(event.getSourceType());
  }
}
