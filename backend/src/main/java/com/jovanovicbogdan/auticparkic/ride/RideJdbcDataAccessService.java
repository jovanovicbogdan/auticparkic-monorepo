package com.jovanovicbogdan.auticparkic.ride;

import com.jovanovicbogdan.auticparkic.common.Constants;
import com.jovanovicbogdan.auticparkic.common.DAO;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RideJdbcDataAccessService implements DAO<Ride> {

  private final JdbcTemplate jdbcTemplate;
  private final RideRowMapper rideRowMapper;

  public RideJdbcDataAccessService(final JdbcTemplate jdbcTemplate, final RideRowMapper rideRowMapper) {
    this.jdbcTemplate = jdbcTemplate;
    this.rideRowMapper = rideRowMapper;
  }

  @Override
  @Transactional
  public Ride create(final Ride ride) {
    final String sql = """
        INSERT INTO ride(status, elapsed_time, created_at, finished_at, price, vehicle_id)
        VALUES (?::status, ?, ?::timestamp, ?::timestamp, ?, ?)
        RETURNING ride_id, status, elapsed_time, created_at, finished_at, price, vehicle_id;
        """;
    final String finishedAt = Optional.ofNullable(ride.finishedAt)
        .map(it -> it.format(Constants.FORMATTER))
        .orElse(null);

    return jdbcTemplate.queryForObject(sql, rideRowMapper, ride.status.name(), ride.elapsedTime,
        ride.createdAt, finishedAt, ride.price, ride.vehicleId);
  }

  @Override
  @Transactional
  public Ride update(final Ride ride) {
    final String sql = """
        UPDATE ride
        SET status = ?, elapsed_time = ?, created_at = ?, finished_at = ?, price = ?, vehicle_id = ?
        WHERE ride_id = ?
        RETURNING ride_id, status, elapsed_time, created_at, finished_at, price, vehicle_id;
        """;

    return jdbcTemplate.queryForObject(sql, rideRowMapper, ride.status.name(), ride.elapsedTime,
        ride.createdAt, ride.finishedAt, ride.price, ride.vehicleId, ride.rideId);
  }

  @Override
  public List<Ride> findAll() {
    final String sql = """
        SELECT ride_id, status, elapsed_time, created_at, finished_at, price, vehicle_id
        FROM ride
        ORDER BY ride_id DESC;
        """;

    return jdbcTemplate.query(sql, rideRowMapper);
  }

  @Override
  public Optional<Ride> findById(final long rideId) {
    final String sql = """
        SELECT ride_id, status, elapsed_time, created_at, finished_at, price, vehicle_id
        FROM ride
        WHERE ride_id = ?;
        """;

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

  public List<Ride> findByStatuses(final List<String> statuses) {
    final String inSql = String.join(", ", Collections.nCopies(statuses.size(), "?::status"));
    final String sql = """
        SELECT ride_id, status, elapsed_time, created_at, finished_at, price, vehicle_id
        FROM ride
        WHERE status IN (%s)
        ORDER BY ride_id DESC;
        """;

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
        SELECT ride_id, status, elapsed_time, created_at, finished_at, price, vehicle_id
        FROM ride
        WHERE vehicle_id = ? AND status IN (%s)
        ORDER BY ride_id DESC;
        """;

    return jdbcTemplate.query(String.format(sql, inSql), rideRowMapper, params);
  }
}
