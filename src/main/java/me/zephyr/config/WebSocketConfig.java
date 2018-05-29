package me.zephyr.config;

import me.zephyr.web.clip.ClipWebSocketController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.jetty.JettyWebSocketClient;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
  @Value("${websocket.allowedOrigins:*}")
  private String[] allowedOrigins;
  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
    webSocketHandlerRegistry
        .addHandler(clipWebSocketController(), "/clip")
        .setAllowedOrigins(allowedOrigins);
  }

  @Bean
  public ClipWebSocketController clipWebSocketController() {
    return new ClipWebSocketController();
  }

  @Bean
//  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public WebSocketClient jettyWebSocketClient() {
    JettyWebSocketClient client = new JettyWebSocketClient();
    client.start();
    return client;
  }
}
