package com.demo.flight.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingSeatRepository bookingSeatRepository;

    @InjectMocks
    private FlightService flightService;

    // =========================
    // getSeatAvailability
    // =========================
    @Test
    void getSeatAvailability_success() {

        Flight flight = new Flight();
        flight.setFlightId(1L);
        flight.setFlightCode("AI101");
        flight.setFlightName("Air India");
        flight.setSource("PUN");
        flight.setDestination("DEL");
        flight.setDepartureTime(LocalDateTime.now());
        flight.setArrivalTime(LocalDateTime.now().plusHours(2));
        flight.setStatus(FlightStatus.SCHEDULED);

        Seat s1 = new Seat();
        s1.setSeatNumber("A1");
        Seat s2 = new Seat();
        s2.setSeatNumber("A2");

        when(flightRepository.findByFlightCode("AI101"))
                .thenReturn(Optional.of(flight));
        when(seatRepository.findAvailableSeats(1L))
                .thenReturn(List.of(s1, s2));

        FlightSeatAvailabilityResponse response =
                flightService.getSeatAvailability("AI101");

        assertEquals("AI101", response.getFlightCode());
        assertEquals(2, response.getAvailableSeatCount());
        assertTrue(response.getAvailableSeats().contains("A1"));
        assertTrue(response.getAvailableSeats().contains("A2"));
    }

    @Test
    void getSeatAvailability_flightNotFound() {
        when(flightRepository.findByFlightCode("XX101"))
                .thenReturn(Optional.empty());

        assertThrows(FlightNotFoundException.class,
                () -> flightService.getSeatAvailability("XX101"));
    }

    // =========================
    // updateFlightStatus
    // =========================
    @Test
    void updateFlightStatus_success() {

        Flight flight = new Flight();
        flight.setFlightCode("AI101");
        flight.setStatus(FlightStatus.SCHEDULED);

        when(flightRepository.findByFlightCode("IN105"))
                .thenReturn(Optional.of(flight));

        flightService.updateFlightStatus("IN105", FlightStatus.DELAYED);

        assertEquals(FlightStatus.DELAYED, flight.getStatus());
        verify(flightRepository).save(flight);
    }

    @Test
    void updateFlightStatus_invalidStatusChange() {

        Flight flight = new Flight();
        flight.setStatus(FlightStatus.DEPARTED);

        when(flightRepository.findByFlightCode("AI101"))
                .thenReturn(Optional.of(flight));

        assertThrows(InvalidFlightStatusException.class,
                () -> flightService.updateFlightStatus("AI101", FlightStatus.CANCELLED));
    }

    @Test
    void updateFlightStatus_cancelled_updatesBookings() {

        Flight flight = new Flight();
        flight.setStatus(FlightStatus.SCHEDULED);

        Booking booking = new Booking();
        booking.setBookingId(10L);
        booking.setStatus(BookingStatus.CONFIRMED);

        when(flightRepository.findByFlightCode("AI101"))
                .thenReturn(Optional.of(flight));
        when(bookingRepository.findByFlight(flight))
                .thenReturn(List.of(booking));

        flightService.updateFlightStatus("AI101", FlightStatus.CANCELLED);

        assertEquals(BookingStatus.CANCELLED_BY_AIRLINE, booking.getStatus());
        verify(bookingSeatRepository).deleteBookingSeats(10L);
        verify(bookingRepository).save(booking);
        verify(flightRepository).save(flight);
    }

    // =========================
    // getScheduledFlights
    // =========================
    @Test
    void getScheduledFlights_success() {

        Flight flight = new Flight();
        flight.setFlightCode("AI101");
        flight.setSource("PUNE");
        flight.setDestination("DELHI");
        flight.setDepartureTime(LocalDateTime.now());
        flight.setArrivalTime(LocalDateTime.now().plusHours(2));
        flight.setStatus(FlightStatus.SCHEDULED);

        when(flightRepository.findByStatus(FlightStatus.SCHEDULED))
                .thenReturn(List.of(flight));

        List<FlightResponse> responses = flightService.getScheduledFlights();

        assertEquals(1, responses.size());
        assertEquals("AI101", responses.get(0).getFlightCode());
    }

    // =========================
    // searchFlights
    // =========================
    @Test
    void searchFlights_success() {

        when(flightRepository.searchActiveFlights(
                "PUNE", "DELHI", LocalDate.now()))
                .thenReturn(List.of(new Flight()));

        List<Flight> flights = flightService.serachFlights(
                "PUNE", "DELHI", LocalDate.now());

        assertEquals(1, flights.size());
    }
}
