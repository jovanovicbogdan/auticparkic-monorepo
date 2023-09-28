package com.jovanovicbogdan.auticparkic.ride;

import com.jovanovicbogdan.auticparkic.components.WebSocketEventListenerComponent;
import com.jovanovicbogdan.auticparkic.exception.BadRequestException;
import com.jovanovicbogdan.auticparkic.exception.ResourceNotFoundException;
import com.jovanovicbogdan.auticparkic.tasks.CustomTaskScheduler;
import com.jovanovicbogdan.auticparkic.tasks.StreamRidesDataTask;
import com.jovanovicbogdan.auticparkic.vehicle.VehicleJdbcDataAccessService;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RideService {

  private static final Logger log = LoggerFactory.getLogger(RideService.class);
  private final RideDTOMapper rideDTOMapper;
  private final RideJdbcDataAccessService dao;
  private final VehicleJdbcDataAccessService vehicleJdbcDao;
  private final CustomTaskScheduler taskScheduler;
  private final SimpMessagingTemplate simpMessagingTemplate;
  private final WebSocketEventListenerComponent webSocketEventListenerComponent;
  private final Clock clock;
  private List<Ride> unfinishedRides = new ArrayList<>();

  public RideService(final RideDTOMapper rideDTOMapper, final RideJdbcDataAccessService dao,
      final VehicleJdbcDataAccessService vehicleJdbcDao, final CustomTaskScheduler taskScheduler,
      final SimpMessagingTemplate simpMessagingTemplate,
      final WebSocketEventListenerComponent webSocketEventListenerComponent, final Clock clock) {
    this.rideDTOMapper = rideDTOMapper;
    this.dao = dao;
    this.vehicleJdbcDao = vehicleJdbcDao;
    this.taskScheduler = taskScheduler;
    this.simpMessagingTemplate = simpMessagingTemplate;
    this.webSocketEventListenerComponent = webSocketEventListenerComponent;
    this.clock = clock;
  }

  @Transactional
  public List<RideDTO> createRide(final long vehicleId) {
    vehicleJdbcDao.findById(vehicleId)
        .map(vehicle -> {
          if (!vehicle.isActive) {
            throw new BadRequestException("Vehicle is not active");
          }
          return vehicle;
        })
        .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

    throwIfVehicleInUse(vehicleId);

    final Ride ride = new Ride(vehicleId, RideStatus.CREATED);
    final Ride createdRide = dao.create(ride);
    log.info("Created ride: {}", createdRide);

    setUnfinishedRides();

    return getUnfinishedRidesWithCalculatedElapsedTime();
  }

  @Transactional
  public void startRide(final long rideId) {
    final Ride ride = findRideIfExistsOrThrow(rideId);

    if (!isCreated(ride) && !isPaused(ride)) {
      throw new BadRequestException("Ride can be started only if it's created or paused");
    }

    ride.status = RideStatus.RUNNING;

    if (ride.startedAt == null) {
      ride.startedAt = LocalDateTime.now(clock);
    } else {
      if (ride.resumedAt == null) {
        ride.resumedAt = new LocalDateTime[1];
        ride.resumedAt[0] = LocalDateTime.now(clock);
      } else {
        final LocalDateTime[] resumedAtTimestamps = new LocalDateTime[ride.resumedAt.length + 1];
        System.arraycopy(ride.resumedAt, 0, resumedAtTimestamps, 0, ride.resumedAt.length);
        resumedAtTimestamps[resumedAtTimestamps.length - 1] = LocalDateTime.now(clock);
        ride.resumedAt = resumedAtTimestamps;
      }
    }

    final boolean isUpdated = dao.update(ride);

    if (!isUpdated) {
      throw new RuntimeException("Failed to start ride");
    }

    log.info("Ride with id '{}' started", rideId);

    setUnfinishedRides();

    scheduleStreamRidesDataTaskIfEligible();
  }

  @Transactional
  public List<RideDTO> pauseRide(final long rideId) {
    final Ride ride = findRideIfExistsOrThrow(rideId);

    if (!isRunning(ride)) {
      throw new BadRequestException(
          "Ride can be paused only if it's previously started or resumed");
    }

    ride.status = RideStatus.PAUSED;
    ride.price = calculateRidePrice(ride.elapsedTime);

    if (ride.pausedAt == null) {
      ride.pausedAt = new LocalDateTime[1];
      ride.pausedAt[0] = LocalDateTime.now(clock);
    } else {
      final LocalDateTime[] pausedAtTimestamps = new LocalDateTime[ride.pausedAt.length + 1];
      System.arraycopy(ride.pausedAt, 0, pausedAtTimestamps, 0, ride.pausedAt.length);
      pausedAtTimestamps[pausedAtTimestamps.length - 1] = LocalDateTime.now(clock);
      ride.pausedAt = pausedAtTimestamps;
    }

    ride.elapsedTime = getRideElapsedTime(ride);
    final boolean isUpdated = dao.update(ride);

    if (!isUpdated) {
      throw new RuntimeException("Failed to pause ride");
    }

    log.info("Ride with id '{}' paused", rideId);

    setUnfinishedRides();

    cancelStreamRidesDataTask();

    return getUnfinishedRidesWithCalculatedElapsedTime();
  }

  @Transactional
  public List<RideDTO> stopRide(final long rideId) {
    final Ride ride = findRideIfExistsOrThrow(rideId);

    if (isStopped(ride) || isFinished(ride)) {
      throw new BadRequestException("Ride is not active");
    }

    ride.status = RideStatus.STOPPED;
    ride.elapsedTime = getRideElapsedTime(ride);
    ride.price = calculateRidePrice(ride.elapsedTime);
    ride.stoppedAt = LocalDateTime.now(clock);
    final boolean isUpdated = dao.update(ride);

    if (!isUpdated) {
      throw new RuntimeException("Failed to stop ride");
    }

    log.info("Ride with id '{}' stopped", rideId);

    setUnfinishedRides();

    cancelStreamRidesDataTask();

    return getUnfinishedRidesWithCalculatedElapsedTime();
  }

  @Transactional
  public List<RideDTO> finishRide(final long rideId) {
    final Ride ride = findRideIfExistsOrThrow(rideId);

    if (ride.status != RideStatus.STOPPED) {
      throw new BadRequestException("Ride is not stopped");
    }

    ride.status = RideStatus.FINISHED;
    ride.finishedAt = LocalDateTime.now(clock);
    ride.price = calculateRidePrice(ride.elapsedTime);
    final boolean isUpdated = dao.update(ride);

    if (!isUpdated) {
      throw new RuntimeException("Failed to finish ride");
    }

    log.info("Ride with id '{}' finished", rideId);

    setUnfinishedRides();

    return getUnfinishedRidesWithCalculatedElapsedTime();
  }

  @Transactional
  public List<RideDTO> restartRide(final long rideId) {
    final long vehicleId = getUnfinishedRidesWithCalculatedElapsedTime()
        .stream()
        .filter(ride -> ride.rideId() == rideId)
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("Ride not found"))
        .vehicleId();
    finishRide(rideId);
    throwIfVehicleInUse(vehicleId);

    final Ride ride = new Ride(vehicleId, RideStatus.CREATED);
    final Ride restartedRide = dao.create(ride);
    log.info("Finished and restarted ride with id '{}', and created a new one with id {}", rideId,
        restartedRide.rideId);

    setUnfinishedRides();

    scheduleStreamRidesDataTaskIfEligible();

    return getUnfinishedRidesWithCalculatedElapsedTime();
  }

  public List<RideDTO> getUnfinishedRidesWithCalculatedElapsedTime() {
    return unfinishedRides.stream()
        .map(ride -> {
          if (!isStopped(ride)) {
            ride.elapsedTime = getRideElapsedTime(ride);
          }
          return rideDTOMapper.apply(ride);
        }).toList();
  }

  public List<RideDTO> scheduleStreamRidesDataTaskIfEligible() {
    if (shouldScheduleStreamRidesDataTask() && !taskScheduler.isTaskRunning(
        StreamRidesDataTask.TASK_ID)) {
      taskScheduler.scheduleAtFixedRate(
          new StreamRidesDataTask(this, simpMessagingTemplate,
              webSocketEventListenerComponent),
          Duration.ofSeconds(1L), StreamRidesDataTask.TASK_ID);

      if (!taskScheduler.isTaskScheduled(StreamRidesDataTask.TASK_ID)) {
        log.warn("Failed to schedule task with id: {}", StreamRidesDataTask.TASK_ID);
        throw new RuntimeException("Failed to schedule task");
      }
    }

    setUnfinishedRides();

    return getUnfinishedRidesWithCalculatedElapsedTime();
  }

  public void cancelStreamRidesDataTask() {
    if (!areThereAnyRunningRides() || !webSocketEventListenerComponent.hasActiveSessions()) {
      taskScheduler.cancelScheduledTask(StreamRidesDataTask.TASK_ID);
    }
  }

 private void setUnfinishedRides() {
   unfinishedRides = dao.findByStatuses(
       List.of(RideStatus.CREATED.name(), RideStatus.RUNNING.name(), RideStatus.PAUSED.name(),
           RideStatus.STOPPED.name()));
 }

  private boolean shouldScheduleStreamRidesDataTask() {
    return areThereAnyRunningRides() && webSocketEventListenerComponent.hasActiveSessions();
  }

  private boolean areThereAnyRunningRides() {
    return !dao.findByStatuses(
        List.of(RideStatus.RUNNING.name())).isEmpty();
  }

  private long getRideElapsedTime(final Ride ride) {
    if (isFinished(ride)) {
      throw new BadRequestException("Ride is not active");
    }

    if (isCreated(ride)) {
      return 0L;
    }

    final LocalDateTime[] pausedAt = ride.pausedAt != null ? ride.pausedAt : new LocalDateTime[0];
    final LocalDateTime[] resumedAt =
        ride.resumedAt != null ? ride.resumedAt : new LocalDateTime[0];

    return calculateRideElapsedTime(ride.startedAt, pausedAt, resumedAt,
        LocalDateTime.now(clock)).getSeconds();
  }

  private Duration calculateRideElapsedTime(final LocalDateTime startedAt,
      final LocalDateTime[] pausedAt, final LocalDateTime[] resumedAt,
      final LocalDateTime currentTimestamp) {
    Duration elapsedTime = Duration.ZERO;

    // Calculate the time between start and first pause
    if (pausedAt.length > 0) {
      elapsedTime = elapsedTime.plus(Duration.between(startedAt, pausedAt[0]));
    } else {
      return Duration.between(startedAt, currentTimestamp);
    }

    // Calculate the time between each resume and next pause
    for (int i = 0; i < resumedAt.length; i++) {
      final LocalDateTime resumeTime = resumedAt[i];
      final LocalDateTime nextPauseTime =
          (i < pausedAt.length - 1) ? pausedAt[i + 1] : currentTimestamp;
      elapsedTime = elapsedTime.plus(Duration.between(resumeTime, nextPauseTime));
    }

    return elapsedTime;
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

  private void throwIfVehicleInUse(final long vehicleId) {
    final List<Ride> rides = dao.findByVehicleIdAndStatuses(vehicleId,
        List.of(RideStatus.CREATED.name(), RideStatus.RUNNING.name(), RideStatus.PAUSED.name(),
            RideStatus.STOPPED.name()));

    if (!rides.isEmpty()) {
      throw new BadRequestException("Vehicle is already in use");
    }
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
