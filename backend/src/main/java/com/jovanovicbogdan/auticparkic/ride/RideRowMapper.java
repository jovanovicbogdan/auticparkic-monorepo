package com.jovanovicbogdan.auticparkic.ride;

import com.jovanovicbogdan.auticparkic.common.Constants;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class RideRowMapper implements RowMapper<Ride> {

  @Override
  public Ride mapRow(final ResultSet rs, final int rowNum) throws SQLException {
    final LocalDateTime finishedAt = Optional.ofNullable(rs.getString("finished_at"))
        .map(str -> LocalDateTime.parse(str, Constants.FORMATTER))
        .orElse(null);
    return new Ride(
        rs.getLong("ride_id"),
        RideStatus.valueOf(rs.getString("status")),
        rs.getLong("elapsed_time"),
        LocalDateTime.parse(rs.getString("created_at"), Constants.FORMATTER),
        LocalDateTime.parse(rs.getString("started_at"), Constants.FORMATTER),
        (LocalDateTime[]) rs.getArray("paused_at").getArray(),
        (LocalDateTime[]) rs.getArray("resumed_at").getArray(),
        finishedAt,
        rs.getDouble("price"),
        rs.getLong("vehicle_id")
    );
  }
}
