package com.demo.flight.entity;

import java.time.LocalDateTime;

import com.demo.flight.enums.FlightStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "flight")
@Data
public class Flight {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "flight_id")
	private Long flightId;

	@Column(name = "flight_code", nullable = false, unique = true)
	private String flightCode;

	@Column(name = "flight_name", nullable = false)
	private String flightName;

	@Column(nullable = false)
	private String source;

	@Column(nullable = false)
	private String destination;

	@Column(name = "departure_time", nullable = false)
	private LocalDateTime departureTime;

	@Column(name = "arrival_time", nullable = false)
	private LocalDateTime arrivalTime;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private FlightStatus status;

	@Column(name = "total_seats", nullable = false)
	private int totalSeats;
}
