package com.jovanovicbogdan.auticparkic.components;

import java.util.Collections;
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
  private final Set<String> activeSessions = Collections.synchronizedSet(Collections.emptySet());

  @EventListener
  public void handleWebSocketConnectedListener(final SessionConnectedEvent event) {
    final String sessionId = Objects.requireNonNull(
        event.getMessage().getHeaders().get("simpSessionId")).toString();
    activeSessions.add(sessionId);
    log.info("Client successfully connected to the WebSocket");

    // schedule a task to send rides elapsed time
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
