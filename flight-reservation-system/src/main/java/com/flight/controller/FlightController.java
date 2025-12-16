package com.flight.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flight.dto.FlightSeatAvailabilityResponse;
import com.flight.service.FlightService;

@RestController
@RequestMapping("/api/flight")
public class FlightController {
	private final FlightService flightService;

	public FlightController(FlightService flightService) {
		this.flightService = flightService;
	}

	@GetMapping("/seatAvailability/{flightCode}")
	public FlightSeatAvailabilityResponse getSeatAvailability(@PathVariable String flightCode) {
		return flightService.getSeatAvailability(flightCode);
	}
}
