package com.flight.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flight.dto.BookingCancelRequest;
import com.flight.dto.BookingCancelResponse;
import com.flight.dto.BookingRequest;
import com.flight.dto.BookingResponse;
import com.flight.dto.BookingSeatResponse;
import com.flight.entity.Booking;
import com.flight.service.BookingService;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

	private final BookingService bookingService;

	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	// ===============================
	// BOOK SEATS
	// ===============================
	@PostMapping
	public ResponseEntity<BookingResponse> bookSeats(
			@RequestBody BookingRequest request) {

		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		BookingResponse response = bookingService.bookSeats(userId, request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	// ===============================
	// SHOW MY BOOKINGS
	// ===============================
	@GetMapping
	public List<Booking> getMyBookings() {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		return bookingService.getUserBookings(userId);
	}
	
	// ===============================
	// SHOW MY BOOKINGS WITH SEATS
	// ===============================
	@GetMapping("/seats")
	public List<BookingSeatResponse> getMyBookingsWithSeats() {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		return bookingService.getUserBookingsWithSeats(userId);
	}
	
	// ===============================
	// CANCEL BOOKING
	// ===============================
	@PostMapping("/cancel")
	public BookingCancelResponse cancelBooking(@RequestBody BookingCancelRequest bookingCancelRequest) {
		return bookingService.cancelBooking(bookingCancelRequest.getBookingCode());
	}
}
