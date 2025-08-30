-- Default ID Tags for testing
INSERT INTO id_tags (tag_id, status) VALUES
('04E91C5A123456', 'Accepted'),
('RFID001', 'Accepted'),
('RFID002', 'Accepted'),
('TEST123', 'Accepted'),
('BLOCKED001', 'Blocked');

-- Default configuration keys that all charge points should have
-- This will be used as a template for new charge points
CREATE TABLE IF NOT EXISTS default_configuration (
    key VARCHAR(255) PRIMARY KEY,
    value TEXT,
    readonly BOOLEAN DEFAULT FALSE,
    description TEXT
);

INSERT INTO default_configuration (key, value, readonly, description) VALUES
('HeartbeatInterval', '300', FALSE, 'Interval of inactivity (in seconds) after which the Charge Point or Central System sends out a heartbeat signal'),
('ConnectionTimeOut', '60', FALSE, 'Time (in seconds) after which the Charge Point closes idle connections'),
('MeterValueSampleInterval', '60', FALSE, 'Sampling interval (in seconds) for meters'),
('ClockAlignedDataInterval', '900', FALSE, 'Clock aligned data interval (in seconds)'),
('MeterValuesSampledData', 'Energy.Active.Import.Register', FALSE, 'Sampled measurands to be included in MeterValues'),
('MeterValuesAlignedData', 'Energy.Active.Import.Register', FALSE, 'Clock aligned measurand(s) to be included in MeterValues'),
('StopTransactionOnEVSideDisconnect', 'true', FALSE, 'Whether a transaction is automatically stopped when a disconnect is detected'),
('StopTransactionOnInvalidId', 'true', FALSE, 'Whether a transaction is stopped when an invalid ID is presented'),
('TransactionMessageAttempts', '3', FALSE, 'Number of times to retry transaction start/stop messages'),
('TransactionMessageRetryInterval', '60', FALSE, 'Retry interval (in seconds) for transaction messages'),
('UnlockConnectorOnEVSideDisconnect', 'true', FALSE, 'Whether the connector is unlocked when a disconnect is detected'),
('LocalAuthorizeOffline', 'true', FALSE, 'Whether the Charge Point can authorize locally when offline'),
('LocalPreAuthorize', 'false', FALSE, 'Whether the Charge Point can preauthorize locally'),
('NumberOfConnectors', '2', TRUE, 'Number of connectors on this Charge Point'),
('ChargePointVendor', 'OCPP CSMS', TRUE, 'Charge point vendor'),
('ChargePointModel', 'Model-1', TRUE, 'Charge point model'),
('ChargePointSerialNumber', '', TRUE, 'Charge point serial number'),
('FirmwareVersion', '1.0.0', TRUE, 'Firmware version'),
('SupportedFeatureProfiles', 'Core,LocalAuthListManagement,RemoteTrigger,Reservation,SmartCharging', TRUE, 'Supported feature profiles'),
('LocalAuthListEnabled', 'true', FALSE, 'Whether the Local Authorization List is enabled'),
('LocalAuthListMaxLength', '100', TRUE, 'Maximum number of identifications that can be stored locally'),
('SendLocalListMaxLength', '20', TRUE, 'Maximum number of identifications that can be send in a single SendLocalList message'),
('ReserveConnectorZeroSupported', 'false', TRUE, 'Whether the Charge Point can reserve connector 0'),
('AuthorizeRemoteTxRequests', 'true', FALSE, 'Whether the Charge Point supports remote authorization checks');