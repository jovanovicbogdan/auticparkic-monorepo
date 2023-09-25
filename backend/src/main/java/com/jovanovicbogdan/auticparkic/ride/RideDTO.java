package com.jovanovicbogdan.auticparkic.ride;

import java.time.LocalDateTime;

public record RideDTO(long rideId, Long vehicleId, RideStatus status, LocalDateTime createdAt,
                      long price, Long elapsedTime, LocalDateTime startedAt,
                      LocalDateTime stoppedAt) {

}
