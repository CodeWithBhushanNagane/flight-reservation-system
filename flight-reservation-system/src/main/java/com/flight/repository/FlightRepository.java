package com.flight.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flight.entity.Flight;
import com.flight.enums.FlightStatus;


public interface FlightRepository extends JpaRepository<Flight, Long> {

	Optional<Flight> findByFlightCode(String flightCode);
	
	List<Flight> findByStatus(FlightStatus status);
}
