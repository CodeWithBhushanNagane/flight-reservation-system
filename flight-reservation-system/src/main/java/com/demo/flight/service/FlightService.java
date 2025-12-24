package com.demo.flight.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.demo.flight.exception.DuplicateFlightException;
import org.springframework.stereotype.Service;

import com.demo.flight.dto.FlightRequest;
import com.demo.flight.dto.FlightResponse;
import com.demo.flight.dto.FlightSeatAvailabilityResponse;
import com.demo.flight.entity.Booking;
import com.demo.flight.entity.Flight;
import com.demo.flight.entity.Seat;
import com.demo.flight.enums.BookingStatus;
import com.demo.flight.enums.FlightStatus;
import com.demo.flight.exception.FlightNotFoundException;
import com.demo.flight.exception.InvalidFlightStatusException;
import com.demo.flight.repository.BookingRepository;
import com.demo.flight.repository.BookingSeatRepository;
import com.demo.flight.repository.FlightRepository;
import com.demo.flight.repository.SeatRepository;
import com.demo.flight.util.FlightMapper;

@Service
public class FlightService {

	private final FlightRepository flightRepository;
	private final SeatRepository seatRepository;
	private final BookingRepository bookingRepository;
	private final BookingSeatRepository bookingSeatRepository;

	public FlightService(FlightRepository flightRepository, SeatRepository seatRepository, BookingRepository bookingRepository, BookingSeatRepository bookingSeatRepository) {

		this.flightRepository = flightRepository;
		this.seatRepository = seatRepository;
		this.bookingRepository = bookingRepository;
		this.bookingSeatRepository = bookingSeatRepository;
	}

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
	
	public FlightResponse addFlight(FlightRequest flightRequest) {
        Optional<Flight> flight = flightRepository.findByFlightCode(flightRequest.getFlightCode());
        if(flight.isPresent()){
            throw new DuplicateFlightException("Duplicate Flight: " + flightRequest.getFlightCode());
        }
		return FlightMapper.toDto(flightRepository.save(FlightMapper.toEntity(flightRequest)));
	}
	
	public void updateFlightStatus(String flightCode, FlightStatus newStatus) {

	    Flight flight = flightRepository.findByFlightCode(flightCode)
	            .orElseThrow(() -> new FlightNotFoundException("Flight not found: " + flightCode));

	    if (flight.getStatus() == FlightStatus.DEPARTED ||
	        flight.getStatus() == FlightStatus.CANCELLED) {
	        throw new InvalidFlightStatusException("Flight status cannot be changed");
	    }
	    if(newStatus == FlightStatus.CANCELLED) {
	    	//TODO: Update bookings and seats and inform user.
	    	List<Booking> bookings = bookingRepository.findByFlight(flight);
	    	for(Booking booking : bookings) {
	    		bookingSeatRepository.deleteBookingSeats(booking.getBookingId());
	    		booking.setStatus(BookingStatus.CANCELLED_BY_AIRLINE);
	    		bookingRepository.save(booking);
	    	}
	    }
	    flight.setStatus(newStatus);
	    flightRepository.save(flight);
	}
	
	public List<FlightResponse> getScheduledFlights() {
		
		return flightRepository.findByStatus(FlightStatus.SCHEDULED).stream().map(flight -> {
			return new FlightResponse(
					flight.getFlightCode(), 
					flight.getSource(), 
					flight.getDestination(),
					flight.getDepartureTime(), 
					flight.getArrivalTime(), 
					flight.getStatus().name());
		}).toList();

	}
	
	public List<Flight> serachFlights(String source, String destination, LocalDate journeyDate) {
	    List<Flight> flights = flightRepository.searchActiveFlights(source, destination, journeyDate);
        if(flights.isEmpty()){
            throw new FlightNotFoundException("Flight Not Found.");
        }
        return flights;
	}

}
