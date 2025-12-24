package com.demo.flight.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.demo.flight.entity.Seat;

import jakarta.persistence.LockModeType;

public interface SeatRepository extends JpaRepository<Seat, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM Seat s WHERE s.flight.flightId = :flightId AND s.seatNumber IN :seatNumbers")
	List<Seat> lockSeatsForBooking(@Param("flightId") Long flightId, @Param("seatNumbers") List<String> seatNumbers);

	@Query("SELECT s FROM Seat s WHERE s.flight.flightId = :flightId AND s.status = 'AVAILABLE'")
	List<Seat> findAvailableSeats(@Param("flightId") Long flightId);
	
	@Modifying
	@Transactional
	@Query("UPDATE Seat s SET s.status = 'AVAILABLE' WHERE s.flight.flightId = :flightId AND s.seatNumber IN :seatNumbers")
	int updateSeatStatus(@Param("flightId") Long flightId, @Param("seatNumbers") List<String> seatNumbers);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO seat (flight_id, seat_number, status)
        SELECT
            f.flight_id,
            CONCAT(row_num, col) AS seat_number,
            'AVAILABLE'
        FROM flight f,
             generate_series(1, 25) AS row_num,
             (SELECT unnest(ARRAY['A','B','C','D']) AS col) cols
        WHERE f.flight_code = :flightCode
        """, nativeQuery = true)
    int generateSeatsForFlight(@Param("flightCode") String flightCode);
}
