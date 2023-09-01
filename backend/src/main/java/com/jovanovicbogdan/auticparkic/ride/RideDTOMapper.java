package com.jovanovicbogdan.auticparkic.ride;

import java.util.Optional;
import java.util.function.Function;
import org.springframework.stereotype.Service;

@Service
public class RideDTOMapper implements Function<Ride, RideDTO> {

  @Override
  public RideDTO apply(final Ride ride) {
    return new RideDTO(ride.rideId, ride.vehicleId, ride.status, ride.createdAt,
        Math.round(ride.price), Optional.of(ride.elapsedTime));
  }
}
