-- OCPP 1.6J Additional Tables

-- Firmware Updates
CREATE TABLE firmware_updates (
    id BIGSERIAL PRIMARY KEY,
    charging_station_id UUID NOT NULL,
    location VARCHAR(500) NOT NULL,
    retrieve_date TIMESTAMP NOT NULL,
    retries INTEGER,
    retry_interval INTEGER,
    status VARCHAR(50) NOT NULL DEFAULT 'Idle',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (charging_station_id) REFERENCES charging_stations(id) ON DELETE CASCADE
);

-- Local Authorization Lists
CREATE TABLE local_authorization_list (
    id BIGSERIAL PRIMARY KEY,
    charging_station_id UUID NOT NULL,
    list_version INTEGER NOT NULL,
    update_type VARCHAR(20) NOT NULL, -- Differential, Full
    status VARCHAR(30) NOT NULL DEFAULT 'Accepted', -- Accepted, Failed, NotSupported, VersionMismatch
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (charging_station_id) REFERENCES charging_stations(id) ON DELETE CASCADE
);

-- Charging Profiles
CREATE TABLE charging_profiles (
    id BIGSERIAL PRIMARY KEY,
    charging_station_id UUID NOT NULL,
    charging_profile_id INTEGER NOT NULL,
    connector_id INTEGER,
    transaction_id INTEGER,
    stack_level INTEGER,
    charging_profile_purpose VARCHAR(50) NOT NULL, -- ChargePointMaxProfile, TxDefaultProfile, TxProfile
    charging_profile_kind VARCHAR(20) NOT NULL, -- Absolute, Recurring, Relative
    recurrency_kind VARCHAR(10), -- Daily, Weekly
    valid_from TIMESTAMP,
    valid_to TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'Accepted', -- Accepted, Rejected, NotSupported
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (charging_station_id) REFERENCES charging_stations(id) ON DELETE CASCADE
);

-- Charging Schedules
CREATE TABLE charging_schedules (
    id BIGSERIAL PRIMARY KEY,
    charging_profile_id BIGINT NOT NULL,
    duration INTEGER,
    start_schedule TIMESTAMP,
    charging_rate_unit VARCHAR(5) NOT NULL, -- W, A
    min_charging_rate DECIMAL(10,3),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (charging_profile_id) REFERENCES charging_profiles(id) ON DELETE CASCADE
);

-- Charging Schedule Periods
CREATE TABLE charging_schedule_periods (
    id BIGSERIAL PRIMARY KEY,
    charging_schedule_id BIGINT NOT NULL,
    start_period INTEGER NOT NULL,
    limit_value DECIMAL(10,3) NOT NULL,
    number_phases INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (charging_schedule_id) REFERENCES charging_schedules(id) ON DELETE CASCADE
);

-- Add indexes for performance
CREATE INDEX idx_firmware_updates_station_id ON firmware_updates(charging_station_id);
CREATE INDEX idx_firmware_updates_status ON firmware_updates(status);
CREATE INDEX idx_local_auth_station_id ON local_authorization_list(charging_station_id);
CREATE INDEX idx_charging_profiles_station_id ON charging_profiles(charging_station_id);
CREATE INDEX idx_charging_profiles_connector_id ON charging_profiles(connector_id);
CREATE INDEX idx_charging_profiles_profile_id ON charging_profiles(charging_profile_id);
CREATE INDEX idx_charging_schedules_profile_id ON charging_schedules(charging_profile_id);
CREATE INDEX idx_charging_schedule_periods_schedule_id ON charging_schedule_periods(charging_schedule_id);