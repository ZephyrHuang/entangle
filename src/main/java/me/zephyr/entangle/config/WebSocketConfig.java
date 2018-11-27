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
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import static me.zephyr.util.misc.MiscUtil.getOrDefault;

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
   * websocket 客户端，负责传输字符串。
   *
   * 传输文本的最大尺寸优先取配置（单位 KB），未配置则取 {@code org.apache.tomcat.websocket.Constants#DEFAULT_BUFFER_SIZE}。
   */
  @Bean
  public WebSocketStompClient stompClientForString(
      @Qualifier("stringMessageConverter") MessageConverter messageConverter) {
    int maxTextBuffer = getOrDefault(entangleProps.getMaxTextBuffer(), 8) * 1024;//默认 8K

    WebSocketContainer wsContainer = ContainerProvider.getWebSocketContainer();
    wsContainer.setDefaultMaxTextMessageBufferSize(maxTextBuffer);

    WebSocketStompClient client = new WebSocketStompClient(new StandardWebSocketClient(wsContainer));
    client.setMessageConverter(messageConverter);
    client.setInboundMessageSizeLimit(maxTextBuffer);//客户端接收消息的最大尺寸
    client.start();
    return client;
  }

  @Override
  public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
    //registry.setMessageSizeLimit(10 * 1024 * 1024);//服务端接收消息的最大尺寸
  }

  @Bean
  public StringMessageConverter stringMessageConverter() {
    return new StringMessageConverter();
  }
}
