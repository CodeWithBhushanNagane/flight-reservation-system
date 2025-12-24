package com.demo.flight.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class FlightSeatAvailabilityResponse {

	private String flightCode;
	private String flightName;
	private String source;
	private String destination;

	private LocalDateTime departureTime;
	private LocalDateTime arrivalTime;

	private String status;

	private int availableSeatCount;
	private List<String> availableSeats;
}
