package me.zephyr.web.clip;

import me.zephyr.clip.ClipBoardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Component
public class ClipWebSocketController extends AbstractWebSocketHandler {
  private static final Logger logger = LoggerFactory.getLogger(ClipWebSocketController.class);

  @Autowired
  private ClipBoardService clipBoardService;

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    logger.debug("\r\n...connection established\r\n");
    super.afterConnectionEstablished(session);
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String content = message.getPayload();
    logger.debug("clipboard ==> " + content);
    clipBoardService.saveClipContent(content);
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    super.handleTransportError(session, exception);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    logger.debug("\r\n...connection closed\r\n");
    super.afterConnectionClosed(session, status);
  }
}
