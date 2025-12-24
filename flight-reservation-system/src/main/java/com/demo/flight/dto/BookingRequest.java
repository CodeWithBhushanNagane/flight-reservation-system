package com.demo.flight.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequest {

	@NotNull
	private String flightCode;

	@NotEmpty
	private List<String> requestedSeats;
}
