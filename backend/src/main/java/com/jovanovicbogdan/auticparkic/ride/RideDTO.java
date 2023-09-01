package com.jovanovicbogdan.auticparkic.ride;

import java.time.LocalDateTime;
import java.util.Optional;

public record RideDTO(long rideId, Long vehicleId, RideStatus status, LocalDateTime createdAt,
                      long price, Optional<Long> elapsedTime) {

}
