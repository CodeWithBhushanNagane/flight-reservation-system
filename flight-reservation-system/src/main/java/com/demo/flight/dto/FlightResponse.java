package com.demo.flight.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FlightResponse {

	private String flightCode;
	private String source;
	private String destination;
	private LocalDateTime departureTime;
	private LocalDateTime arrivalTime;
	private String status;
}
