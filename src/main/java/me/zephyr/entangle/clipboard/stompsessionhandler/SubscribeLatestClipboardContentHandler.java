package me.zephyr.entangle.clipboard.stompsessionhandler;

import me.zephyr.entangle.clipboard.ClipboardOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

@Component
public class SubscribeLatestClipboardContentHandler extends AbstractStompSessionHandler {
  private static final Logger logger = LoggerFactory.getLogger(SubscribeLatestClipboardContentHandler.class);

  @Autowired
  private ClipboardOperator clipboardOperator;

  @Override
  public void handleFrame(StompHeaders headers, Object payload) {
    if (!(payload instanceof String)) {
      logger.error("消息负载不是文本类型。");
      return;
    }
    String incoming = (String) payload;
    clipboardOperator.pushIntoSystemClipboard(incoming);
  }
}
