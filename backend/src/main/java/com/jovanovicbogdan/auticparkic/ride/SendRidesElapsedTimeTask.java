package com.jovanovicbogdan.auticparkic.ride;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SendRidesElapsedTimeTask implements Runnable {

  private final Logger log = LoggerFactory.getLogger(SendRidesElapsedTimeTask.class);
  private String taskId;
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
    simpMessagingTemplate.convertAndSend("/topic/public", rides);
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(final String taskId) {
    this.taskId = taskId;
  }

}
