package com.jovanovicbogdan.auticparkic.vehicle;

import com.jovanovicbogdan.auticparkic.common.Constants;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class VehicleRowMapper implements RowMapper<Vehicle> {

  @Override
  public Vehicle mapRow(final ResultSet rs, final int rowNum) throws SQLException {
    final LocalDateTime createdAt = Optional.ofNullable(rs.getString("created_at"))
        .map(str -> LocalDateTime.parse(str, Constants.FORMATTER))
        .orElse(null);
    return new Vehicle(
        rs.getLong("vehicle_id"),
        rs.getString("name"),
        createdAt,
        rs.getString("vehicle_image_id"),
        rs.getBoolean("is_active")
    );
  }
}
