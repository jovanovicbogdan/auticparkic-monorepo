package com.jovanovicbogdan.auticparkic.ride;

import com.jovanovicbogdan.auticparkic.exception.BadRequestException;
import com.jovanovicbogdan.auticparkic.exception.ResourceNotFoundException;
import com.jovanovicbogdan.auticparkic.vehicle.VehicleJdbcDataAccessService;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RideService {

  private final Logger log = LoggerFactory.getLogger(RideService.class);
  private final RideDTOMapper rideDTOMapper;
  private final RideJdbcDao dao;
  private final VehicleJdbcDataAccessService vehicleJdbcDao;
  private final Clock clock;

  public RideService(final RideDTOMapper rideDTOMapper, final RideJdbcDao dao,
      final VehicleJdbcDataAccessService vehicleJdbcDao, final Clock clock) {
    this.rideDTOMapper = rideDTOMapper;
    this.dao = dao;
    this.vehicleJdbcDao = vehicleJdbcDao;
    this.clock = clock;
  }

  public RideDTO createRide(final long vehicleId) {
    vehicleJdbcDao.findById(vehicleId)
        .map(vehicle -> {
          if (!vehicle.isActive) {
            throw new BadRequestException("Vehicle is not active");
          }
          return vehicle;
        })
        .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
    final List<Ride> rides = dao.findByVehicleIdAndStatuses(vehicleId,
        List.of(RideStatus.CREATED.name(), RideStatus.RUNNING.name(), RideStatus.PAUSED.name(),
            RideStatus.STOPPED.name()));
    if (!rides.isEmpty()) {
      throw new BadRequestException("Vehicle is already in use");
    }
    final Ride ride = new Ride(vehicleId, RideStatus.CREATED);
    final Ride createdRide = dao.create(ride);
    log.info("Created ride: {}", createdRide);

    return rideDTOMapper.apply(createdRide);
  }

  public RideDTO startRide(final long rideId) {
    final Ride ride = findRideIfExistsOrThrow(rideId);

    if (!isCreated(ride) && !isPaused(ride)) {
      throw new BadRequestException("Ride can start only if it's previously created or paused");
    }

    ride.status = RideStatus.RUNNING;
    final Ride startedRide = dao.update(ride);
    log.info("Started ride: {}", startedRide);

    return rideDTOMapper.apply(startedRide);
  }

  public RideDTO pauseRide(final long rideId, final long elapsedTime) {
    final Ride ride = findRideIfExistsOrThrow(rideId);

    if (!isRunning(ride)) {
      throw new BadRequestException(
          "Ride can be paused only if it's previously started or resumed");
    }
    ride.status = RideStatus.PAUSED;
    ride.elapsedTime = elapsedTime;
    ride.price = calculateRidePrice(ride.elapsedTime);
    final Ride pausedRide = dao.update(ride);
    log.info("Paused ride: {}", pausedRide);

    return rideDTOMapper.apply(pausedRide);
  }

  public RideDTO stopRide(final long rideId, final long elapsedTime) {
    final Ride ride = findRideIfExistsOrThrow(rideId);

    if (isStopped(ride) || isFinished(ride)) {
      throw new BadRequestException("Ride is not active");
    }

    ride.status = RideStatus.STOPPED;
    ride.elapsedTime = elapsedTime;
    ride.price = calculateRidePrice(ride.elapsedTime);
    final Ride stoppedRide = dao.update(ride);
    log.info("Stopped ride: {}", stoppedRide);

    return rideDTOMapper.apply(stoppedRide);
  }

  public RideDTO restartRide(final long rideId) {
    final RideDTO finishedRideDTO = finishRide(rideId);

    final List<Ride> rides = dao.findByVehicleIdAndStatuses(finishedRideDTO.vehicleId(),
        List.of(RideStatus.CREATED.name(), RideStatus.RUNNING.name(), RideStatus.PAUSED.name(),
            RideStatus.STOPPED.name()));

    if (!rides.isEmpty()) {
      throw new BadRequestException("Vehicle is already in use");
    }

    final Ride ride = new Ride(finishedRideDTO.vehicleId(), RideStatus.CREATED);
    final Ride restartedRide = dao.create(ride);
    log.info("Restarted/finished ride id '{}', created ride: {}", rideId, restartedRide);

    return rideDTOMapper.apply(restartedRide);
  }

  public RideDTO finishRide(final long rideId) {
    final Ride ride = findRideIfExistsOrThrow(rideId);

    if (ride.status != RideStatus.STOPPED) {
      throw new BadRequestException("Ride is not stopped");
    }

    ride.status = RideStatus.FINISHED;
    ride.finishedAt = LocalDateTime.now(clock);
    ride.price = calculateRidePrice(ride.elapsedTime);
    final Ride updatedRide = dao.update(ride);
    log.info("Updated ride: {}", updatedRide);

    return rideDTOMapper.apply(updatedRide);
  }

  public List<Ride> getUnfinishedRides() {
    return dao.findByStatuses(
        List.of(RideStatus.CREATED.name(), RideStatus.RUNNING.name(), RideStatus.PAUSED.name(),
            RideStatus.STOPPED.name()));
  }

  public void updateRideElapsedTime(final long rideId, final long elapsedTime) {
    final Ride ride = findRideIfExistsOrThrow(rideId);

    if (ride.status == RideStatus.FINISHED) {
      throw new BadRequestException("Ride is finished");
    }

    if (Math.abs(ride.elapsedTime - elapsedTime) > 10) {
      throw new BadRequestException("Ride's elapsed time is not valid");
    }

    ride.elapsedTime = elapsedTime;
    final Ride updatedRide = dao.update(ride);
    log.info("Updated ride elapsed time: {}", updatedRide);
  }

  private double calculateRidePrice(final long elapsedTime) {
    final Map<String, Long> elapsedTimeCalculated = convertSecondsToTimeComponents(elapsedTime);
    final int pricePerMinute = 40;
    final double seconds = elapsedTimeCalculated.get("seconds");
    final double minutes = seconds > 10
        ? elapsedTimeCalculated.get("minutes") + 1
        : elapsedTimeCalculated.get("minutes");

    return pricePerMinute * minutes;
  }

  private Map<String, Long> convertSecondsToTimeComponents(final long elapsedTime) {
    long totalSeconds = Duration.ofSeconds(elapsedTime).getSeconds();
    final long hours = totalSeconds / 3600;
    totalSeconds %= 3600;
    final long minutes = totalSeconds / 60;
    final long seconds = totalSeconds % 60;

    return Map.of("hours", hours, "minutes", minutes, "seconds", seconds);
  }

  private Ride findRideIfExistsOrThrow(final long rideId) {
    return dao.findById(rideId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Ride with id '" + rideId + "' not found"));
  }

  private boolean isCreated(final Ride ride) {
    return ride.status == RideStatus.CREATED;
  }

  private boolean isRunning(final Ride ride) {
    return ride.status == RideStatus.RUNNING;
  }

  private boolean isPaused(final Ride ride) {
    return ride.status == RideStatus.PAUSED;
  }

  private boolean isStopped(final Ride ride) {
    return ride.status == RideStatus.STOPPED;
  }

  private boolean isFinished(final Ride ride) {
    return ride.status == RideStatus.FINISHED;
  }
}
