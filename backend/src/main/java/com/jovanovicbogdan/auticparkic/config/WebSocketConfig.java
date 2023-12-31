package com.jovanovicbogdan.auticparkic.config;

import com.jovanovicbogdan.auticparkic.common.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(final MessageBrokerRegistry registry) {
    registry.enableSimpleBroker(Constants.WEBSOCKET_BROKER);
    registry.setApplicationDestinationPrefixes(Constants.WEBSOCKET_APPLICATION_DESTINATION_PREFIX);
  }

  @Override
  public void registerStompEndpoints(final StompEndpointRegistry registry) {
    registry.addEndpoint(Constants.WEBSOCKET_ENDPOINT).setAllowedOriginPatterns("*");
  }

}
