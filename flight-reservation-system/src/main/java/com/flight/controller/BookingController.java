package com.flight.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flight.dto.BookingRequest;
import com.flight.dto.BookingResponse;
import com.flight.dto.BookingSeatResponse;
import com.flight.entity.Booking;
import com.flight.service.BookingService;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

	private final BookingService bookingService;

	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	// ===============================
	// BOOK SEATS
	// ===============================
	@PostMapping
	public ResponseEntity<BookingResponse> bookSeats(@RequestHeader("X-USER-ID") String userId,
			@RequestBody BookingRequest request) {

		BookingResponse response = bookingService.bookSeats(userId, request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	// ===============================
	// SHOW MY BOOKINGS
	// ===============================
	@GetMapping
	public List<Booking> getMyBookings(@RequestHeader("X-USER-ID") String userId) {

		return bookingService.getUserBookings(userId);
	}
	
	// ===============================
	// SHOW MY BOOKINGS WITH SEATS
	// ===============================
	@GetMapping("/seats")
	public List<BookingSeatResponse> getMyBookingsWithSeats(@RequestHeader("X-USER-ID") String userId) {
		return bookingService.getUserBookingsWithSeats(userId);
	}
}
