CREATE TYPE status AS ENUM ('CREATED', 'RUNNING', 'PAUSED', 'STOPPED', 'FINISHED');
CREATE CAST (varchar AS status) WITH INOUT AS IMPLICIT;

CREATE TABLE vehicle (
    vehicle_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    vehicle_image_id VARCHAR(36),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT uq_vehicle_name UNIQUE (name),
    CONSTRAINT uq_vehicle_vehicle_image_id UNIQUE (vehicle_image_id)
);

CREATE TABLE ride (
    ride_id BIGSERIAL PRIMARY KEY,
    status status NOT NULL,
    elapsed_time BIGINT DEFAULT 0,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP WITHOUT TIME ZONE,
    paused_at TIMESTAMP[] WITHOUT TIME ZONE,
    resumed_at TIMESTAMP[] WITHOUT TIME ZONE,
    finished_at TIMESTAMP WITHOUT TIME ZONE,
    price DECIMAL,
    vehicle_id BIGINT NOT NULL,

    CONSTRAINT fk_vehicle_id FOREIGN KEY (vehicle_id) REFERENCES vehicle(vehicle_id)
);
