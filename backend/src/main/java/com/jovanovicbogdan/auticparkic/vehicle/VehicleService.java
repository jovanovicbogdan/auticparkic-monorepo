package com.jovanovicbogdan.auticparkic.vehicle;

import com.jovanovicbogdan.auticparkic.exception.ConflictException;
import com.jovanovicbogdan.auticparkic.exception.ResourceNotFoundException;
import com.jovanovicbogdan.auticparkic.ride.RideJdbcDao;
import com.jovanovicbogdan.auticparkic.ride.RideStatus;
import com.jovanovicbogdan.auticparkic.s3.S3Buckets;
import com.jovanovicbogdan.auticparkic.s3.S3Service;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VehicleService {

  private final Logger log = LoggerFactory.getLogger(VehicleService.class);
  private final VehicleJdbcDataAccessService dao;
  private final RideJdbcDao rideJdbcDao;
  private final S3Service s3Service;
  private final S3Buckets s3Buckets;

  public VehicleService(final VehicleJdbcDataAccessService dao, final RideJdbcDao rideJdbcDao,
      final S3Service s3Service, final S3Buckets s3Buckets) {
    this.dao = dao;
    this.rideJdbcDao = rideJdbcDao;
    this.s3Service = s3Service;
    this.s3Buckets = s3Buckets;
  }

  public Vehicle getVehicleById(final long vehicleId) {
    return findVehicleIfExistsOrThrow(vehicleId);
  }

  public long createVehicleIfNotExists(final VehicleRequestDTO request) {
    dao.findVehicleByName(request.vehicleName().toUpperCase()).ifPresent(vehicle -> {
      throw new ConflictException("Vehicle with name '" + vehicle.name + "' already exists.");
    });
    final Vehicle createdVehicle = dao.create(
        new Vehicle(request.vehicleName().toUpperCase(), request.isActive()));
    log.info("Created vehicle: {}", createdVehicle);

    return createdVehicle.vehicleId;
  }

  public void updateVehicle(final long vehicleId, final VehicleRequestDTO request) {
    final Vehicle vehicle = findVehicleIfExistsOrThrow(vehicleId);
    vehicle.name = request.vehicleName().toUpperCase();
    vehicle.isActive = request.isActive();
    final Vehicle updatedVehicle = dao.update(vehicle);
    log.info("Updated vehicle: {}", updatedVehicle);
  }

  public List<Vehicle> getAvailableVehicles() {
    final List<Long> activeVehicleIds = rideJdbcDao.findByStatuses(
            List.of(RideStatus.CREATED.name(), RideStatus.RUNNING.name(), RideStatus.PAUSED.name(),
                RideStatus.STOPPED.name()))
        .stream()
        .map(ride -> ride.vehicleId)
        .toList();

    return dao.findAll()
        .stream()
        .filter(vehicle -> !activeVehicleIds.contains(vehicle.vehicleId) && vehicle.isActive)
        .toList();
  }

  public void uploadVehicleImage(final long vehicleId, final MultipartFile file) {
    findVehicleIfExistsOrThrow(vehicleId);
    final String vehicleImageId = UUID.randomUUID().toString();

    try {
      s3Service.putObject(s3Buckets.getVehicles(),
          "vehicle-images/%s/%s".formatted(vehicleId, vehicleImageId),
          file.getBytes());
      log.info("Uploaded vehicle image with id: {}", vehicleImageId);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    dao.updateVehicleImageId(vehicleId, vehicleImageId);
  }

  public byte[] getVehicleImage(final long vehicleId) {
    final var vehicle = findVehicleIfExistsOrThrow(vehicleId);

    var vehicleImageId = vehicle.vehicleImageId;

    if (vehicleImageId == null || vehicleImageId.isBlank()) {
      throw new ResourceNotFoundException("Vehicle image not found.");
    }

    return s3Service.getObject(
        s3Buckets.getVehicles(),
        "vehicle-images/%s/%s".formatted(vehicleId, vehicleImageId)
    );
  }

  private Vehicle findVehicleIfExistsOrThrow(long vehicleId) {
    return dao.findById(vehicleId).orElseThrow(
        () -> new ResourceNotFoundException("Vehicle with id '" + vehicleId + "' does not exist."));
  }

}
