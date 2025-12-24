INSERT INTO seat (flight_id, seat_number, status)
SELECT
    f.flight_id,
    CONCAT(row_num, col) AS seat_number,
    'AVAILABLE'
FROM flight f,
     generate_series(1, 25) AS row_num,
     (SELECT unnest(ARRAY['A','B','C','D']) AS col) cols
WHERE f.flight_code = 'AI101';
