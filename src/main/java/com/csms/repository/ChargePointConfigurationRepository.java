package com.csms.repository;

import com.csms.model.ChargePointConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChargePointConfigurationRepository extends JpaRepository<ChargePointConfiguration, UUID> {
    List<ChargePointConfiguration> findByChargingStationId(UUID chargingStationId);
    Optional<ChargePointConfiguration> findByChargingStationIdAndConfigKey(UUID chargingStationId, String configKey);
    void deleteByChargingStationIdAndConfigKey(UUID chargingStationId, String configKey);
}