package com.demo.flight.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class FlightRequest {

	private String flightName;
	private String flightCode;
	private String source;
	private String destination;
	private LocalDateTime departureTime;
	private LocalDateTime arrivalTime;
	private int totalSeats;
}
