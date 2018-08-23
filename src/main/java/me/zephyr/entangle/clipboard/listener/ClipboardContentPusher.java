package me.zephyr.entangle.clipboard.listener;

import me.zephyr.entangle.clipboard.event.ClipboardUpdatedEvent;
import me.zephyr.entangle.clipboard.event.ClipboardUpdatedTextEvent;
import me.zephyr.entangle.clipboard.sender.ClipContentSender;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ClipboardContentPusher implements ApplicationListener<ClipboardUpdatedEvent> {
  private static final Logger logger = LoggerFactory.getLogger(ClipboardContentPusher.class);
  @Autowired
  @Qualifier("webSocketSender")
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
  public void onApplicationEvent(ClipboardUpdatedEvent event) {
    //暂时只支持文本类型
    boolean isAcceptable = switchh && event instanceof ClipboardUpdatedTextEvent;
    logger.debug("发送剪贴板内容开关：{}", switchh);
    logger.debug("是否满足所有发送条件：{}", isAcceptable);
    if (!isAcceptable) {
      return;
    }

    String content = ((ClipboardUpdatedTextEvent) event).getText();
    if (StringUtils.isNotBlank(content)) {
      sender.send(content);
    } else {
      logger.warn("剪贴板内容为空，未发送。");
    }
  }
}
