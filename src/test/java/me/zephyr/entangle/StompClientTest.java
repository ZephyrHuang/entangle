package me.zephyr.entangle;

import me.zephyr.entangle.clipboard.sender.ClipContentSenderByWebSocket;
import me.zephyr.entangle.transport.stomp.WebSocketSessionHolder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EntangleBootApplication.class)
public class StompClientTest {
  private static final Logger logger = LoggerFactory.getLogger(StompClientTest.class);
  @Autowired
  private ClipContentSenderByWebSocket clipContentSender;
  @Autowired
  @Qualifier("stompClientForString")
  private WebSocketStompClient stompClient;

  @Test
  public void testClient() {
    String url = "ws://localhost:8080/entangle/entangleEndpoint";
    final StompSession[] session = new StompSession[1];
    WebSocketSessionHolder.createSessionIfRequired(url, stompClient).ifPresent(s -> {
      session[0] = s;
      clipContentSender.send("example");});
    logger.info("sessionId={}", session[0].getSessionId());
  }
}
