package com.demo.flight.util;

import com.demo.flight.dto.FlightRequest;
import com.demo.flight.dto.FlightResponse;
import com.demo.flight.entity.Flight;
import com.demo.flight.enums.FlightStatus;

public class FlightMapper {

	public static Flight toEntity(FlightRequest dto) {
		Flight flight = new Flight();
		flight.setFlightName(dto.getFlightName());
		flight.setFlightCode(dto.getFlightCode());
		flight.setSource(dto.getSource());
		flight.setDestination(dto.getDestination());
		flight.setDepartureTime(dto.getDepartureTime());
		flight.setArrivalTime(dto.getArrivalTime());
		flight.setTotalSeats(dto.getTotalSeats());
		flight.setStatus(FlightStatus.SCHEDULED);
		return flight;
	}

	public static FlightResponse toDto(Flight flight) {
		return new FlightResponse(flight.getFlightCode(), flight.getSource(), flight.getDestination(),
				flight.getDepartureTime(), flight.getArrivalTime(), flight.getStatus().name());
	}
}
