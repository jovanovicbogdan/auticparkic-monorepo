package com.jovanovicbogdan.auticparkic.ride;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger log = LoggerFactory.getLogger(RideController.class);
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
  public void startRide(@PathVariable final long rideId) {
    log.info("Request to start ride with id: {}", rideId);
    service.startRide(rideId);
  }

  @PostMapping("{rideId}/pause")
  public long pauseRide(@PathVariable final long rideId) {
    log.info("Request to pause ride with id: {}", rideId);
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
