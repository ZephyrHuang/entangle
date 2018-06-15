package me.zephyr.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@Component
public class WebSocketSessionHolder {
  private static final Logger logger = LoggerFactory.getLogger(WebSocketSessionHolder.class);

  private static Long connectTimeOut;
  private static WebSocketStompClient webSocketStompClient;
  private static StompSessionHandler stompSessionHandler;
  /**
   * 暂时只做一对一的连接
   */
  private static volatile StompSession cache;

  public static Optional<StompSession> getSessionIfActive(String id) {
    if (cache != null && !cache.isConnected()) {
      cache = null; // 清理失效连接
      return Optional.empty();
    }
    return Optional.ofNullable(cache);
  }

  public static synchronized StompSession putSession(StompSession session) {
    if (cache == null || !cache.isConnected()) {
      Assert.notNull(session, "session must not be null!");
      cache = session;
    }
    return cache;
  }

  public static Optional<StompSession> removeSession(String id) {
    StompSession result = cache;
    cache = null;
    return Optional.ofNullable(result);
  }

  /**
   * 若没有有效连接的话就创建并返回一个有效连接，否则返回 {@link Optional#EMPTY}
   * @param url
   * @return
   */
  public static Optional<StompSession> createSessionIfRequired(String url) {
    if (cache != null && cache.isConnected()) {
      return Optional.empty();
    }
    Assert.notNull(webSocketStompClient, "webSocketStompClient is null");
    Assert.hasText(url, "url cannot be blank");
    if (!webSocketStompClient.isRunning()) {
      webSocketStompClient.start();
    }
    StompSession session = null;
    try {
      logger.debug("connectTimeOut:" + connectTimeOut);
      session = webSocketStompClient.connect(url, stompSessionHandler).get(connectTimeOut, TimeUnit.MILLISECONDS);
      putSession(session);
    } catch (TimeoutException e) {
      logger.error("向{}发起 websocket 连接超时！", url);
    } catch (Exception e) {
      logger.error("发起 websocket 连接时发生异常！", e);
    }
    return Optional.ofNullable(session);
  }

  //~ setter -----------------------------------------------------------------------------------------------------------

  @Autowired
  public void setConnectTimeOut(@Value("${websocket.connectTimeOut:3000}") Long timeOut) {
    setIfAbsent(connectTimeOut, timeOut, (value) -> connectTimeOut = value);
  }

  @Autowired
  public void setWebSocketStompClient(WebSocketStompClient client) {
    setIfAbsent(webSocketStompClient, client, (value) -> webSocketStompClient = value);
  }

  @Autowired
  @Qualifier("connectionHandler")
  public void setStompSessionHandler(StompSessionHandler handler) {
    setIfAbsent(stompSessionHandler, handler, (value) -> stompSessionHandler = value);
  }

  private <T> void setIfAbsent(T property, T value, Consumer<T> setter) {
    if (property != null) {
      throw new UnsupportedOperationException();
    }
    setter.accept(value);
  }
}
