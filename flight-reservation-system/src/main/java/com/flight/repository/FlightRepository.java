package com.flight.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flight.entity.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long> {

	Optional<Flight> findByFlightCode(String flightCode);
}
