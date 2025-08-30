-- Add missing meter value columns to transactions table

ALTER TABLE transactions 
ADD COLUMN IF NOT EXISTS start_meter_value INTEGER,
ADD COLUMN IF NOT EXISTS end_meter_value INTEGER;

-- Copy data from existing columns if they exist and new columns are null
UPDATE transactions 
SET start_meter_value = meter_start 
WHERE start_meter_value IS NULL AND meter_start IS NOT NULL;

UPDATE transactions 
SET end_meter_value = meter_stop 
WHERE end_meter_value IS NULL AND meter_stop IS NOT NULL;

-- Add missing columns for proper OCPP compliance
ALTER TABLE transactions
ADD COLUMN IF NOT EXISTS end_timestamp TIMESTAMP,
ADD COLUMN IF NOT EXISTS reason VARCHAR(255);

-- Copy data from existing columns
UPDATE transactions 
SET end_timestamp = stop_timestamp 
WHERE end_timestamp IS NULL AND stop_timestamp IS NOT NULL;

UPDATE transactions 
SET reason = stop_reason 
WHERE reason IS NULL AND stop_reason IS NOT NULL;