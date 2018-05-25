package me.zephyr.clip.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Qualifier("webSocketSender")
public class ClipContentSenderByWebSocket implements ClipContentSender {
  private static final Logger logger = LoggerFactory.getLogger(ClipContentSenderByWebSocket.class);

  @Value("${clipboard.send.targetUrl:http://10.191.196.183:8079/transfer/clip/set}")
  private String targetUrl;

  @Override
  public <T> void send(T content) {

  }
}
