package com.flight.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.flight.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	List<Booking> findByUserId(String userId);
	
	//==========
	// ADDED FOR SHOWING SEATS 
	//===========
	@Query("SELECT b FROM Booking b JOIN FETCH b.flight WHERE b.userId = :userId")
	List<Booking> findBookingsWithFlight(@Param("userId") String userId);
}
