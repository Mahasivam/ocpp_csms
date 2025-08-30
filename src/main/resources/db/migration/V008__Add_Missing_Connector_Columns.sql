-- Add missing columns to connectors table

ALTER TABLE connectors 
ADD COLUMN IF NOT EXISTS vendor_id VARCHAR(255),
ADD COLUMN IF NOT EXISTS vendor_error_code VARCHAR(255),
ADD COLUMN IF NOT EXISTS current_transaction_id UUID;