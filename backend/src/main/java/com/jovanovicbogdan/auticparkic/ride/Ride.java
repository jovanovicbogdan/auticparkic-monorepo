package com.jovanovicbogdan.auticparkic.ride;

import java.time.LocalDateTime;
import java.util.Objects;

public class Ride {

  public Long rideId;
  public RideStatus status;
  public long elapsedTime;
  public LocalDateTime createdAt;
  public LocalDateTime startedAt;
  public LocalDateTime[] pausedAt;
  public LocalDateTime[] resumedAt;
  public LocalDateTime finishedAt;
  public double price;
  public Long vehicleId;

  public Ride() {
  }

  public Ride(final Long rideId, final RideStatus status, final long elapsedTime,
      final LocalDateTime createdAt, final LocalDateTime startedAt, final LocalDateTime[] pausedAt,
      final LocalDateTime[] resumedAt, final LocalDateTime finishedAt, final double price,
      final Long vehicleId) {
    this.rideId = rideId;
    this.status = status;
    this.elapsedTime = elapsedTime;
    this.createdAt = createdAt;
    this.startedAt = startedAt;
    this.pausedAt = pausedAt;
    this.resumedAt = resumedAt;
    this.finishedAt = finishedAt;
    this.price = price;
    this.vehicleId = vehicleId;
  }

  public Ride(final Long vehicleId, final RideStatus status) {
    this.vehicleId = vehicleId;
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Ride ride = (Ride) o;
    return elapsedTime == ride.elapsedTime && Double.compare(price, ride.price) == 0
        && Objects.equals(rideId, ride.rideId) && status == ride.status
        && Objects.equals(createdAt, ride.createdAt) && Objects.equals(finishedAt,
        ride.finishedAt) && Objects.equals(vehicleId, ride.vehicleId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rideId, status, elapsedTime, createdAt, finishedAt, price, vehicleId);
  }
}
