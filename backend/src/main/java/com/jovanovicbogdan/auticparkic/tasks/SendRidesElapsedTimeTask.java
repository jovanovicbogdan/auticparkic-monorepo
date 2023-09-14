package com.jovanovicbogdan.auticparkic.tasks;

import com.jovanovicbogdan.auticparkic.common.Constants;
import com.jovanovicbogdan.auticparkic.components.WebSocketEventListenerComponent;
import com.jovanovicbogdan.auticparkic.ride.RideDTO;
import com.jovanovicbogdan.auticparkic.ride.RideService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SendRidesElapsedTimeTask implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(SendRidesElapsedTimeTask.class);
  public static final String TASK_ID = "sendRidesElapsedTimeTask";
  private final RideService service;
  private final SimpMessagingTemplate simpMessagingTemplate;
  private final WebSocketEventListenerComponent webSocketEventListenerComponent;

  public SendRidesElapsedTimeTask(final RideService service,
      final SimpMessagingTemplate simpMessagingTemplate,
      final WebSocketEventListenerComponent webSocketEventListenerComponent) {
    this.service = service;
    this.simpMessagingTemplate = simpMessagingTemplate;
    this.webSocketEventListenerComponent = webSocketEventListenerComponent;
  }

  @Override
  public void run() {
    if (webSocketEventListenerComponent.hasActiveSessions()) {
      final List<RideDTO> rides = service.getAllRidesElapsedTime();
      log.debug("Sending rides elapsed time {} to a topic {}", rides,
          Constants.WEBSOCKET_BROKER_PUBLIC);
      simpMessagingTemplate.convertAndSend(Constants.WEBSOCKET_BROKER_PUBLIC, rides);
    } else {
      service.cancelSendRidesElapsedTimeTask();
    }
  }

}
