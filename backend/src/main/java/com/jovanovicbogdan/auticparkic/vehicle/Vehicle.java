package com.jovanovicbogdan.auticparkic.vehicle;

import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.data.annotation.Id;

public class Vehicle {

  @Id
  public Long vehicleId;
  public String name;
  public LocalDateTime createdAt = LocalDateTime.now();
  public String vehicleImageId;
  public boolean isActive = true;

  public Vehicle(final String name, final boolean isActive) {
    this.name = name;
    this.isActive = isActive;
  }

  public Vehicle(final Long vehicleId, final String name, final LocalDateTime createdAt,
      final String vehicleImageId, final boolean isActive) {
    this.vehicleId = vehicleId;
    this.name = name;
    this.createdAt = createdAt;
    this.vehicleImageId = vehicleImageId;
    this.isActive = isActive;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Vehicle vehicle = (Vehicle) o;
    return isActive == vehicle.isActive && Objects.equals(vehicleId, vehicle.vehicleId)
        && Objects.equals(name, vehicle.name) && Objects.equals(createdAt,
        vehicle.createdAt) && Objects.equals(vehicleImageId, vehicle.vehicleImageId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vehicleId, name, createdAt, vehicleImageId, isActive);
  }

  @Override
  public String toString() {
    return "Vehicle{" +
        "vehicleId=" + vehicleId +
        ", name='" + name + '\'' +
        ", createdAt=" + createdAt +
        ", vehicleImageId='" + vehicleImageId + '\'' +
        ", isActive=" + isActive +
        '}';
  }
}
