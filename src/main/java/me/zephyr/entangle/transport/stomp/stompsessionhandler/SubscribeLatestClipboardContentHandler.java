package me.zephyr.entangle.transport.stomp.stompsessionhandler;

import me.zephyr.entangle.clipboard.ClipboardOperator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

@Component
public class SubscribeLatestClipboardContentHandler extends AbstractStompSessionHandler {
  private static final Logger logger = LoggerFactory.getLogger(SubscribeLatestClipboardContentHandler.class);

  private static final Object monitor = new Object();
  @Autowired
  private ClipboardOperator clipboardOperator;
  /**
   * 缓存最近接收到的文本，防止被第三方软件影响在两个端点之间产生回响。
   */
  private volatile String stringPushedLastTime;

  @Override
  public void handleFrame(StompHeaders headers, Object payload) {
    if (!(payload instanceof String)) {
      logger.warn("消息负载不是文本类型。");
      return;
    }
    String incoming = (String) payload;
    if (StringUtils.isBlank(incoming)) {
      return;
    }
    if (!incoming.equals(stringPushedLastTime)) {
      synchronized (monitor) {
        if (!incoming.equals(stringPushedLastTime)) {
          clipboardOperator.pushIntoSystemClipboard(incoming);
          stringPushedLastTime = incoming;
        }
      }
    }
  }
}
