-- Add all missing columns across tables to match JPA entities

-- Add missing columns to id_tags table
ALTER TABLE id_tags 
ADD COLUMN IF NOT EXISTS parent_id_tag VARCHAR(255);

-- Add any other commonly missing columns that might be expected by entities
-- Based on OCPP standard, these are typical columns that might be missing:

-- Add missing columns to transactions table if needed
ALTER TABLE transactions 
ADD COLUMN IF NOT EXISTS reservation_id INTEGER,
ADD COLUMN IF NOT EXISTS id_tag_info TEXT;

-- Add missing columns to reservations table if needed  
ALTER TABLE reservations
ADD COLUMN IF NOT EXISTS connector_id INTEGER;

-- Add missing columns to meter_values table if needed
ALTER TABLE meter_values
ADD COLUMN IF NOT EXISTS sampling_value TEXT;

-- Add missing columns to ocpp_messages table if needed
ALTER TABLE ocpp_messages
ADD COLUMN IF NOT EXISTS session_id VARCHAR(255),
ADD COLUMN IF NOT EXISTS response_payload JSONB;

-- Make sure tag_id column exists and is properly mapped
-- The original migration had tag_id but entity expects id_tag, so we need both
UPDATE id_tags SET id_tag = tag_id WHERE id_tag IS NULL AND tag_id IS NOT NULL;