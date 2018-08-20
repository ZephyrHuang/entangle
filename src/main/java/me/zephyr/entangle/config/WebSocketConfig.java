package me.zephyr.entangle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  @Override
  public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
    stompEndpointRegistry.addEndpoint("/clipboardEndpoint");
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.setApplicationDestinationPrefixes("/app");
  }

  @Bean
  public WebSocketStompClient webSocketStompClient() {
    WebSocketStompClient client = new WebSocketStompClient(new StandardWebSocketClient());
    client.start();
    return client;
  }
}
