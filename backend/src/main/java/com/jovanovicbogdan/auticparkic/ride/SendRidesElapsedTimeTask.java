package com.jovanovicbogdan.auticparkic.ride;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SendRidesElapsedTimeTask implements Runnable {

  private final Logger log = LoggerFactory.getLogger(SendRidesElapsedTimeTask.class);
  public static final String TASK_ID = "sendRidesElapsedTimeTask";
  private final RideService service;
  private final SimpMessagingTemplate simpMessagingTemplate;

  public SendRidesElapsedTimeTask(final RideService service,
      final SimpMessagingTemplate simpMessagingTemplate) {
    this.service = service;
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  @Override
  public void run() {
    final List<RideDTO> rides = service.getAllRidesElapsedTime();
    log.info("Sending rides elapsed time: {}", rides);
    simpMessagingTemplate.convertAndSend("/topic/public", rides);
  }

}
