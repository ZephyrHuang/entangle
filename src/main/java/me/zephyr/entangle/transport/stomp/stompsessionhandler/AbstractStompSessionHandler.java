package me.zephyr.entangle.transport.stomp.stompsessionhandler;

import me.zephyr.entangle.transport.stomp.WebSocketSessionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

public abstract class AbstractStompSessionHandler extends StompSessionHandlerAdapter {
  private static final Logger logger = LoggerFactory.getLogger(AbstractStompSessionHandler.class);

  @Override
  public abstract void handleFrame(StompHeaders headers, Object payload);

  @Override
  public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
    if (WebSocketSessionHolder.isSessionPresentAndActive()) {
      throw new RuntimeException("已存在有效会话，无需建立新会话。");
    }
    WebSocketSessionHolder.putSession(session);
    logger.info("已建立 websocket 连接，sessionID={}", session.getSessionId());
  }

  @Override
  public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
    logger.error("处理 STOMP 消息时发生异常!", exception);
  }

  @Override
  public void handleTransportError(StompSession session, Throwable exception) {
    logger.error("websocket 连接发生底层错误！", exception);
  }
}
