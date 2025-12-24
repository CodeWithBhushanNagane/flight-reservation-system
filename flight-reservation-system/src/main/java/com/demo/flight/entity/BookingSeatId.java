package com.demo.flight.entity;

import java.io.Serializable;
import java.util.Objects;

public class BookingSeatId implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long bookingId;
	private Long seatId;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof BookingSeatId))
			return false;
		BookingSeatId that = (BookingSeatId) o;
		return Objects.equals(bookingId, that.bookingId) && Objects.equals(seatId, that.seatId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(bookingId, seatId);
	}
}
