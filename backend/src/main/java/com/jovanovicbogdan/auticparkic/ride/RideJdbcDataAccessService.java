package com.jovanovicbogdan.auticparkic.ride;

import com.jovanovicbogdan.auticparkic.common.Constants;
import com.jovanovicbogdan.auticparkic.common.DAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

@Repository
public class RideJdbcDataAccessService implements DAO<Ride> {

  private static final Logger log = LoggerFactory.getLogger(RideJdbcDataAccessService.class);
  private final JdbcTemplate jdbcTemplate;
  private final RideRowMapper rideRowMapper;

  public RideJdbcDataAccessService(final JdbcTemplate jdbcTemplate,
      final RideRowMapper rideRowMapper) {
    this.jdbcTemplate = jdbcTemplate;
    this.rideRowMapper = rideRowMapper;
  }

  @Override
  public Ride create(final Ride ride) {
    final String sql = """
        INSERT INTO ride(status, elapsed_time, started_at, paused_at, resumed_at, stopped_at, finished_at, price, vehicle_id)
        VALUES (?::status, ?, ?::timestamp, ?, ?, ?::timestamp, ?::timestamp, ?, ?)
        RETURNING ride_id, status, elapsed_time, created_at, started_at, paused_at, resumed_at, stopped_at, finished_at, price, vehicle_id;
        """;

    final String startedAt = Optional.ofNullable(ride.startedAt)
        .map(it -> it.format(Constants.FORMATTER))
        .orElse(null);

    final String stoppedAt = Optional.ofNullable(ride.stoppedAt)
        .map(it -> it.format(Constants.FORMATTER))
        .orElse(null);

    final String finishedAt = Optional.ofNullable(ride.finishedAt)
        .map(it -> it.format(Constants.FORMATTER))
        .orElse(null);

    log.debug("Attempting to save ride to database: {}", ride);

    return jdbcTemplate.queryForObject(sql, rideRowMapper, ride.status.name(), ride.elapsedTime,
        startedAt, ride.pausedAt, ride.resumedAt, stoppedAt, finishedAt, ride.price,
        ride.vehicleId);
  }

  @Override
  public boolean update(final Ride ride) {
    final String sql = """
        UPDATE ride
        SET status = ?::status, elapsed_time = ?, started_at = ?::timestamp, paused_at = ?, resumed_at = ?, stopped_at = ?::timestamp, finished_at = ?::timestamp, price = ?, vehicle_id = ?
        WHERE ride_id = ?;
        """;

    log.debug("Attempting to update ride in database: {}", ride);

    final int rowsAffected = jdbcTemplate.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(final Connection con) throws SQLException {
        final PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, ride.status.name());
        ps.setLong(2, ride.elapsedTime);
        ps.setString(3, ride.startedAt != null ? ride.startedAt.format(Constants.FORMATTER) : null);
        ps.setArray(4, con.createArrayOf("timestamp", ride.pausedAt));
        ps.setArray(5, con.createArrayOf("timestamp", ride.resumedAt));
        ps.setString(6,
            ride.stoppedAt != null ? ride.stoppedAt.format(Constants.FORMATTER) : null);
        ps.setString(7,
            ride.finishedAt != null ? ride.finishedAt.format(Constants.FORMATTER) : null);
        ps.setDouble(8, ride.price);
        ps.setLong(9, ride.vehicleId);
        ps.setLong(10, ride.rideId);

        return ps;
      }
    });

    return rowsAffected == 1;
  }

  @Override
  public List<Ride> findAll() {
    final String sql = """
        SELECT ride_id, status, elapsed_time, created_at, started_at, paused_at, resumed_at, stopped_at, finished_at, price, vehicle_id
        FROM ride
        ORDER BY ride_id DESC;
        """;

    log.debug("Attempting to retrieve all rides from database");

    return jdbcTemplate.query(sql, rideRowMapper);
  }

  @Override
  public Optional<Ride> findById(final long rideId) {
    final String sql = """
        SELECT ride_id, status, elapsed_time, created_at, started_at, paused_at, resumed_at, stopped_at, finished_at, price, vehicle_id
        FROM ride
        WHERE ride_id = ?
        FOR UPDATE;
        """;

    log.debug("Attempting to retrieve ride with id {} from database", rideId);

    return jdbcTemplate.query(sql, rideRowMapper, rideId)
        .stream()
        .findFirst();
  }

  @Override
  public void delete(final long rideId) {
    final String sql = """
        DELETE FROM ride
        WHERE ride_id = ?;
        """;

    jdbcTemplate.update(sql, rideId);
  }

  protected void deleteAll() {
    final String sql = """
        DELETE FROM ride;
        """;

    log.debug("Attempting to delete all rides from database");

    jdbcTemplate.update(sql);
  }

  public List<Ride> findByStatuses(final List<String> statuses) {
    final String inSql = String.join(", ", Collections.nCopies(statuses.size(), "?::status"));
    final String sql = """
        SELECT ride_id, status, elapsed_time, created_at, started_at, paused_at, resumed_at, stopped_at, finished_at, price, vehicle_id
        FROM ride
        WHERE status IN (%s)
        ORDER BY ride_id DESC
        FOR UPDATE;
        """;

    log.debug("Attempting to retrieve rides with statuses {} from database", statuses);

    return jdbcTemplate.query(String.format(sql, inSql), rideRowMapper, statuses.toArray());
  }

  public List<Ride> findByVehicleIdAndStatuses(final Long vehicleId, final List<String> statuses) {
    final String inSql = statuses.stream().map(str -> "?::status")
        .collect(Collectors.joining(", "));

    final Object[] params = new Object[statuses.size() + 1];
    params[0] = vehicleId;
    for (int i = 0; i < statuses.size(); i++) {
      params[i + 1] = statuses.get(i);
    }

    final String sql = """
        SELECT ride_id, status, elapsed_time, created_at, started_at, paused_at, resumed_at, stopped_at, finished_at, price, vehicle_id
        FROM ride
        WHERE vehicle_id = ? AND status IN (%s)
        ORDER BY ride_id DESC
        FOR UPDATE;
        """;

    log.debug("Attempting to retrieve rides with statuses {} and vehicle id {} from database",
        statuses, vehicleId);

    return jdbcTemplate.query(String.format(sql, inSql), rideRowMapper, params);
  }
}
