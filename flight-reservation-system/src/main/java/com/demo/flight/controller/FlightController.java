package com.demo.flight.controller;

import java.util.List;

import com.demo.flight.service.SeatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.flight.dto.FlightRequest;
import com.demo.flight.dto.FlightResponse;
import com.demo.flight.dto.FlightSearchRequest;
import com.demo.flight.dto.FlightSeatAvailabilityResponse;
import com.demo.flight.dto.FlightStatusRequest;
import com.demo.flight.entity.Flight;
import com.demo.flight.service.FlightService;

@RestController
@RequestMapping("/api/v1/flight")
public class FlightController {
	private final FlightService flightService;
    private final SeatService seatService;

	public FlightController(FlightService flightService, SeatService seatService) {
		this.flightService = flightService;
        this.seatService = seatService;
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
	
	@GetMapping("/search")
	public List<Flight> searchFlights(@RequestBody FlightSearchRequest flightSearchRequest) {
		return flightService.serachFlights(flightSearchRequest.source(), flightSearchRequest.destination(), flightSearchRequest.journeyDate());
	}

    @PostMapping("/add/{flightCode}")
    public ResponseEntity<String> generateSeats(@PathVariable String flightCode) {
        int seatsCreated = seatService.createSeatsForFlight(flightCode);
        return ResponseEntity.ok(seatsCreated + " seats Added for flight " + flightCode);
    }

}
