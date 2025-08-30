-- Fix additional column type mismatches

-- Fix min_charging_rate column type in charging_schedules table
ALTER TABLE charging_schedules 
ALTER COLUMN min_charging_rate TYPE DOUBLE PRECISION;