package com.demo.flight.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class BookingResponse {

	private String bookingCode;
	private String flightCode;
	private List<String> seatsBooked;
	private String status;
	private LocalDateTime bookingTime;
	private String message;
}
