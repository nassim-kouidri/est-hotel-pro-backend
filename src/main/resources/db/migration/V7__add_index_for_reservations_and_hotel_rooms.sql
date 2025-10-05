-- Reservations
CREATE INDEX IF NOT EXISTS idx_reservation_room_dates
    ON reservation (hotel_room_id, start_date, end_date);
CREATE INDEX IF NOT EXISTS idx_reservation_status ON reservation (status);
CREATE INDEX IF NOT EXISTS idx_reservation_payment_status ON reservation (payment_status);
CREATE INDEX IF NOT EXISTS idx_reservation_company_name ON reservation (company_name);
CREATE INDEX IF NOT EXISTS idx_reservation_completed ON reservation (completed);

-- Hotel Rooms
CREATE INDEX IF NOT EXISTS idx_room_category_available ON hotel_room (category, available);
CREATE INDEX IF NOT EXISTS idx_room_available ON hotel_room (available);