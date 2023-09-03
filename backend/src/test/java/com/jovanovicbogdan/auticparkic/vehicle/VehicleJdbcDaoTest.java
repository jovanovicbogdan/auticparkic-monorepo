package com.jovanovicbogdan.auticparkic.vehicle;

import static org.assertj.core.api.Assertions.assertThat;

import com.jovanovicbogdan.auticparkic.AbstractTestcontainers;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VehicleJdbcDaoTest extends AbstractTestcontainers {
  private VehicleJdbcDataAccessService underTest;
  private final VehicleRowMapper vehicleRowMapper = new VehicleRowMapper();

  @BeforeEach
  void setUp() {
    underTest = new VehicleJdbcDataAccessService(
        getJdbcTemplate(),
        vehicleRowMapper
    );
  }

  @Test
  void create() {
  }

  @Test
  void update() {
  }

  @Test
  void testFindAllReturnsAllVehicles() {
    // Given
    final Vehicle vehicle = new Vehicle(FAKER.funnyName().name(), true);
    underTest.create(vehicle);

    // When
    final List<Vehicle> actual = underTest.findAll();

    // Then
    assertThat(actual).isNotEmpty();
  }

  @Test
  void testFindByIdReturnsCorrectVehicle() {
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
  void testFindByIdReturnsEmptyIfNotFound() {
    // Given
    final long vehicleId = -1L;

    // When
    final Optional<Vehicle> actual = underTest.findById(vehicleId);

    // Then
    assertThat(actual).isEmpty();
  }

  @Test
  void delete() {
  }

  @Test
  void findVehicleByName() {
  }

  @Test
  void updateVehicleImageId() {
  }
}
