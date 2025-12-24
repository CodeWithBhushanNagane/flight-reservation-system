package com.demo.flight.service;

import com.demo.flight.exception.FlightNotFoundException;
import com.demo.flight.repository.FlightRepository;
import com.demo.flight.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeatService {
    private final SeatRepository seatRepository;
    private final FlightRepository flightRepository;

    public SeatService(SeatRepository seatRepository,
                           FlightRepository flightRepository) {
        this.seatRepository = seatRepository;
        this.flightRepository = flightRepository;
    }

    @Transactional
    public int createSeatsForFlight(String flightCode) {
        flightRepository.findByFlightCode(flightCode)
                .orElseThrow(() -> new FlightNotFoundException("Flight not found: " + flightCode));
        return seatRepository.generateSeatsForFlight(flightCode);
    }
}
