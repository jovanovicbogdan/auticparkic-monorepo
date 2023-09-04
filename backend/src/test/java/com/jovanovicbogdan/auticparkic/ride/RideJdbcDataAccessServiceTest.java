package com.jovanovicbogdan.auticparkic.ride;

import static org.assertj.core.api.Assertions.assertThat;

import com.jovanovicbogdan.auticparkic.AbstractTestcontainers;
import com.jovanovicbogdan.auticparkic.vehicle.Vehicle;
import com.jovanovicbogdan.auticparkic.vehicle.VehicleJdbcDataAccessService;
import com.jovanovicbogdan.auticparkic.vehicle.VehicleRowMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class RideJdbcDataAccessServiceTest extends AbstractTestcontainers {

  private RideJdbcDataAccessService underTest;
  private final RideRowMapper rideRowMapper = new RideRowMapper();
  private VehicleJdbcDataAccessService vehicleJdbcDataAccessService;

  @BeforeEach
  void setUp() {
    underTest = new RideJdbcDataAccessService(
        getJdbcTemplate(),
        rideRowMapper
    );
    vehicleJdbcDataAccessService = new VehicleJdbcDataAccessService(
        getJdbcTemplate(),
        new VehicleRowMapper()
    );
    underTest.deleteAll();
  }

  @Test
  @Tag("unit")
  void createRide() {
    // Given
    final Vehicle vehicle = createVehicle();
    final LocalDateTime createdAt = LocalDateTime.of(2021, 8, 20, 12, 30, 45);
    final Ride ride = new Ride(null, RideStatus.RUNNING, 0L, createdAt, null, 0.0,
        vehicle.vehicleId);

    // When
    final Ride actual = underTest.create(ride);

    // Then
    assertThat(actual.rideId).isNotNull();
    assertThat(actual.status).isEqualTo(ride.status);
    assertThat(actual.elapsedTime).isEqualTo(ride.elapsedTime);
    assertThat(actual.createdAt).isEqualTo(ride.createdAt);
    assertThat(actual.finishedAt).isEqualTo(ride.finishedAt);
    assertThat(actual.price).isEqualTo(ride.price);
    assertThat(actual.vehicleId).isEqualTo(ride.vehicleId);
  }

  @Test
  @Tag("unit")
  void update() {
  }

  @Test
  @Tag("unit")
  void findAll() {
  }

  @Test
  @Tag("unit")
  void findById() {
  }

  @Test
  @Tag("unit")
  void delete() {
  }

  @Test
  @Tag("unit")
  void findByStatuses() {
  }

  @Test
  @Tag("unit")
  void findByVehicleIdAndStatuses() {
  }

  private Vehicle createVehicle() {
    final Vehicle vehicle = new Vehicle(1L, FAKER.name().name(), LocalDateTime.now(), null, true);
    return vehicleJdbcDataAccessService.create(vehicle);
  }

  private Vehicle createVehicle(final Vehicle vehicle) {
    return vehicleJdbcDataAccessService.create(vehicle);
  }
}