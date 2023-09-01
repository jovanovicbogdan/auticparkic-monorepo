package com.jovanovicbogdan.auticparkic.vehicle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VehicleRequestDTO(@NotBlank String vehicleName, @NotNull boolean isActive) {
}
