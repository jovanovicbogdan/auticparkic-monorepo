package com.jovanovicbogdan.auticparkic.vehicle;

import static org.assertj.core.api.Assertions.assertThat;

import com.jovanovicbogdan.auticparkic.AbstractTestcontainers;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VehicleJdbcDataAccessServiceTest extends AbstractTestcontainers {
  private VehicleJdbcDataAccessService underTest;
  private final VehicleRowMapper vehicleRowMapper = new VehicleRowMapper();

  @BeforeEach
  void setUp() {
    underTest = new VehicleJdbcDataAccessService(
        getJdbcTemplate(),
        vehicleRowMapper
    );
    underTest.deleteAll();
  }

  @Test
  void createVehicle() {
    // Given
    final Vehicle vehicle = new Vehicle(FAKER.funnyName().name(), true);

    // When
    final Vehicle actual = underTest.create(vehicle);

    // Then
    assertThat(actual.name).isEqualTo(vehicle.name);
    assertThat(actual.isActive).isEqualTo(vehicle.isActive);
    assertThat(actual.vehicleImageId).isNull();
  }

  @Test
  void findAllVehiclesReturnsEmptyIfNoneFound() {
    // Given
    // When
    final List<Vehicle> actual = underTest.findAll();

    // Then
    assertThat(actual).isEmpty();
  }

  @Test
  void findAllVehicles() {
    // Given
    final Vehicle vehicle = new Vehicle(FAKER.funnyName().name(), true);
    underTest.create(vehicle);

    // When
    final List<Vehicle> actual = underTest.findAll();

    // Then
    assertThat(actual).isNotEmpty();
  }

  @Test
  void updateVehicle() {
    // Given
    final Vehicle vehicle = new Vehicle(FAKER.funnyName().name(), true);
    final Vehicle createdVehicle = underTest.create(vehicle);

    // When
    createdVehicle.isActive = false;
    final Vehicle actual = underTest.update(createdVehicle);

    // Then
    assertThat(actual.name).isEqualTo(createdVehicle.name);
    assertThat(actual.isActive).isFalse();
  }

  @Test
  void findVehicleByVehicleId() {
    // Given
    final Vehicle vehicle = new Vehicle(FAKER.funnyName().name(), true);
    underTest.create(vehicle);
    final long vehicleId = underTest.findAll()
        .stream()
        .filter(v -> v.name.equals(vehicle.name))
        .map(v -> v.vehicleId)
        .findFirst()
        .orElseThrow();

    // When
    final Optional<Vehicle> actual = underTest.findById(vehicleId);

    // Then
    assertThat(actual).isPresent().hasValueSatisfying(v -> {
      assertThat(v.name).isEqualTo(vehicle.name);
      assertThat(v.isActive).isEqualTo(vehicle.isActive);
    });
  }

  @Test
  void findByIdReturnsEmptyIfNotFound() {
    // Given
    final long vehicleId = -1L;

    // When
    final Optional<Vehicle> actual = underTest.findById(vehicleId);

    // Then
    assertThat(actual).isEmpty();
  }

  @Test
  void deleteVehicleByVehicleId() {
    // Given
    final Vehicle vehicle = new Vehicle(FAKER.funnyName().name(), true);
    final Vehicle createdVehicle = underTest.create(vehicle);

    // When
    underTest.delete(createdVehicle.vehicleId);
    final Optional<Vehicle> actual = underTest.findById(createdVehicle.vehicleId);

    // Then
    assertThat(actual).isEmpty();
  }

  @Test
  void findVehicleByName() {
    // Given
    final Vehicle vehicle = new Vehicle(FAKER.funnyName().name(), true);
    underTest.create(vehicle);

    // When
    final Optional<Vehicle> actual = underTest.findVehicleByName(vehicle.name);

    // Then
    assertThat(actual).isPresent().hasValueSatisfying(v -> {
      assertThat(v.name).isEqualTo(vehicle.name);
      assertThat(v.isActive).isEqualTo(vehicle.isActive);
    });
  }

  @Test
  void findVehicleByNameReturnsEmptyIfNotFound() {
    // Given
    // Make sure it's a unique name
    final String name = FAKER.funnyName().name().concat(UUID.randomUUID().toString());

    // When
    final Optional<Vehicle> actual = underTest.findVehicleByName(name);

    // Then
    assertThat(actual).isEmpty();
  }

  @Test
  void updateVehicleImageIdForExistingVehicle() {
    // Given
    final Vehicle vehicle = new Vehicle(FAKER.funnyName().name(), true);
    final Vehicle createdVehicle = underTest.create(vehicle);
    final String vehicleImageId = UUID.randomUUID().toString();

    // When
    underTest.updateVehicleImageId(createdVehicle.vehicleId, vehicleImageId);
    final Vehicle actual = underTest.findById(createdVehicle.vehicleId).orElseThrow();

    // Then
    assertThat(actual.name).isEqualTo(createdVehicle.name);
    assertThat(actual.isActive).isEqualTo(createdVehicle.isActive);
    assertThat(actual.vehicleImageId).isEqualTo(vehicleImageId);
  }

  @Test
  void updateVehicleImageIdForNonExistingVehicle() {
    // Given
    final long vehicleId = -1L;
    final String vehicleImageId = UUID.randomUUID().toString();

    // When
    underTest.updateVehicleImageId(vehicleId, vehicleImageId);
    final Optional<Vehicle> actual = underTest.findById(vehicleId);

    // Then
    assertThat(actual).isEmpty();
  }
}
