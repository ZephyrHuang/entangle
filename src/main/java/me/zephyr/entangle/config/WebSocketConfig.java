package me.zephyr.entangle.config;

import me.zephyr.entangle.config.property.EntangleProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  public static final String STOMP_ENDPOINT = "/entangleEndpoint";

  private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);
  @Autowired
  private EntangleProperties entangleProps;

  @Override
  public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
    String allowedOrigins = entangleProps.getAllowedOrigins();
    logger.debug("allowedOrigins={}", allowedOrigins);
    stompEndpointRegistry.addEndpoint(STOMP_ENDPOINT)
        .setAllowedOrigins(allowedOrigins);
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.setApplicationDestinationPrefixes("/app");
    registry.enableSimpleBroker("/queue", "/topic");
  }

  /**
   * websocket 客户端，负责传输字符串
   */
  @Bean
  public WebSocketStompClient stompClientForString(
      @Qualifier("stringMessageConverter") MessageConverter messageConverter) {
    WebSocketStompClient client = new WebSocketStompClient(new StandardWebSocketClient());
    client.setMessageConverter(messageConverter);
    client.start();
    return client;
  }

  @Bean
  public StringMessageConverter stringMessageConverter() {
    return new StringMessageConverter();
  }
}
