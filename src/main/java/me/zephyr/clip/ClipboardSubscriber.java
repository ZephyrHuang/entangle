package me.zephyr.clip;

import me.zephyr.clip.sender.ClipContentSenderByWebSocket;
import me.zephyr.web.WebSocketSessionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ClipboardSubscriber {
  @Autowired
  @Qualifier("subscribeLatestClipboardContentHandler")
  private StompSessionHandler subscribeHandler;
  @Value("${websocket.send.targetUrl}")
  private String wsTargetUrl;

  @Scheduled(cron = "${websocket.subscribe.cron:*/5 * * * * ?}")
  public void subscribe() {
    WebSocketSessionHolder.createSessionIfRequired(wsTargetUrl)
        .ifPresent(stompSession -> stompSession.subscribe(ClipContentSenderByWebSocket.getDestination(), subscribeHandler));
  }
}
