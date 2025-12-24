package com.demo.flight.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.flight.dto.BookingCancelRequest;
import com.demo.flight.dto.BookingCancelResponse;
import com.demo.flight.dto.BookingRequest;
import com.demo.flight.dto.BookingResponse;
import com.demo.flight.dto.BookingSeatResponse;
import com.demo.flight.entity.Booking;
import com.demo.flight.service.BookingService;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

	private final BookingService bookingService;

	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	@PostMapping
	public ResponseEntity<BookingResponse> bookSeats(
			@RequestBody BookingRequest request) {

		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		BookingResponse response = bookingService.bookSeats(userId, request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public List<Booking> getMyBookings() {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		return bookingService.getUserBookings(userId);
	}

	@GetMapping("/seats")
	public List<BookingSeatResponse> getMyBookingsWithSeats() {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		return bookingService.getUserBookingsWithSeats(userId);
	}

	@PostMapping("/cancel")
	public BookingCancelResponse cancelBooking(@RequestBody BookingCancelRequest bookingCancelRequest) {
		return bookingService.cancelBooking(bookingCancelRequest.getBookingCode());
	}
}
