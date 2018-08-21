package me.zephyr.entangle.transport;

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

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static me.zephyr.util.misc.MiscUtil.setIfAbsentOrThrow;

@Component
public class WebSocketSessionHolder {
  private static final Logger logger = LoggerFactory.getLogger(WebSocketSessionHolder.class);
  private static final Object monitor = new Object();
  private static Long connectTimeOut;
  private static WebSocketStompClient webSocketStompClient;
  private static StompSessionHandler connectSessionHandler;
  /**
   * 暂时只做一对一的连接
   */
  private static volatile StompSession cache;


  //~ public methods ---------------------------------------------------------------------------------------------------

  public static boolean isSessionActive() {
    return cache != null && cache.isConnected();
  }

  public static boolean isSessionNotActive() {
    return cache != null && !cache.isConnected();
  }

  public static Optional<StompSession> getSessionIfActive() {
    if (isSessionNotActive()) {
      synchronized (monitor) {
        if (isSessionNotActive()) {
          cache = null; // 清理失效连接
        }
      }
    }
    return Optional.ofNullable(cache);
  }

  public static void putSession(StompSession session) {
    Objects.requireNonNull(session, "session must not be null!");
    if (!isSessionActive()) {
      synchronized (monitor) {
        if (!isSessionActive()) {
          cache = session;
        }
      }
    }
  }

  /**
   * 若没有有效连接的话就创建并返回一个有效连接，否则返回当前的有效连接
   */
  public static Optional<StompSession> createSessionIfRequired(String url) {
    Objects.requireNonNull(webSocketStompClient, "webSocketStompClient is null");
    Assert.hasText(url, "url cannot be blank");

    if (isSessionActive()) {
      return Optional.of(cache);
    }

    if (!webSocketStompClient.isRunning()) {
      webSocketStompClient.start();
    }

    StompSession session;
    try {
      logger.debug("connectTimeOut:{}", connectTimeOut);
      session = webSocketStompClient.connect(url, connectSessionHandler).get(connectTimeOut, TimeUnit.MILLISECONDS);
      putSession(session);
    } catch (TimeoutException e) {
      logger.error("向{}发起 websocket 连接超时！", url);
    } catch (Exception e) {
      logger.error("发起 websocket 连接时发生异常！", e);
    }
    return Optional.ofNullable(cache);
  }

  /**
   * 移除会话
   *
   * @return 被移除的那个会话
   */
  public static Optional<StompSession> removeSession() {
    synchronized (monitor) {
      StompSession result = cache;
      cache = null;
      return Optional.ofNullable(result);
    }
  }

  //~ setter -----------------------------------------------------------------------------------------------------------

  @Autowired
  public void setConnectTimeOut(@Value("${websocket.connectTimeOut:3000}") Long timeOut) {
    setIfAbsentOrThrow(connectTimeOut, timeOut, v -> connectTimeOut = v);
  }

  @Autowired
  public void setWebSocketStompClient(WebSocketStompClient client) {
    setIfAbsentOrThrow(webSocketStompClient, client, v -> webSocketStompClient = v);
  }

  @Autowired
  @Qualifier("connectionHandler")
  public void setConnectSessionHandler(StompSessionHandler sessionHandler) {
    setIfAbsentOrThrow(connectSessionHandler, sessionHandler, v -> connectSessionHandler = v);
  }
}
