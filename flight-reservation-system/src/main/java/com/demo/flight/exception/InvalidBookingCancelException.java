package com.demo.flight.exception;

public class InvalidBookingCancelException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidBookingCancelException(String message) {
		super(message);
	}
}