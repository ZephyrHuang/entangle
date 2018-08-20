package me.zephyr.entangle.clipboard.sender;

import me.zephyr.entangle.web.WebSocketSessionHolder;
import me.zephyr.util.map.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

@Component
@Qualifier("webSocketSender")
public class ClipContentSenderByWebSocket implements ClipContentSender {
  private static final Logger logger = LoggerFactory.getLogger(ClipContentSenderByWebSocket.class);

  @Autowired
  private SimpMessagingTemplate sendingOperations;

  @Override
  public <T> void send(T content) {
    if (!validate(content)) {
      return;
    }
    CharSequence stuffToSend = (CharSequence) content;
    WebSocketSessionHolder.getSessionIfActive().ifPresent((session) -> {
      logger.debug("向发送剪贴板内容：{}", stuffToSend);
      sendingOperations.convertAndSendToUser(session.getSessionId(), getDestination(),
          new GenericMessage<>(stuffToSend), MapUtil.of("payloadType", "text"));
    });

  }

  public String getDestination() {
    return "/topic/latestClipboardContent";
  }

  private <T> boolean validate(T arg) {
    //暂不支持非文本
    return arg instanceof CharSequence;
  }
}
