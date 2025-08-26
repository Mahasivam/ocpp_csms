package com.csms.service;

import com.csms.model.ChargingStation;
import com.csms.model.ChargePointConfiguration;
import com.csms.repository.ChargePointConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigurationService {

    private final ChargePointConfigurationRepository configurationRepository;

    @Transactional
    public void initializeDefaultConfiguration(ChargingStation station) {
        // Check if configuration already exists
        List<ChargePointConfiguration> existingConfig =
                configurationRepository.findByChargingStationId(station.getId());

        if (existingConfig.isEmpty()) {
            Map<String, Object> defaultConfig = getDefaultConfiguration();

            for (Map.Entry<String, Object> entry : defaultConfig.entrySet()) {
                ChargePointConfiguration config = new ChargePointConfiguration();
                config.setChargingStation(station);
                config.setConfigKey(entry.getKey());
                config.setConfigValue(entry.getValue().toString());
                config.setIsReadonly(isReadOnlyKey(entry.getKey()));

                configurationRepository.save(config);
            }

            log.info("Initialized default configuration for charge point: {}",
                    station.getChargePointId());
        }
    }

    public List<ChargePointConfiguration> getConfiguration(UUID chargingStationId) {
        return configurationRepository.findByChargingStationId(chargingStationId);
    }

    public Optional<String> getConfigurationValue(UUID chargingStationId, String key) {
        return configurationRepository.findByChargingStationIdAndConfigKey(chargingStationId, key)
                .map(ChargePointConfiguration::getConfigValue);
    }

    @Transactional
    public boolean setConfiguration(UUID chargingStationId, String key, String value) {
        Optional<ChargePointConfiguration> configOpt =
                configurationRepository.findByChargingStationIdAndConfigKey(chargingStationId, key);

        if (configOpt.isPresent()) {
            ChargePointConfiguration config = configOpt.get();
            if (config.getIsReadonly()) {
                log.warn("Attempted to modify readonly configuration key: {}", key);
                return false;
            }
            config.setConfigValue(value);
            configurationRepository.save(config);
            return true;
        } else {
            log.warn("Configuration key not found: {}", key);
            return false;
        }
    }

    private Map<String, Object> getDefaultConfiguration() {
        Map<String, Object> config = new HashMap<>();

        // Core Configuration Keys (OCPP 1.6J)
        config.put("AllowOfflineTxForUnknownId", false);
        config.put("AuthorizationCacheEnabled", true);
        config.put("AuthorizeRemoteTxRequests", true);
        config.put("BlinkRepeat", 0);
        config.put("ClockAlignedDataInterval", 900);
        config.put("ConnectionTimeOut", 60);
        config.put("GetConfigurationMaxKeys", 50);
        config.put("HeartbeatInterval", 300);
        config.put("LightIntensity", 100);
        config.put("LocalAuthorizeOffline", true);
        config.put("LocalPreAuthorize", false);
        config.put("MaxEnergyOnInvalidId", 0);
        config.put("MeterValuesAlignedData", "Energy.Active.Import.Register");
        config.put("MeterValuesSampledData", "Energy.Active.Import.Register");
        config.put("MeterValueSampleInterval", 60);
        config.put("NumberOfConnectors", 2);
        config.put("ResetRetries", 3);
        config.put("ConnectorPhaseRotation", "NotApplicable");
        config.put("StopTransactionOnEVSideDisconnect", true);
        config.put("StopTransactionOnInvalidId", true);
        config.put("StopTxnAlignedData", "Energy.Active.Import.Register");
        config.put("StopTxnSampledData", "Energy.Active.Import.Register");
        config.put("SupportedFeatureProfiles", "Core,FirmwareManagement,RemoteTrigger");
        config.put("TransactionMessageAttempts", 3);
        config.put("TransactionMessageRetryInterval", 60);
        config.put("UnlockConnectorOnEVSideDisconnect", true);
        config.put("WebSocketPingInterval", 0);

        return config;
    }

    private boolean isReadOnlyKey(String key) {
        return List.of(
                "GetConfigurationMaxKeys",
                "NumberOfConnectors",
                "SupportedFeatureProfiles",
                "LocalAuthListMaxLength",
                "SendLocalListMaxLength"
        ).contains(key);
    }
}