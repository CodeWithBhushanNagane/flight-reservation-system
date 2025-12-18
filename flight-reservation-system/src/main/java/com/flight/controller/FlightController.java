package com.flight.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flight.dto.FlightRequest;
import com.flight.dto.FlightResponse;
import com.flight.dto.FlightSeatAvailabilityResponse;
import com.flight.dto.FlightStatusRequest;
import com.flight.service.FlightService;

@RestController
@RequestMapping("/api/v1/flight")
public class FlightController {
	private final FlightService flightService;

	public FlightController(FlightService flightService) {
		this.flightService = flightService;
	}

	@GetMapping("/seatAvailability/{flightCode}")
	public FlightSeatAvailabilityResponse getSeatAvailability(@PathVariable String flightCode) {
		return flightService.getSeatAvailability(flightCode);
	}

	@PostMapping("/add")
	public ResponseEntity<FlightResponse> create(@RequestBody FlightRequest flightRequest) {
		return new ResponseEntity<>(flightService.addFlight(flightRequest), HttpStatus.CREATED);
	}

	@PutMapping("/status/{flightCode}")
	public ResponseEntity<?> updateStatus(@PathVariable String flightCode, @RequestBody FlightStatusRequest request) {

		flightService.updateFlightStatus(flightCode, request.getStatus());
		return ResponseEntity.ok("Flight status updated");
	}

	@GetMapping("/scheduled")
	public List<FlightResponse> getScheduledFlights() {
		return flightService.getScheduledFlights();
	}

}
