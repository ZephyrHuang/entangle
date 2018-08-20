package me.zephyr.entangle.clipboard;

import me.zephyr.entangle.clipboard.sender.ClipContentSenderByWebSocket;
import me.zephyr.entangle.web.WebSocketSessionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static me.zephyr.entangle.web.WebSocketSessionHolder.createSessionIfRequired;

@Component
public class ClipboardSubscriber {
  @Autowired
  @Qualifier("subscribeLatestClipboardContentHandler")
  private StompSessionHandler subscribeHandler;
  @Value("${websocket.send.targetUrl}")
  private String wsTargetUrl;
  @Autowired
  private ClipContentSenderByWebSocket clipContentSender;

  @Scheduled(cron = "${websocket.subscribe.cron:*/5 * * * * ?}")
  public void subscribe() {
    WebSocketSessionHolder.getSessionIfActive().ifPresentOrElse(i -> {},
        () -> createSessionIfRequired(wsTargetUrl)
            .ifPresent(session -> session.subscribe(clipContentSender.getDestination(), subscribeHandler)));
  }
}
