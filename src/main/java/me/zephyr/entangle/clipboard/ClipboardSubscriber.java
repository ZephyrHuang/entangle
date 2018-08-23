package me.zephyr.entangle.clipboard;

import me.zephyr.entangle.clipboard.sender.ClipContentSenderByWebSocket;
import me.zephyr.entangle.transport.stomp.WebSocketSessionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import static me.zephyr.entangle.transport.stomp.WebSocketSessionHolder.createSessionIfRequired;

@Component
public class ClipboardSubscriber {
  @Value("${clipboard.receive.switch:true}")
  private boolean receiveSwitch;
  @Autowired
  @Qualifier("subscribeLatestClipboardContentHandler")
  private StompSessionHandler subscribeHandler;
  @Value("${entangle.targetUrl}")
  private String targetUrl;
  @Autowired
  private ClipContentSenderByWebSocket clipContentSender;
  @Autowired
  @Qualifier("stompClientForString")
  private WebSocketStompClient stompClientForString;

  @Scheduled(cron = "${clipboard.receive.reconnect.cron:*/5 * * * * ?}")
  public void subscribe() {
    if (!receiveSwitch) {
      return;
    }
    WebSocketSessionHolder.getSessionIfActive().ifPresentOrElse(i -> {}/*do nothing*/,
        () -> createSessionIfRequired(targetUrl, stompClientForString)
            .ifPresent(session -> session.subscribe(clipContentSender.getDestination(), subscribeHandler)));
  }
}
