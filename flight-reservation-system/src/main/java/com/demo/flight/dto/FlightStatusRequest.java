package com.demo.flight.dto;

import com.demo.flight.enums.FlightStatus;

import lombok.Data;

@Data
public class FlightStatusRequest {

	private FlightStatus status;
}
