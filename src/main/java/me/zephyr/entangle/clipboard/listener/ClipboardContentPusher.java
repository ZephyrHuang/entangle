package me.zephyr.entangle.clipboard.listener;

import me.zephyr.entangle.clipboard.event.ClipboardUpdatedEvent;
import me.zephyr.entangle.clipboard.event.ClipboardUpdatedTextEvent;
import me.zephyr.entangle.clipboard.sender.ClipContentSender;
import me.zephyr.entangle.config.property.ClipboardProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 推送剪贴板内容
 */
@Component
public class ClipboardContentPusher {
  private static final Logger logger = LoggerFactory.getLogger(ClipboardContentPusher.class);
  @Autowired
  @Qualifier("webSocketSender")
  private ClipContentSender sender;
  @Autowired
  private ClipboardProperties clipboardProps;

  @ClipboardUpdatedEventListener
  public void onApplicationEvent(ClipboardUpdatedEvent event) {
    //暂时只支持文本类型
    boolean switchh = clipboardProps.getSendSwitch();
    boolean isAcceptable = switchh && event instanceof ClipboardUpdatedTextEvent;
    logger.debug("发送剪贴板内容开关：{}；是否满足所有发送条件：{}", switchh, isAcceptable);
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
