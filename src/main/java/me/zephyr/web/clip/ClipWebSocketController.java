package me.zephyr.web.clip;

import me.zephyr.clip.ClipBoardService;
import me.zephyr.web.WebSocketSessionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Component
public class ClipWebSocketController extends AbstractWebSocketHandler {
  private static final Logger logger = LoggerFactory.getLogger(ClipWebSocketController.class);

  @Autowired
  private ClipBoardService clipBoardService;

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    logger.debug("\r\n...connection established\r\n");
    if (logger.isDebugEnabled()) {
      logger.debug(session.getId());
      logger.debug(session.getUri().toString());
      logger.debug(session.getRemoteAddress().toString());
    }
    WebSocketSessionHolder.putSession(session);
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String content = message.getPayload();
    logger.debug("clipboard ==> " + content);
    if ("pull".equals(content)) {
      if ()
    }
    clipBoardService.saveClipContent(content);
    if (logger.isDebugEnabled()) {
      logger.debug(session.getId());
      logger.debug(session.getUri().toString());
      logger.debug(session.getRemoteAddress().toString());
    }
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    logger.debug("webSocket 连接发生异常！", exception);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    logger.debug("\r\n...connection closed\r\n");
    WebSocketSessionHolder.removeSession(session.getId());
  }
}
