package com.flight.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.flight.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// ========================
	// 404 - Not Found
	// ========================
	@ExceptionHandler(FlightNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleFlightNotFound(FlightNotFoundException ex, WebRequest request) {

		return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
	}

	// ========================
	// 400 - Bad Request
	// ========================
	@ExceptionHandler({ SeatNotAvailableException.class, InvalidBookingException.class })
	public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex, WebRequest request) {

		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	}

	// ========================
	// 500 - Internal Server Error
	// ========================
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, WebRequest request) {

		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", request);
	}

	// ========================
	// Helper Method
	// ========================
	private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, WebRequest request) {

		ErrorResponse error = new ErrorResponse();
		error.setTimestamp(LocalDateTime.now());
		error.setStatus(status.value());
		error.setError(status.getReasonPhrase());
		error.setMessage(message);
		return ResponseEntity.status(status).body(error);
	}
}
