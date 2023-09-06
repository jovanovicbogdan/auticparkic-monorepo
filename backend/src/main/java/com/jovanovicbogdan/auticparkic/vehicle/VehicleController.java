package com.jovanovicbogdan.auticparkic.vehicle;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/vehicles")
@CrossOrigin
@Validated
public class VehicleController {

  private final Logger log = LoggerFactory.getLogger(VehicleController.class);
  private final VehicleService service;

  public VehicleController(final VehicleService service) {
    this.service = service;
  }

  @GetMapping("/{vehicleId}")
  public Vehicle getVehicleById(@PathVariable final long vehicleId) {
    log.info("Request to get vehicle with id: {}", vehicleId);
    return service.getVehicleById(vehicleId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public long createVehicleIfNotExists(@Valid @RequestBody final VehicleRequestDTO request) {
    log.info("Request to create vehicle: {}", request);
    return service.createVehicleIfNotExists(request);
  }

  @Operation(summary = "Update vehicle")
  @PutMapping("/{vehicleId}")
  public void updateVehicle(@PathVariable final long vehicleId,
      @Valid @RequestBody final VehicleRequestDTO request) {
    log.info("Request to update vehicle with id: {} and request: {}", vehicleId, request);
    service.updateVehicle(vehicleId, request);
  }

  @GetMapping("available")
  public List<Vehicle> getAvailableVehicles() {
    log.info("Request to get available vehicles");
    return service.getAvailableVehicles();
  }

  @PostMapping(value = "/image/{vehicleId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public void uploadVehicleImage(@PathVariable final long vehicleId,
      @RequestParam("image") final MultipartFile file) {
    log.info("Request to upload image for vehicle with id: {}", vehicleId);
    service.uploadVehicleImage(vehicleId, file);
  }

  @GetMapping(value = "/image/{vehicleId}/download")
  public byte[] getVehicleImage(@PathVariable final long vehicleId) {
    log.info("Request to download image for vehicle with id: {}", vehicleId);
    return service.getVehicleImage(vehicleId);
  }

  @DeleteMapping("/{vehicleId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteVehicle(@PathVariable final long vehicleId) {
    log.info("Request to delete vehicle with id: {}", vehicleId);
    service.deleteVehicle(vehicleId);
  }

}
