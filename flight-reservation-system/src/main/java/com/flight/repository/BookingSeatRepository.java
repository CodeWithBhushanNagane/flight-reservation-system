package com.flight.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.flight.entity.BookingSeat;
import com.flight.entity.BookingSeatId;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, BookingSeatId> {
	//==========
	// ADDED FOR SHOWING SEATS 
	//===========
	@Query("SELECT s.seatNumber FROM BookingSeat bs JOIN Seat s ON bs.seatId = s.seatId WHERE bs.bookingId = :bookingId")
	List<String> findSeatNumbersByBookingId(@Param("bookingId") Long bookingId);
}
