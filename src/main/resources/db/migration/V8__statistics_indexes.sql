-- Purpose: Add indexes to optimize statistics queries (date overlaps, filters, groupings)
-- This migration is safe to run multiple times thanks to IF NOT EXISTS guards.

-- Reservation table indexes
CREATE INDEX IF NOT EXISTS idx_reservation_start_date ON reservation (start_date);
CREATE INDEX IF NOT EXISTS idx_reservation_end_date   ON reservation (end_date);
CREATE INDEX IF NOT EXISTS idx_reservation_start_end  ON reservation (start_date, end_date);
CREATE INDEX IF NOT EXISTS idx_reservation_status     ON reservation (status);
CREATE INDEX IF NOT EXISTS idx_reservation_payment_status ON reservation (payment_status);
CREATE INDEX IF NOT EXISTS idx_reservation_is_contracted ON reservation (is_contracted);
CREATE INDEX IF NOT EXISTS idx_reservation_company_name  ON reservation (company_name);

-- Hotel room indexes
CREATE INDEX IF NOT EXISTS idx_hotel_room_category ON hotel_room (category);
