package com.demo.flight.exception;

public class InvalidFlightStatusException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidFlightStatusException(String message) {
		super(message);
	}
}

