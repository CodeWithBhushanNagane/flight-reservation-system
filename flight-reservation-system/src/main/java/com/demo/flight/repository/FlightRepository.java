package com.demo.flight.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.demo.flight.entity.Flight;
import com.demo.flight.enums.FlightStatus;

public interface FlightRepository extends JpaRepository<Flight, Long> {

	Optional<Flight> findByFlightCode(String flightCode);

	List<Flight> findByStatus(FlightStatus status);

	@Query("SELECT f FROM Flight f WHERE f.source = :source AND f.destination = :destination AND DATE(f.departureTime) = :journeyDate AND f.status IN ('SCHEDULED', 'DELAYED')")
	List<Flight> searchActiveFlights(@Param("source") String source, @Param("destination") String destination,
			@Param("journeyDate") LocalDate journeyDate);
}
