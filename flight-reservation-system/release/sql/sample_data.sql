INSERT INTO flight (
    flight_code,
    flight_name,
    source,
    destination,
    departure_time,
    arrival_time,
    status,
    total_seats
)
VALUES
(
    'AI101',
    'Air India Express',
    'DEL',
    'BOM',
    '2025-01-15 08:30',
    '2025-01-15 10:45',
    'SCHEDULED',
    100
);

INSERT INTO seat (flight_id, seat_number, status)
SELECT
    f.flight_id,
    CONCAT(row_num, col) AS seat_number,
    'AVAILABLE'
FROM flight f,
     generate_series(1, 25) AS row_num,
     (SELECT unnest(ARRAY['A','B','C','D']) AS col) cols
WHERE f.flight_code = 'AI101';
