package com.demo.flight.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.demo.flight.entity.Flight;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingSeatResponse {

	private String bookingCode;
    private String status;
    private LocalDateTime bookingTime;
    private Flight flight;
    private List<String> seats;
}
