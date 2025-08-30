-- Add missing tables that may not have been created properly

-- Create charge_point_configurations table if it doesn't exist
CREATE TABLE IF NOT EXISTS charge_point_configurations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    charging_station_id UUID NOT NULL,
    config_key VARCHAR(255) NOT NULL,
    is_readonly BOOLEAN DEFAULT FALSE,
    config_value TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (charging_station_id) REFERENCES charging_stations(id) ON DELETE CASCADE,
    UNIQUE(charging_station_id, config_key)
);

-- Create trigger if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'update_charge_point_configurations_updated_at') THEN
        CREATE TRIGGER update_charge_point_configurations_updated_at 
        BEFORE UPDATE ON charge_point_configurations 
        FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
    END IF;
END$$;