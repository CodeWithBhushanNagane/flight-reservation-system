CREATE DATABASE flight_db;

CREATE TABLE flight (
    flight_id BIGSERIAL PRIMARY KEY,

    flight_code VARCHAR(20) NOT NULL UNIQUE,
    flight_name VARCHAR(100) NOT NULL,

    source VARCHAR(10) NOT NULL,
    destination VARCHAR(10) NOT NULL,

    departure_time TIMESTAMP NOT NULL,
    arrival_time TIMESTAMP NOT NULL,

    status VARCHAR(20) NOT NULL,   -- SCHEDULED / DEPARTED / CANCELLED

    total_seats INT NOT NULL CHECK (total_seats > 0),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE seat (
    seat_id BIGSERIAL PRIMARY KEY,

    flight_id BIGINT NOT NULL,
    seat_number VARCHAR(5) NOT NULL,   -- 12C, 13B
    status VARCHAR(20) NOT NULL,        -- AVAILABLE / BOOKED

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_seat_flight
        FOREIGN KEY (flight_id)
        REFERENCES flight(flight_id)
        ON DELETE CASCADE,

    CONSTRAINT uq_flight_seat
        UNIQUE (flight_id, seat_number)
);

CREATE TABLE booking (
    booking_id BIGSERIAL PRIMARY KEY,

    booking_code VARCHAR(30) NOT NULL UNIQUE,
    user_id VARCHAR(50) NOT NULL,

    flight_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,   -- CONFIRMED / CANCELLED

    booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_booking_flight
        FOREIGN KEY (flight_id)
        REFERENCES flight(flight_id)
);

CREATE TABLE booking_seat (
    booking_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,

    CONSTRAINT fk_bs_booking
        FOREIGN KEY (booking_id)
        REFERENCES booking(booking_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_bs_seat
        FOREIGN KEY (seat_id)
        REFERENCES seat(seat_id),

    CONSTRAINT uq_booking_seat
        UNIQUE (booking_id, seat_id)
);

-- Seat lookup & locking
CREATE INDEX idx_seat_flight_status
ON seat (flight_id, status);

-- User booking lookup
CREATE INDEX idx_booking_user
ON booking (user_id);

-- Flight booking lookup
CREATE INDEX idx_booking_flight
ON booking (flight_id);
