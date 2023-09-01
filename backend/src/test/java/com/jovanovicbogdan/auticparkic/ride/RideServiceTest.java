package com.jovanovicbogdan.auticparkic.ride;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jovanovicbogdan.auticparkic.vehicle.Vehicle;
import com.jovanovicbogdan.auticparkic.vehicle.VehicleJdbcDao;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RideServiceTest {
//
//  @Mock
//  private RideJdbcDao dao;
//
//  @Mock
//  private VehicleJdbcDao vehicleJdbcDao;
//  private RideService underTest;
//  @Mock
//  private RideDTOMapper rideDTOMapper;
//  @Mock
//  private Clock clock;
//
//  private static final ZonedDateTime NOW = ZonedDateTime.of(
//      2023,
//      8,
//      20,
//      12,
//      30,
//      45,
//      0,
//      ZoneId.of("Europe/Belgrade")
//  );
//
//  @BeforeEach
//  void setUp() {
//    lenient().when(clock.getZone()).thenReturn(NOW.getZone());
//    lenient().when(clock.instant()).thenReturn(NOW.toInstant());
//    underTest = new RideService(rideDTOMapper, dao, vehicleJdbcDao, clock);
//  }
//
//  @Test
//  @Tag("unit")
//  void getUnfinishedRides_shouldCallDao() {
//    // when
//    underTest.getUnfinishedRides();
//
//    // then
//    verify(dao).findByStatuses(List.of(RideStatus.RUNNING.name(),
//        RideStatus.PAUSED.name(), RideStatus.STOPPED.name()));
//  }
//
//  @Test
//  @Tag("unit")
//  void canCreateAndStartRide() {
//    // given
//    final long vehicleId = 1L;
//    final Vehicle vehicle = new Vehicle(vehicleId, "vehicle", NOW.toLocalDateTime(),
//        UUID.randomUUID().toString(), true);
//
//    when(vehicleJdbcDao.findById(vehicleId)).thenReturn(Optional.of(vehicle));
//    when(dao.findByVehicleIdAndStatuses(vehicleId,
//        List.of(RideStatus.RUNNING.name(), RideStatus.PAUSED.name(), RideStatus.STOPPED.name())))
//        .thenReturn(Collections.emptyList());
//
////    when(dao.create(any(Ride.class))).thenAnswer(invocation -> {
////      final Ride rideToCreate = invocation.getArgument(0);
////      rideToCreate.rideId = 1L;
////      return rideToCreate;
////    });
//
////      public Ride(final Long rideId, final RideStatus status, final long elapsedTime,
////    final LocalDateTime startedAt, final LocalDateTime finishedAt, final double price,
////    final Long vehicleId) {
//
//    final Ride createdRide = new Ride(
//        3L,
//        RideStatus.RUNNING,
//        0L,
//        NOW.toLocalDateTime(),
//        null,
//        0.0,
//        vehicleId
//    );
//    when(dao.create(any(Ride.class))).thenReturn(createdRide);
//    when(rideDTOMapper.apply(any(Ride.class))).thenReturn(
//        new RideDTO(createdRide.rideId, vehicleId, createdRide.status, createdRide.elapsedTime,
//            Double.valueOf(createdRide.price).longValue()));
//
//    // when
//    final RideDTO result = underTest.createRide(vehicleId);
//    final ArgumentCaptor<Ride> rideArgumentCaptor = ArgumentCaptor.forClass(Ride.class);
//    verify(dao).create(rideArgumentCaptor.capture());
//    final Ride ride = rideArgumentCaptor.getValue();
//    verify(rideDTOMapper).apply(rideArgumentCaptor.capture());
//    final Ride capturedRide = rideArgumentCaptor.getValue();
//
//    // then
//    assertThat(result).isNotNull();
//    assertThat(result.vehicleId()).isEqualTo(ride.vehicleId);
//    assertThat(result.status()).isEqualTo(ride.status);
//    assertThat(result.elapsedTime()).isEqualTo(ride.elapsedTime);
//    assertThat(result.price()).isEqualTo(Double.valueOf(ride.price).longValue());
//    assertThat(capturedRide).isEqualTo(createdRide);
//  }
}
