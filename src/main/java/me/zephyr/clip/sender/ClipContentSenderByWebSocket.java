package me.zephyr.clip.sender;

import me.zephyr.web.WebSocketSessionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Qualifier("webSocketSender")
public class ClipContentSenderByWebSocket implements ClipContentSender {
  private static final Logger logger = LoggerFactory.getLogger(ClipContentSenderByWebSocket.class);
  private static Class<String> stringClass = String.class;

  @Value("${websocket.send.targetUrl:ws://10.191.196.183:8079/transfer/ws/clip}")
  private String targetUrl;
  @Value("${websocket.connectTimeOut:2000}")
  private long connectTimeOut;
  @Autowired
  private WebSocketClient webSocketClient;
  @Autowired
  private WebSocketHandler webSocketHandler;

  @Override
  public <T> void send(T content) {
    if (content == null || !content.getClass().isAssignableFrom(stringClass)) {
      return;//暂不支持非文本
    }
    CharSequence textToSend = (CharSequence) content;
    WebSocketSession session = WebSocketSessionHolder.getSessionIfOpen("").orElseGet(this::connectWebSocket);
    try {
      session.sendMessage(new TextMessage(textToSend));
    } catch (IOException e) {
      logger.error("通过 WebSocket 向{}发送消息时发生异常！", targetUrl);
    }
  }

  private WebSocketSession connectWebSocket() {
    try {
      WebSocketSession session = webSocketClient.doHandshake(webSocketHandler, targetUrl)
          .get(connectTimeOut, TimeUnit.MILLISECONDS);
      WebSocketSessionHolder.putSession(session);
      return session;
    } catch (Exception e) {
      logger.error("向{}发起 WebSocket 连接时发生异常!", targetUrl);
      throw new RuntimeException("发起 WebSocket 连接时发生异常！", e);
    }
  }
}
