package com.csms.repository;

import com.csms.model.MeterValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MeterValueRepository extends JpaRepository<MeterValue, UUID> {
    List<MeterValue> findByChargingStationIdAndConnectorId(UUID chargingStationId, Integer connectorId);
    List<MeterValue> findByTransactionId(Integer transactionId);
    List<MeterValue> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}