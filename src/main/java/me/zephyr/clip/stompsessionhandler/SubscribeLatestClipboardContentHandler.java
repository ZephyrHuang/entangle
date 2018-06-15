package me.zephyr.clip.stompsessionhandler;

import me.zephyr.clip.ClipboardOperator;
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
      logger.error("消息负载不是字符串类型。");
      return;
    }
    String incoming = (String) payload;
    clipboardOperator.pushIntoSystemClipboard(incoming);
  }
}
