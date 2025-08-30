-- Fix column type mismatches between database and JPA entities

-- Fix limit_value column type in charging_schedule_periods table
ALTER TABLE charging_schedule_periods 
ALTER COLUMN limit_value TYPE DOUBLE PRECISION;

-- Fix min_charging_rate column type in charging_schedules table
ALTER TABLE charging_schedules 
ALTER COLUMN min_charging_rate TYPE DOUBLE PRECISION;