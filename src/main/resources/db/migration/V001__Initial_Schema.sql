-- OCPP CSMS Database Schema
-- Version: 1.0.0

-- Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Charging Stations
CREATE TABLE charging_stations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    charge_point_id VARCHAR(255) UNIQUE NOT NULL,
    charge_point_vendor VARCHAR(255),
    charge_point_model VARCHAR(255),
    charge_point_serial_number VARCHAR(255),
    firmware_version VARCHAR(255),
    status VARCHAR(50) DEFAULT 'Unknown',
    last_heartbeat TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Connectors
CREATE TABLE connectors (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    charging_station_id UUID NOT NULL,
    connector_id INTEGER NOT NULL,
    status VARCHAR(50) DEFAULT 'Unknown',
    error_code VARCHAR(50) DEFAULT 'NoError',
    info VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (charging_station_id) REFERENCES charging_stations(id) ON DELETE CASCADE,
    UNIQUE(charging_station_id, connector_id)
);

-- ID Tags (RFID/Authentication)
CREATE TABLE id_tags (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tag_id VARCHAR(255) UNIQUE NOT NULL,
    parent_tag_id VARCHAR(255),
    expiry_date TIMESTAMP,
    status VARCHAR(50) DEFAULT 'Accepted',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transactions
CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    transaction_id INTEGER UNIQUE NOT NULL,
    charging_station_id UUID NOT NULL,
    connector_id INTEGER NOT NULL,
    id_tag VARCHAR(255) NOT NULL,
    start_timestamp TIMESTAMP NOT NULL,
    meter_start INTEGER NOT NULL,
    stop_timestamp TIMESTAMP,
    meter_stop INTEGER,
    stop_reason VARCHAR(50),
    status VARCHAR(50) DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (charging_station_id) REFERENCES charging_stations(id) ON DELETE CASCADE
);

-- Reservations
CREATE TABLE reservations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reservation_id INTEGER UNIQUE NOT NULL,
    charging_station_id UUID NOT NULL,
    connector_id INTEGER NOT NULL,
    id_tag VARCHAR(255) NOT NULL,
    parent_id_tag VARCHAR(255),
    start_timestamp TIMESTAMP NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    status VARCHAR(50) DEFAULT 'Reserved',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (charging_station_id) REFERENCES charging_stations(id) ON DELETE CASCADE
);

-- Charge Point Configuration
CREATE TABLE charge_point_configurations (
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

-- Meter Values
CREATE TABLE meter_values (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    charging_station_id UUID NOT NULL,
    connector_id INTEGER NOT NULL,
    transaction_id INTEGER,
    timestamp TIMESTAMP NOT NULL,
    measurand VARCHAR(100),
    phase VARCHAR(20),
    location VARCHAR(50),
    unit VARCHAR(50),
    context VARCHAR(50),
    format VARCHAR(50),
    value VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (charging_station_id) REFERENCES charging_stations(id) ON DELETE CASCADE
);

-- OCPP Messages Log
CREATE TABLE ocpp_messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    charge_point_id VARCHAR(255) NOT NULL,
    message_id VARCHAR(255),
    message_type VARCHAR(50) NOT NULL,
    action VARCHAR(100),
    payload JSONB,
    direction VARCHAR(20) NOT NULL, -- 'INCOMING' or 'OUTGOING'
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed BOOLEAN DEFAULT FALSE,
    error_code VARCHAR(50),
    error_description TEXT
);

-- Diagnostics
CREATE TABLE diagnostics (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    charging_station_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    file_name VARCHAR(255),
    upload_timestamp TIMESTAMP,
    failure_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (charging_station_id) REFERENCES charging_stations(id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX idx_charging_stations_charge_point_id ON charging_stations(charge_point_id);
CREATE INDEX idx_connectors_charging_station_id ON connectors(charging_station_id);
CREATE INDEX idx_transactions_charging_station_id ON transactions(charging_station_id);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_reservations_charging_station_id ON reservations(charging_station_id);
CREATE INDEX idx_reservations_expiry_date ON reservations(expiry_date);
CREATE INDEX idx_meter_values_charging_station_id ON meter_values(charging_station_id);
CREATE INDEX idx_meter_values_timestamp ON meter_values(timestamp);
CREATE INDEX idx_ocpp_messages_charge_point_id ON ocpp_messages(charge_point_id);
CREATE INDEX idx_ocpp_messages_timestamp ON ocpp_messages(timestamp);

-- Sequences for transaction and reservation IDs
CREATE SEQUENCE IF NOT EXISTS transaction_id_seq START 1;
CREATE SEQUENCE IF NOT EXISTS reservation_id_seq START 1;

-- Update timestamp function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';

-- Triggers for updated_at
CREATE TRIGGER update_charging_stations_updated_at BEFORE UPDATE ON charging_stations FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_connectors_updated_at BEFORE UPDATE ON connectors FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_id_tags_updated_at BEFORE UPDATE ON id_tags FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_transactions_updated_at BEFORE UPDATE ON transactions FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_reservations_updated_at BEFORE UPDATE ON reservations FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_charge_point_configurations_updated_at BEFORE UPDATE ON charge_point_configurations FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_diagnostics_updated_at BEFORE UPDATE ON diagnostics FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();