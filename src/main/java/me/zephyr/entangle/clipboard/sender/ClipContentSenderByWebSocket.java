package me.zephyr.entangle.clipboard.sender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Qualifier("webSocketSender")
public class ClipContentSenderByWebSocket implements ClipContentSender {

  @Autowired
  private SimpMessagingTemplate sendingOperations;

  @Override
  public <T> void send(T content) {
    if (!(content instanceof String)) {
      return;//暂不支持非文本
    }
    String payload = (String) content;
    sendingOperations.convertAndSend(getDestination(), payload);
  }

  public String getDestination() {
    return "/queue/latestClipboardContent";
  }
}
