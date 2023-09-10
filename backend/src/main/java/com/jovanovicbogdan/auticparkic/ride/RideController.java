package com.jovanovicbogdan.auticparkic.ride;

import com.jovanovicbogdan.auticparkic.config.CustomTaskScheduler;
import java.time.Duration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rides")
@CrossOrigin
public class RideController {

  private final Logger log = LoggerFactory.getLogger(RideController.class);
  private final RideService service;
  private final CustomTaskScheduler taskScheduler;
  private final SimpMessagingTemplate simpMessagingTemplate;

  public RideController(final RideService service, final CustomTaskScheduler taskScheduler,
      final SimpMessagingTemplate simpMessagingTemplate) {
    this.service = service;
    this.taskScheduler = taskScheduler;
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  @PostMapping("create")
  public RideDTO createRide(@RequestParam final long vehicleId) {
    log.info("Request to create ride with vehicle id: {}", vehicleId);
    return service.createRide(vehicleId);
  }

  @PostMapping("{rideId}/start")
  public void startRide(@PathVariable final long rideId) {
    log.info("Request to start ride with id: {}", rideId);

    boolean isTaskRunning = taskScheduler.isTaskRunning(SendRidesElapsedTimeTask.TASK_ID);

    if (!isTaskRunning) {
      taskScheduler.scheduleAtFixedRate(
          new SendRidesElapsedTimeTask(service, simpMessagingTemplate),
          Duration.ofSeconds(1L), SendRidesElapsedTimeTask.TASK_ID);

      if (!taskScheduler.isTaskScheduled(SendRidesElapsedTimeTask.TASK_ID)) {
        log.warn("Failed to schedule task with id: {}", rideId);
        throw new RuntimeException("Failed to schedule task");
      }
    }

    service.startRide(rideId);
  }

  @PostMapping("{rideId}/pause")
  public long pauseRide(@PathVariable final long rideId) {
    log.info("Request to pause ride with id: {}", rideId);

    final boolean areThereAnyRunningRides = service.areThereAnyRunningRides();

    // if there are no running rides, cancel the task (stop sending elapsed time to a topic)
    if (!areThereAnyRunningRides) {
      final boolean isCancelled = taskScheduler.cancelScheduledTask(
          SendRidesElapsedTimeTask.TASK_ID);

      if (!isCancelled) {
        log.warn("Failed to cancel task with id: {}", rideId);
        throw new RuntimeException("Failed to cancel task");
      }
    }

    return service.pauseRide(rideId);
  }

  @PostMapping("{rideId}/stop")
  public void stopRide(@PathVariable final long rideId) {
    log.info("Request to stop ride with id: {}", rideId);
    service.stopRide(rideId);
  }

  @PostMapping("{rideId}/restart")
  public long restartRide(@PathVariable final long rideId) {
    log.info("Request to restart ride with id: {}", rideId);
    return service.restartRide(rideId);
  }

  @PostMapping("{rideId}/finish")
  public void finishRide(@PathVariable final long rideId) {
    log.info("Request to finish ride with id: {}", rideId);
    service.finishRide(rideId);
  }

  @GetMapping("unfinished")
  public List<Ride> getUnfinishedRides() {
    log.info("Request to get unfinished rides");
    return service.getUnfinishedRides();
  }

}
