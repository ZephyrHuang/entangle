package me.zephyr.entangle.clipboard;

import me.zephyr.entangle.clipboard.sender.ClipContentSenderByWebSocket;
import me.zephyr.entangle.config.property.ClipboardProperties;
import me.zephyr.entangle.config.property.EntangleProperties;
import me.zephyr.entangle.transport.stomp.WebSocketSessionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import static me.zephyr.entangle.transport.stomp.WebSocketSessionHolder.createSessionIfRequired;

@Component
public class ClipboardSubscriber {
  @Autowired
  @Qualifier("subscribeLatestClipboardContentHandler")
  private StompSessionHandler subscribeHandler;
  @Autowired
  private ClipContentSenderByWebSocket clipContentSender;
  @Autowired
  @Qualifier("stompClientForString")
  private WebSocketStompClient stompClientForString;
  @Autowired
  private EntangleProperties entangleProps;
  @Autowired
  private ClipboardProperties clipboardProps;

  @Scheduled(cron = "*/5 * * * * ?")
  public void subscribe() {
    if (!clipboardProps.getReceiveSwitch()) {
      return;
    }
    if (!WebSocketSessionHolder.getSessionIfActive().isPresent()) {
      createSessionIfRequired(entangleProps.getTargetUrl(), stompClientForString).ifPresent(this::doSubscription);
    }
  }

  private void doSubscription(StompSession session) {
    session.subscribe(clipContentSender.getDestination(), subscribeHandler);
  }
}
