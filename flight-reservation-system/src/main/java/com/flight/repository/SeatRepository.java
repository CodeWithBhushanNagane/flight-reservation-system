package com.flight.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.flight.entity.Seat;

import jakarta.persistence.LockModeType;

public interface SeatRepository extends JpaRepository<Seat, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM Seat s WHERE s.flight.flightId = :flightId AND s.seatNumber IN :seatNumbers")
	List<Seat> lockSeatsForBooking(@Param("flightId") Long flightId, @Param("seatNumbers") List<String> seatNumbers);

	@Query("SELECT s FROM Seat s WHERE s.flight.flightId = :flightId AND s.status = 'AVAILABLE'")
	List<Seat> findAvailableSeats(@Param("flightId") Long flightId);
}
