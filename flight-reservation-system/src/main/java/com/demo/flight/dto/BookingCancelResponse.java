package com.demo.flight.dto;

import java.util.List;

import com.demo.flight.enums.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingCancelResponse {

	private String bookingCode;
	private String flightCode;
	private String source;
	private String destination;
	private List<String> seatsBooked;
	private BookingStatus status;
	private String message;
}
