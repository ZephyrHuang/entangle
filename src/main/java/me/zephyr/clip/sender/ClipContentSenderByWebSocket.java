package me.zephyr.clip.sender;

import me.zephyr.web.WebSocketSessionHolder;
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
  private static Class<String> stringClass = String.class;
  private static String destination = "/topic/latestClipContent";

  @Autowired
  private SimpMessagingTemplate sendingOperations;

  @Override
  public <T> void send(T content) {
    if (content == null || !content.getClass().isAssignableFrom(stringClass)) {
      return;//暂不支持非文本
    }
    CharSequence textToSend = (CharSequence) content;
    WebSocketSessionHolder.getSessionIfActive("").ifPresent(
        (session) -> sendingOperations.convertAndSendToUser(session.getSessionId(), getDestination(), new GenericMessage<>(textToSend)));
    logger.debug("向发送剪贴板内容：{}", textToSend);
  }

  public static String getDestination() {
    return destination;
  }
}
