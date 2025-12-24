package com.demo.flight.entity;

import java.time.LocalDateTime;

import com.demo.flight.enums.BookingStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "booking")
@Data
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "booking_id")
	private Long bookingId;

	@Column(name = "booking_code", nullable = false, unique = true)
	private String bookingCode;

	@Column(name = "user_id", nullable = false)
	private String userId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "flight_id")
	private Flight flight;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BookingStatus status;

	@Column(name = "booking_time", nullable = false)
	private LocalDateTime bookingTime;
}
