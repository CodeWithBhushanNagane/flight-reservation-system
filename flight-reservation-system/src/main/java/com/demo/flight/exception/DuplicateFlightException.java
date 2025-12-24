package com.demo.flight.exception;

public class DuplicateFlightException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public DuplicateFlightException(String message) {
        super(message);
    }
}