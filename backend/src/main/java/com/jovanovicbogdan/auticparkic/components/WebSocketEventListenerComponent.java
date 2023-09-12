package com.jovanovicbogdan.auticparkic.components;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListenerComponent {

  private static final Logger log = LoggerFactory.getLogger(WebSocketEventListenerComponent.class);
  private final Set<String> activeSessions = Collections.synchronizedSet(new HashSet<>());

  @EventListener
  public void handleWebSocketConnectedListener(final SessionConnectedEvent event) {
    final String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
    activeSessions.add(sessionId);
    log.info("Received Session Connected for session: {}", sessionId);
  }

  @EventListener
  public void handleWebSocketDisconnectedListener(final SessionDisconnectEvent event) {
    final String sessionId = event.getSessionId();
    activeSessions.remove(sessionId);
    log.info("Received Session Disconnect for session: {}", sessionId);
  }

  public boolean hasActiveSessions() {
    return !activeSessions.isEmpty();
  }

}
