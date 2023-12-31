package com.jovanovicbogdan.auticparkic.vehicle;

import com.jovanovicbogdan.auticparkic.common.DAO;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class VehicleJdbcDataAccessService implements DAO<Vehicle> {

  private static final Logger log = LoggerFactory.getLogger(VehicleJdbcDataAccessService.class);
  private final JdbcTemplate jdbcTemplate;
  private final VehicleRowMapper vehicleRowMapper;

  public VehicleJdbcDataAccessService(final JdbcTemplate jdbcTemplate, final VehicleRowMapper vehicleRowMapper) {
    this.jdbcTemplate = jdbcTemplate;
    this.vehicleRowMapper = vehicleRowMapper;
  }

  @Override
  public Vehicle create(final Vehicle vehicle) {
    final String sql = """
        INSERT INTO vehicle(name, created_at, vehicle_image_id, is_active)
        VALUES (?, ?, ?, ?)
        RETURNING vehicle_id, name, created_at, vehicle_image_id, is_active;
        """;

    log.debug("Attempting to save vehicle to database: {}", vehicle);

    return jdbcTemplate.queryForObject(sql, vehicleRowMapper, vehicle.name, vehicle.createdAt,
        vehicle.vehicleImageId, vehicle.isActive);
  }

  @Override
  public boolean update(final Vehicle vehicle) {
    final String sql = """
        UPDATE vehicle
        SET name = ?, created_at = ?, vehicle_image_id = ?, is_active = ?
        WHERE vehicle_id = ?;
        """;

    log.debug("Attempting to update vehicle in database: {}", vehicle);

    final int rowsAffected = jdbcTemplate.update(sql, vehicle.name,
        vehicle.createdAt, vehicle.vehicleImageId, vehicle.isActive, vehicle.vehicleId);

    return rowsAffected == 1;
  }

  @Override
  public List<Vehicle> findAll() {
    final String sql = """
        SELECT vehicle_id, name, created_at, vehicle_image_id, is_active
        FROM vehicle
        ORDER BY created_at ASC;
        """;

    log.debug("Attempting to retrieve all vehicles from database");

    return jdbcTemplate.query(sql, vehicleRowMapper);
  }

  @Override
  public Optional<Vehicle> findById(final long vehicleId) {
    final String sql = """
        SELECT vehicle_id, name, created_at, vehicle_image_id, is_active
        FROM vehicle
        WHERE vehicle_id = ?
        FOR UPDATE;
        """;

    log.debug("Attempting to retrieve vehicle with id '{}' from database", vehicleId);

    return jdbcTemplate.query(sql, vehicleRowMapper, vehicleId)
        .stream()
        .findFirst();
  }

  @Override
  public void delete(final long vehicleId) {
    final String sql = """
        DELETE FROM vehicle
        WHERE vehicle_id = ?;
        """;

    log.debug("Attempting to delete vehicle with id '{}' from database", vehicleId);

    jdbcTemplate.update(sql, vehicleId);
  }

  protected void deleteAll() {
    final String sql = """
        DELETE FROM vehicle;
        """;

    log.debug("Attempting to delete all vehicles from database");

    jdbcTemplate.update(sql);
  }

  public Optional<Vehicle> findVehicleByName(final String name) {
    final String sql = """
        SELECT vehicle_id, name, created_at, vehicle_image_id, is_active
        FROM vehicle
        WHERE name = ?
        FOR UPDATE;
        """;

    log.debug("Attempting to retrieve vehicle with name '{}' from database", name);

    return jdbcTemplate.query(sql, vehicleRowMapper, name)
        .stream()
        .findFirst();
  }

  public void updateVehicleImageId(final long vehicleId, final String vehicleImageId) {
    final var sql = """
        UPDATE vehicle
        SET vehicle_image_id = ?
        WHERE vehicle_id = ?;
        """;

    log.debug("Attempting to update vehicle image id '{}' for vehicle with id '{}' in database",
        vehicleImageId, vehicleId);

    jdbcTemplate.update(sql, vehicleImageId, vehicleId);
  }
}
