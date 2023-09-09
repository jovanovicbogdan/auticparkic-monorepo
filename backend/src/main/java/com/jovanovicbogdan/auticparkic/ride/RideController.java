package com.jovanovicbogdan.auticparkic.ride;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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

  public RideController(final RideService service) {
    this.service = service;
  }

  @PostMapping("create")
  public RideDTO createRide(@RequestParam final long vehicleId) {
    log.info("Request to create ride with vehicle id: {}", vehicleId);
    return service.createRide(vehicleId);
  }

  @PostMapping("{rideId}/start")
  public RideDTO startRide(@PathVariable final long rideId) {
    log.info("Request to start ride with id: {}", rideId);
    return service.startRide(rideId);
  }

  @PostMapping("{rideId}/pause")
  public RideDTO pauseRide(@PathVariable final long rideId) {
    log.info("Request to pause ride with id: {}", rideId);
    return service.pauseRide(rideId);
  }

  @PostMapping("{rideId}/stop")
  public RideDTO stopRide(@PathVariable final long rideId) {
    log.info("Request to stop ride with id: {}", rideId);
    return service.stopRide(rideId);
  }

  @PostMapping("{rideId}/restart")
  public RideDTO restartRide(@PathVariable final long rideId) {
    log.info("Request to restart ride with id: {}", rideId);
    return service.restartRide(rideId);
  }

  @PostMapping("{rideId}/finish")
  public RideDTO finishRide(@PathVariable final long rideId) {
    log.info("Request to finish ride with id: {}", rideId);
    return service.finishRide(rideId);
  }

//  @PostMapping("{rideId}")
//  public void updateRideElapsedTime(@PathVariable final long rideId,
//      @RequestParam final long elapsedTime) {
//    log.info("Request to update ride with id: {} and elapsed time: {}", rideId, elapsedTime);
//    service.updateRideElapsedTime(rideId, elapsedTime);
//  }

  @GetMapping("unfinished")
  public List<Ride> getUnfinishedRides() {
    log.info("Request to get unfinished rides");
    return service.getUnfinishedRides();
  }

  @MessageMapping("/rides.getElapsedTime")
  @SendTo("/topic/public")
  public Greeting greeting() throws InterruptedException {
//    Thread.sleep(1000);
    return new Greeting("Received at: " + LocalDateTime.now());
  }

  public record Greeting(String content) {
  }

}
