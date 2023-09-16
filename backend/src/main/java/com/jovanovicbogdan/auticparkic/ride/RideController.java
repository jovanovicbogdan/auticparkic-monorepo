package com.jovanovicbogdan.auticparkic.ride;

import java.util.List;
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
  public List<RideDTO> createRide(@Payload final VehicleIdPayload payload) {
    log.info("Request to create ride with vehicle id: {}", payload.vehicleId);
    return service.createRide(payload.vehicleId);
  }

  @MessageMapping("/rides.start")
//  @SendTo("/topic/public")
  public void startRide(@Payload final RideIdPayload payload) {
    log.info("Request to start ride with id: {}", payload.rideId);
    service.startRide(payload.rideId);
  }

  @MessageMapping("/rides.pause")
  @SendTo("/topic/public")
  public List<RideDTO> pauseRide(@Payload final RideIdPayload payload) {
    log.info("Request to pause ride with id: {}", payload.rideId);
    return service.pauseRide(payload.rideId);
  }

  @MessageMapping("/rides.stop")
//  @SendTo("/topic/public")
  public List<RideDTO> stopRide(@Payload final RideIdPayload payload) {
    log.info("Request to stop ride with id: {}", payload.rideId);
    return service.stopRide(payload.rideId);
  }

  @MessageMapping("/rides.restart")
  @SendTo("/topic/public")
  public List<RideDTO> restartRide(@Payload final RideIdPayload payload) {
    log.info("Request to restart ride with id: {}", payload.rideId);
    return service.restartRide(payload.rideId);
  }

  @MessageMapping("/rides.finish")
//  @SendTo("/topic/public")
  public List<RideDTO> finishRide(@Payload final RideIdPayload payload) {
    log.info("Request to finish ride with id: {}", payload.rideId);
    return service.finishRide(payload.rideId);
  }

  @MessageMapping("/rides.streamUnfinishedRidesData")
  @SendTo("/topic/public")
  public List<RideDTO> streamUnfinishedRidesData() {
    log.info("Request to stream unfinished rides");
    return service.scheduleStreamRidesDataTaskIfEligible();
  }

  public record VehicleIdPayload(long vehicleId) { }
  public record RideIdPayload(long rideId) { }

}
