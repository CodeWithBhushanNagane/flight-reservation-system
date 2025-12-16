package com.flight.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.flight.dto.FlightSeatAvailabilityResponse;
import com.flight.entity.Flight;
import com.flight.entity.Seat;
import com.flight.exception.FlightNotFoundException;
import com.flight.repository.FlightRepository;
import com.flight.repository.SeatRepository;

@Service
public class FlightService {

	private final FlightRepository flightRepository;
	private final SeatRepository seatRepository;

	public FlightService(FlightRepository flightRepository, SeatRepository seatRepository) {

		this.flightRepository = flightRepository;
		this.seatRepository = seatRepository;
	}

	// =========================
	// SHOW SEAT AVAILABILITY
	// =========================
	public FlightSeatAvailabilityResponse getSeatAvailability(String flightCode) {

		Flight flight = flightRepository.findByFlightCode(flightCode)
				.orElseThrow(() -> new FlightNotFoundException("Flight not found: " + flightCode));

		List<Seat> availableSeats = seatRepository.findAvailableSeats(flight.getFlightId());

		FlightSeatAvailabilityResponse response = new FlightSeatAvailabilityResponse();
		response.setFlightCode(flight.getFlightCode());
		response.setFlightName(flight.getFlightName());
		response.setSource(flight.getSource());
		response.setDestination(flight.getDestination());
		response.setDepartureTime(flight.getDepartureTime());
		response.setArrivalTime(flight.getArrivalTime());
		response.setStatus(flight.getStatus().name());

		response.setAvailableSeatCount(availableSeats.size());
		response.setAvailableSeats(availableSeats.stream().map(Seat::getSeatNumber).collect(Collectors.toList()));

		return response;
	}
}
