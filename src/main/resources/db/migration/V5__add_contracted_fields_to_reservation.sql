-- Add isContracted and companyName fields to reservation table
ALTER TABLE reservation
    ADD COLUMN IF NOT EXISTS is_contracted BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS company_name VARCHAR(255);