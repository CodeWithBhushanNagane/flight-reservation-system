package com.flight.dto;

import java.time.LocalDate;

public record FlightSearchRequest(String source, String destination, LocalDate journeyDate) {

}
