package com.jovanovicbogdan.auticparkic.vehicle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jovanovicbogdan.auticparkic.ride.Ride;
import com.jovanovicbogdan.auticparkic.ride.RideJdbcDao;
import com.jovanovicbogdan.auticparkic.ride.RideStatus;
import com.jovanovicbogdan.auticparkic.s3.S3Buckets;
import com.jovanovicbogdan.auticparkic.s3.S3Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {
//
//  @Mock
//  private VehicleJdbcDao dao;
//  private VehicleService underTest;
//  @Mock
//  private RideJdbcDao rideJdbcDao;
//  @Mock
//  private S3Service s3Service;
//  @Mock
//  private S3Buckets s3Buckets;
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
//
//  @BeforeEach
//  public void setUp() {
//    underTest = new VehicleService(dao, rideJdbcDao, s3Service, s3Buckets);
//  }
//
//  @Test
//  @Tag("unit")
//  public void canCreateVehicle() {
//    // given
//    final VehicleRequestDTO request = new VehicleRequestDTO("vehicleName", true);
//    when(dao.findVehicleByName(request.vehicleName().toUpperCase())).thenReturn(Optional.empty());
//    when(dao.create(any(Vehicle.class))).thenReturn(
//        new Vehicle(1L, "VEHICLENAME", LocalDateTime.now(), UUID.randomUUID().toString(), true));
//
//    // when
//    underTest.createVehicleIfNotExists(request);
//
//    // then
//    final ArgumentCaptor<Vehicle> vehicleArgumentCaptor = ArgumentCaptor.forClass(Vehicle.class);
//    verify(dao).create(vehicleArgumentCaptor.capture());
//    final Vehicle vehicle = vehicleArgumentCaptor.getValue();
//
//    assertThat(vehicle.name).isEqualTo(request.vehicleName().toUpperCase());
//    assertThat(vehicle.isActive).isEqualTo(request.isActive());
//  }
//
//  @Test
//  @Tag("unit")
//  public void canUpdateVehicle() {
//    // given
//    final long vehicleId = 1L;
//    final String newVehicleName = "updatedVehicleName";
//    final boolean newVehicleStatus = false;
//    final VehicleRequestDTO request = new VehicleRequestDTO(newVehicleName, newVehicleStatus);
//    final Vehicle vehicleToUpdate = new Vehicle(vehicleId, "vehicleName", LocalDateTime.now(),
//        UUID.randomUUID().toString(), true);
//    when(dao.findById(vehicleId)).thenReturn(Optional.of(vehicleToUpdate));
//    when(dao.update(any(Vehicle.class))).thenReturn(
//        new Vehicle(vehicleId, newVehicleName, LocalDateTime.now(),
//            UUID.randomUUID().toString(), newVehicleStatus));
//
//    // when
//    underTest.updateVehicle(vehicleId, request);
//
//    // then
//    final ArgumentCaptor<Vehicle> vehicleArgumentCaptor = ArgumentCaptor.forClass(Vehicle.class);
//    verify(dao).update(vehicleArgumentCaptor.capture());
//    final Vehicle updatedVehicle = vehicleArgumentCaptor.getValue();
//
//    assertThat(updatedVehicle.name).isEqualTo(request.vehicleName().toUpperCase());
//    assertThat(updatedVehicle.isActive).isEqualTo(request.isActive());
//  }
//
//  @Test
//  @Tag("unit")
//  public void canGetAvailableVehicles() {
//    // given
//    final Vehicle vehicle1 = new Vehicle(1L, "vehicle1", LocalDateTime.now(),
//        UUID.randomUUID().toString(), true);
//    final Vehicle vehicle2 = new Vehicle(2L, "vehicle2", LocalDateTime.now(),
//        UUID.randomUUID().toString(), true);
//
//    when(rideJdbcDao.findByStatuses(
//        List.of(RideStatus.RUNNING.name(), RideStatus.PAUSED.name(), RideStatus.STOPPED.name())))
//        .thenReturn(List.of(new Ride(2L, NOW.toLocalDateTime(), RideStatus.RUNNING)));
//    when(dao.findAll()).thenReturn(List.of(vehicle1, vehicle2));
//
//    // when
//    final List<Vehicle> result = underTest.getAvailableVehicles();
//
//    // then
//    assertThat(result).containsExactly(vehicle1);
//  }
//
//  @Test
//  @Tag("unit")
//  public void canUploadVehicleImage() throws IOException {
//    // given
//    final long vehicleId = 1L;
//    final Vehicle vehicle = new Vehicle(vehicleId, "vehicleName", LocalDateTime.now(),
//        UUID.randomUUID().toString(), true);
//    final MultipartFile file = new MockMultipartFile("file", "file.txt", "text/plain",
//        "some xml".getBytes());
//    final String bucket = "vehicles-bucket";
//    when(s3Buckets.getVehicles()).thenReturn(bucket);
//    when(dao.findById(vehicleId)).thenReturn(Optional.of(vehicle));
//
//    // when
//    underTest.uploadVehicleImage(vehicleId, file);
//
//    // then
//    final ArgumentCaptor<String> vehicleImageIdArgumentCaptor = ArgumentCaptor.forClass(String.class);
//
//    verify(dao).updateVehicleImageId(eq(vehicleId), vehicleImageIdArgumentCaptor.capture());
//    verify(s3Service).putObject(bucket, "vehicle-images/%s/%s".formatted(vehicleId,
//        vehicleImageIdArgumentCaptor.getValue()), file.getBytes());
//  }
//
//  @Test
//  @Tag("unit")
//  public void canGetVehicleImage() {
//    // given
//    final long vehicleId = 1L;
//    final Vehicle vehicle = new Vehicle(vehicleId, "vehicleName", LocalDateTime.now(),
//        UUID.randomUUID().toString(), true);
//    final String bucket = "vehicles-bucket";
//    final String vehicleImageId = UUID.randomUUID().toString();
//    when(s3Buckets.getVehicles()).thenReturn(bucket);
//    when(dao.findById(vehicleId)).thenReturn(Optional.of(vehicle));
//
//    final String text = "some xml";
//    when(s3Service.getObject(bucket, "vehicle-images/%s/%s".formatted(vehicleId, vehicleImageId)))
//        .thenReturn(text.getBytes());
//    vehicle.vehicleImageId = vehicleImageId;
//
//    // when
//    final byte[] result = underTest.getVehicleImage(vehicleId);
//
//    // then
//    assertThat(result).isEqualTo(text.getBytes());
//  }
//
}
