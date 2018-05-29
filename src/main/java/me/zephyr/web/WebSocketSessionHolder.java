package me.zephyr.web;

import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

public class WebSocketSessionHolder {
  private static WebSocketSession cache;

  public static Optional<WebSocketSession> getSessionIfOpen(String id) {
    if (cache != null && !cache.isOpen()) {
      cache = null; // 清理失效连接
      return Optional.empty();
    }
    return Optional.ofNullable(cache);
  }

  public static void putSession(WebSocketSession session) {
    Assert.notNull(session, "session must not be null!");
    cache = session;
  }

  public static Optional<WebSocketSession> removeSession(String id) {
    WebSocketSession result = cache;
    cache = null;
    return Optional.ofNullable(result);
  }
}
