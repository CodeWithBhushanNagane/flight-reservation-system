package com.demo.flight.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "booking_seat")
@IdClass(BookingSeatId.class)
@Data
public class BookingSeat {

	@Id
	@Column(name = "booking_id")
	private Long bookingId;

	@Id
	@Column(name = "seat_id")
	private Long seatId;
}
