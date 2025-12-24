package com.demo.flight.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.flight.dto.BookingCancelResponse;
import com.demo.flight.dto.BookingRequest;
import com.demo.flight.dto.BookingResponse;
import com.demo.flight.dto.BookingSeatResponse;
import com.demo.flight.entity.Booking;
import com.demo.flight.entity.BookingSeat;
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

@Service
public class BookingService {

	private final FlightRepository flightRepository;
	private final SeatRepository seatRepository;
	private final BookingRepository bookingRepository;
	private final BookingSeatRepository bookingSeatRepository;

	public BookingService(FlightRepository flightRepository, SeatRepository seatRepository,
			BookingRepository bookingRepository, BookingSeatRepository bookingSeatRepository) {

		this.flightRepository = flightRepository;
		this.seatRepository = seatRepository;
		this.bookingRepository = bookingRepository;
		this.bookingSeatRepository = bookingSeatRepository;
	}

	@Transactional
	public BookingResponse bookSeats(String userId, BookingRequest request) {

		Flight flight = flightRepository.findByFlightCode(request.getFlightCode())
				.orElseThrow(() -> new FlightNotFoundException("Flight not found: " + request.getFlightCode()));

		validateFlightForBooking(flight);

		List<Seat> seats = seatRepository.lockSeatsForBooking(flight.getFlightId(), request.getRequestedSeats());

		if (seats.size() != request.getRequestedSeats().size()) {
			throw new SeatNotAvailableException("One or more seats do not exist");
		}

		for (Seat seat : seats) {
			if (seat.getStatus() != SeatStatus.AVAILABLE) {
				throw new SeatNotAvailableException("Seat already booked: " + seat.getSeatNumber());
			}
		}

		seats.forEach(seat -> seat.setStatus(SeatStatus.BOOKED));

		Booking booking = new Booking();
		booking.setBookingCode("AIBK-" + System.currentTimeMillis());
		booking.setUserId(userId);
		booking.setFlight(flight);
		booking.setStatus(BookingStatus.CONFIRMED);
		booking.setBookingTime(LocalDateTime.now());

		bookingRepository.save(booking);

		for (Seat seat : seats) {
			BookingSeat bs = new BookingSeat();
			bs.setBookingId(booking.getBookingId());
			bs.setSeatId(seat.getSeatId());
			bookingSeatRepository.save(bs);
		}

		return buildBookingResponse(booking, seats);
	}

	public List<Booking> getUserBookings(String userId) {
        return bookingRepository.findByUserId(userId);
	}

	public List<BookingSeatResponse> getUserBookingsWithSeats(String userId) {

	    List<Booking> bookings =
	            bookingRepository.findBookingsWithFlight(userId);

	    return bookings.stream().map(booking -> {

	        List<String> seats =
	                bookingSeatRepository
	                        .findSeatNumbersByBookingId(booking.getBookingId());

	        return new BookingSeatResponse(
	                booking.getBookingCode(),
	                booking.getStatus().name(),
	                booking.getBookingTime(),
	                booking.getFlight(),
	                seats
	        );

	    }).toList();
	}

	private BookingResponse buildBookingResponse(Booking booking, List<Seat> seats) {

		BookingResponse response = new BookingResponse();
		response.setBookingCode(booking.getBookingCode());
		response.setFlightCode(booking.getFlight().getFlightCode());
		response.setSeatsBooked(seats.stream().map(Seat::getSeatNumber).collect(Collectors.toList()));
		response.setStatus(booking.getStatus().name());
		response.setBookingTime(booking.getBookingTime());
		response.setMessage("Booking confirmed");

		return response;
	}

	private void validateFlightForBooking(Flight flight) {

	    switch (flight.getStatus()) {

	        case DEPARTED:
	            throw new InvalidBookingException(
	                "Flight has already departed. Booking not allowed."
	            );

	        case CANCELLED:
	            throw new InvalidBookingException(
	                "Flight is cancelled. Booking not allowed."
	            );

	        case DELAYED:
	        	return;
	        	
	        case SCHEDULED:
	            return;

	        default:
	            throw new InvalidBookingException(
	                "Flight is not open for booking."
	            );
	    }
	}

	public BookingCancelResponse cancelBooking(String bookingCode) {
		Booking booking = bookingRepository.findByBookingCode(bookingCode)
				.orElseThrow(() -> new BookingNotFoundException("Booking Not Found"));

		if(booking.getStatus() == BookingStatus.CANCELLED) {
			throw new InvalidBookingCancelException("Booking is already cancelled.");
		}
		
		Flight flight = flightRepository.findByFlightCode(booking.getFlight().getFlightCode()).orElseThrow(
				() -> new FlightNotFoundException("Flight not found: " + booking.getFlight().getFlightCode()));

		List<String> bookingSeats = bookingSeatRepository.findSeatNumbersByBookingId(booking.getBookingId());
		
		if (flight.getStatus() == FlightStatus.SCHEDULED || flight.getStatus() == FlightStatus.DELAYED) {
			int count = seatRepository.updateSeatStatus(flight.getFlightId(), bookingSeats);
			
			if (count != bookingSeats.size()) {
				throw new SeatNotAvailableException("One or more seats cannot be cancelled.");
			}
			
			bookingSeatRepository.deleteBookingSeats(booking.getBookingId());
			booking.setStatus(BookingStatus.CANCELLED);
			bookingRepository.save(booking);
			
		} else if (flight.getStatus() == FlightStatus.DEPARTED) {
			throw new InvalidBookingCancelException("Cannot cancel booking. Flight has already departed.");
		} else if (flight.getStatus() == FlightStatus.CANCELLED) {
			throw new InvalidBookingCancelException("Flight is already cancelled.");
		}

		return new BookingCancelResponse(bookingCode, flight.getFlightCode(), flight.getSource(),
				flight.getDestination(), bookingSeats, BookingStatus.CANCELLED,
				"Your flight booking has been cancelled successfully");
	}

}
