package com.demo.flight.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.demo.flight.dto.BookingCancelResponse;
import com.demo.flight.dto.BookingRequest;
import com.demo.flight.dto.BookingResponse;
import com.demo.flight.dto.BookingSeatResponse;
import com.demo.flight.entity.Booking;
import com.demo.flight.entity.Flight;
import com.demo.flight.entity.Seat;
import com.demo.flight.enums.BookingStatus;
import com.demo.flight.enums.FlightStatus;
import com.demo.flight.enums.SeatStatus;
import com.demo.flight.exception.BookingNotFoundException;
import com.demo.flight.exception.FlightNotFoundException;
import com.demo.flight.exception.InvalidBookingCancelException;
import com.demo.flight.exception.InvalidBookingException;
import com.demo.flight.exception.SeatNotAvailableException;
import com.demo.flight.repository.BookingRepository;
import com.demo.flight.repository.BookingSeatRepository;
import com.demo.flight.repository.FlightRepository;
import com.demo.flight.repository.SeatRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingSeatRepository bookingSeatRepository;

    @InjectMocks
    private BookingService bookingService;

    // =========================
    // bookSeats - SUCCESS
    // =========================
    @Test
    void bookSeats_success() {

        BookingRequest request = new BookingRequest();
        request.setFlightCode("AI101");
        request.setRequestedSeats(List.of("A1", "A2"));

        Flight flight = new Flight();
        flight.setFlightId(1L);
        flight.setFlightCode("AI101");
        flight.setStatus(FlightStatus.SCHEDULED);

        Seat s1 = new Seat();
        s1.setSeatId(1L);
        s1.setSeatNumber("A1");
        s1.setStatus(SeatStatus.AVAILABLE);

        Seat s2 = new Seat();
        s2.setSeatId(2L);
        s2.setSeatNumber("A2");
        s2.setStatus(SeatStatus.AVAILABLE);

        when(flightRepository.findByFlightCode("AI101"))
                .thenReturn(Optional.of(flight));
        when(seatRepository.lockSeatsForBooking(1L, List.of("A1", "A2")))
                .thenReturn(List.of(s1, s2));
        when(bookingRepository.save(any()))
                .thenAnswer(invocation -> {
                    Booking b = invocation.getArgument(0);
                    b.setBookingId(10L);
                    return b;
                });

        BookingResponse response =
                bookingService.bookSeats("user1", request);

        assertEquals("AI101", response.getFlightCode());
        assertEquals(2, response.getSeatsBooked().size());
        assertEquals("CONFIRMED", response.getStatus());

        verify(bookingRepository).save(any(Booking.class));
        verify(bookingSeatRepository, times(2)).save(any());
    }

    // =========================
    // bookSeats - FLIGHT NOT FOUND
    // =========================
    @Test
    void bookSeats_flightNotFound() {

        BookingRequest request = new BookingRequest();
        request.setFlightCode("XX101");

        when(flightRepository.findByFlightCode("XX101"))
                .thenReturn(Optional.empty());

        assertThrows(FlightNotFoundException.class,
                () -> bookingService.bookSeats("user1", request));
    }

    // =========================
    // bookSeats - SEAT NOT AVAILABLE
    // =========================
    @Test
    void bookSeats_seatAlreadyBooked() {

        BookingRequest request = new BookingRequest();
        request.setFlightCode("AI101");
        request.setRequestedSeats(List.of("A1"));

        Flight flight = new Flight();
        flight.setFlightId(1L);
        flight.setStatus(FlightStatus.SCHEDULED);

        Seat seat = new Seat();
        seat.setSeatNumber("A1");
        seat.setStatus(SeatStatus.BOOKED);

        when(flightRepository.findByFlightCode("AI101"))
                .thenReturn(Optional.of(flight));
        when(seatRepository.lockSeatsForBooking(1L, List.of("A1")))
                .thenReturn(List.of(seat));

        assertThrows(SeatNotAvailableException.class,
                () -> bookingService.bookSeats("user1", request));
    }

    // =========================
    // bookSeats - INVALID FLIGHT STATUS
    // =========================
    @Test
    void bookSeats_flightDeparted() {

        BookingRequest request = new BookingRequest();
        request.setFlightCode("AI101");

        Flight flight = new Flight();
        flight.setStatus(FlightStatus.DEPARTED);

        when(flightRepository.findByFlightCode("AI101"))
                .thenReturn(Optional.of(flight));

        assertThrows(InvalidBookingException.class,
                () -> bookingService.bookSeats("user1", request));
    }

    // =========================
    // getUserBookings
    // =========================
    @Test
    void getUserBookings_success() {

        when(bookingRepository.findByUserId("user1"))
                .thenReturn(List.of(new Booking(), new Booking()));

        List<Booking> bookings =
                bookingService.getUserBookings("user1");

        assertEquals(2, bookings.size());
    }

    // =========================
    // getUserBookingsWithSeats
    // =========================
    @Test
    void getUserBookingsWithSeats_success() {

        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setBookingCode("BK101");
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookingTime(LocalDateTime.now());
        booking.setFlight(new Flight());

        when(bookingRepository.findBookingsWithFlight("user1"))
                .thenReturn(List.of(booking));
        when(bookingSeatRepository.findSeatNumbersByBookingId(1L))
                .thenReturn(List.of("A1", "A2"));

        List<BookingSeatResponse> responses =
                bookingService.getUserBookingsWithSeats("user1");

        assertEquals(1, responses.size());
        assertEquals(2, responses.get(0).getSeats().size());
    }

    // =========================
    // cancelBooking - SUCCESS
    // =========================
    @Test
    void cancelBooking_success() {

        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setBookingCode("BK101");
        booking.setStatus(BookingStatus.CONFIRMED);

        Flight flight = new Flight();
        flight.setFlightId(10L);
        flight.setFlightCode("AI101");
        flight.setSource("PUNE");
        flight.setDestination("DELHI");
        flight.setStatus(FlightStatus.SCHEDULED);

        booking.setFlight(flight);

        when(bookingRepository.findByBookingCode("BK101"))
                .thenReturn(Optional.of(booking));
        when(flightRepository.findByFlightCode("AI101"))
                .thenReturn(Optional.of(flight));
        when(bookingSeatRepository.findSeatNumbersByBookingId(1L))
                .thenReturn(List.of("A1", "A2"));
        when(seatRepository.updateSeatStatus(10L, List.of("A1", "A2")))
                .thenReturn(2);

        BookingCancelResponse response =
                bookingService.cancelBooking("BK101");

        assertEquals(BookingStatus.CANCELLED, response.getStatus());
        verify(bookingRepository).save(booking);
        verify(bookingSeatRepository).deleteBookingSeats(1L);
    }

    // =========================
    // cancelBooking - ALREADY CANCELLED
    // =========================
    @Test
    void cancelBooking_alreadyCancelled() {

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.CANCELLED);

        when(bookingRepository.findByBookingCode("BK101"))
                .thenReturn(Optional.of(booking));

        assertThrows(InvalidBookingCancelException.class,
                () -> bookingService.cancelBooking("BK101"));
    }

    // =========================
    // cancelBooking - BOOKING NOT FOUND
    // =========================
    @Test
    void cancelBooking_bookingNotFound() {

        when(bookingRepository.findByBookingCode("BK101"))
                .thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.cancelBooking("BK101"));
    }
    
    @Test
    void concurrentBooking_onlyOneSucceeds_noDB() throws Exception {

        // -----------------------
        // Shared state (simulate DB)
        // -----------------------
        AtomicBoolean seatBooked = new AtomicBoolean(false);

        Flight flight = new Flight();
        flight.setFlightId(1L);
        flight.setFlightCode("AI101");
        flight.setStatus(FlightStatus.SCHEDULED);

        when(flightRepository.findByFlightCode("AI101"))
                .thenReturn(Optional.of(flight));

        // Mock locking behavior
        when(seatRepository.lockSeatsForBooking(eq(1L), anyList()))
                .thenAnswer(invocation -> {
                    Seat seat = new Seat();
                    seat.setSeatNumber("A1");

                    if (seatBooked.compareAndSet(false, true)) {
                        seat.setStatus(SeatStatus.AVAILABLE); // first thread
                    } else {
                        seat.setStatus(SeatStatus.BOOKED); // second thread
                    }
                    return List.of(seat);
                });

        when(bookingRepository.save(any()))
                .thenAnswer(invocation -> {
                    Booking b = invocation.getArgument(0);
                    b.setBookingId(1L);
                    return b;
                });

        BookingRequest request = new BookingRequest();
        request.setFlightCode("AI101");
        request.setRequestedSeats(List.of("A1"));

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        Callable<Boolean> task = () -> {
            startLatch.await();
            try {
                bookingService.bookSeats("user1", request);
                return true;
            } catch (SeatNotAvailableException ex) {
                return false;
            }
        };

        Future<Boolean> f1 = executor.submit(task);
        Future<Boolean> f2 = executor.submit(task);

        startLatch.countDown(); // start both threads

        boolean r1 = f1.get();
        boolean r2 = f2.get();

        executor.shutdown();

        // -----------------------
        // Assertion
        // -----------------------
        assertTrue(r1 ^ r2, "Only one booking must succeed");

        verify(bookingRepository, times(1)).save(any());
    }
}
