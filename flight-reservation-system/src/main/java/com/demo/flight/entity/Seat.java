package com.demo.flight.entity;

import com.demo.flight.enums.SeatStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(name = "seat", uniqueConstraints = @UniqueConstraint(columnNames = { "flight_id", "seat_number" }))
@Data
public class Seat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seat_id")
	private Long seatId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "flight_id")
	private Flight flight;

	@Column(name = "seat_number", nullable = false)
	private String seatNumber; // e.g. 12C

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SeatStatus status;
}
