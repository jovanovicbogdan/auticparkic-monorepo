package com.jovanovicbogdan.auticparkic.ride;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rides")
@CrossOrigin
public class RideController {

  private static final Logger log = LoggerFactory.getLogger(RideController.class);
  private final RideService service;

  public RideController(final RideService service) {
    this.service = service;
  }

  @MessageMapping("/rides.create")
  @SendTo("/topic/public")
  public RideDTO createRide(@Payload final CreateRidePayload payload) {
    log.info("Request to create ride with vehicle id: {}", payload.vehicleId);
    return service.createRide(payload.vehicleId);
  }

  @MessageMapping("/rides.start")
//  @SendTo("/topic/public")
  public void startRide(@Payload final ManageRidePayload payload) {
    log.info("Request to start ride with id: {}", payload.rideId);
    service.startRide(payload.rideId);
  }

  @MessageMapping("/rides.pause")
  @SendTo("/topic/public")
  public long pauseRide(@Payload final ManageRidePayload payload) {
    log.info("Request to pause ride with id: {}", payload.rideId);
    return service.pauseRide(payload.rideId);
  }

  @MessageMapping("/rides.stop")
//  @SendTo("/topic/public")
  public void stopRide(@Payload final ManageRidePayload payload) {
    log.info("Request to stop ride with id: {}", payload.rideId);
    service.stopRide(payload.rideId);
  }

  @MessageMapping("/rides.restart")
  @SendTo("/topic/public")
  public long restartRide(@Payload final ManageRidePayload payload) {
    log.info("Request to restart ride with id: {}", payload.rideId);
    return service.restartRide(payload.rideId);
  }

  @MessageMapping("/rides.finish")
//  @SendTo("/topic/public")
  public void finishRide(@Payload final ManageRidePayload payload) {
    log.info("Request to finish ride with id: {}", payload.rideId);
    service.finishRide(payload.rideId);
  }

  @MessageMapping("/rides.scheduleStreamingRidesElapsedTimeIfEligible")
  @SendTo("/topic/public")
  public void scheduleStreamingRidesElapsedTimeIfEligible() {
    log.info("Request to get unfinished rides");
    service.scheduleStreamingRidesElapsedTimeIfEligible();
  }

  public record CreateRidePayload(long vehicleId) { }
  public record ManageRidePayload(long rideId) { }

}
