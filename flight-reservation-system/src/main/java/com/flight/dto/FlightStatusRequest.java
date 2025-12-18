package com.flight.dto;

import com.flight.enums.FlightStatus;

import lombok.Data;

@Data
public class FlightStatusRequest {

	private FlightStatus status;
}
