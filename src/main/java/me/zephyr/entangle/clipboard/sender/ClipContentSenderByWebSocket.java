package me.zephyr.entangle.clipboard.sender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

@Component
@Qualifier("webSocketSender")
public class ClipContentSenderByWebSocket implements ClipContentSender {

  @Autowired
  private SimpMessagingTemplate sendingOperations;

  @Override
  public <T> void send(T content) {
    if (!validate(content)) {
      return;
    }
    CharSequence payload = (CharSequence) content;
    sendingOperations.send(getDestination(), new GenericMessage<>(payload));
  }

  public String getDestination() {
    return "/queue/latestClipboardContent";
  }

  private <T> boolean validate(T arg) {
    //暂不支持非文本
    return arg instanceof String;
  }
}
