package me.zephyr.entangle.transport.stompsessionhandler;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

@Component
public class ConnectionHandler extends AbstractStompSessionHandler {
  @Override
  public void handleFrame(StompHeaders headers, Object payload) {
    //pass
  }
}
