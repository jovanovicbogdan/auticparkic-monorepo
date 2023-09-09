package com.jovanovicbogdan.auticparkic.ride;

import com.jovanovicbogdan.auticparkic.common.Constants;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class RideRowMapper implements RowMapper<Ride> {

  @Override
  public Ride mapRow(final ResultSet rs, final int rowNum) throws SQLException {
    final LocalDateTime startedAt = Optional.ofNullable(rs.getString("started_at"))
        .map(str -> LocalDateTime.parse(str, Constants.FORMATTER))
        .orElse(null);

    final LocalDateTime[] pausedAt = Optional.ofNullable(rs.getArray("paused_at"))
        .map(arr -> {
          try {
            final Timestamp[] dbTimestampArr = (Timestamp[]) arr.getArray();
            return convertArray(dbTimestampArr);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        }).orElse(null);

    final LocalDateTime[] resumedAt = Optional.ofNullable(rs.getArray("resumed_at"))
        .map(arr -> {
          try {
            final Timestamp[] dbTimestampArr = (Timestamp[]) arr.getArray();
            return convertArray(dbTimestampArr);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        }).orElse(null);

    final LocalDateTime finishedAt = Optional.ofNullable(rs.getString("finished_at"))
        .map(str -> LocalDateTime.parse(str, Constants.FORMATTER))
        .orElse(null);
    return new Ride(
        rs.getLong("ride_id"),
        RideStatus.valueOf(rs.getString("status")),
        rs.getLong("elapsed_time"),
        LocalDateTime.parse(rs.getString("created_at"), Constants.FORMATTER),
        startedAt,
        pausedAt,
        resumedAt,
        finishedAt,
        rs.getDouble("price"),
        rs.getLong("vehicle_id")
    );
  }

  private LocalDateTime[] convertArray(final Timestamp[] arr) {
    final LocalDateTime[] result = new LocalDateTime[arr.length];
    for (int i = 0; i < arr.length; i++) {
      result[i] = arr[i].toLocalDateTime();
    }
    return result;
  }
}
