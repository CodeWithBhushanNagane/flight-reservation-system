package com.demo.flight.exception;

public class BookingNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BookingNotFoundException(String message) {
		super(message);
	}
}
