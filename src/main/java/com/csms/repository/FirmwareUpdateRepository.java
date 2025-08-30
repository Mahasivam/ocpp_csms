package com.csms.repository;

import com.csms.model.FirmwareUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FirmwareUpdateRepository extends JpaRepository<FirmwareUpdate, Long> {
    
    List<FirmwareUpdate> findByChargingStationId(UUID chargingStationId);
    
    Optional<FirmwareUpdate> findByChargingStationIdAndStatus(UUID chargingStationId, FirmwareUpdate.FirmwareStatus status);
    
    @Query("SELECT f FROM FirmwareUpdate f WHERE f.chargingStation.id = :chargingStationId ORDER BY f.createdAt DESC")
    List<FirmwareUpdate> findByChargingStationIdOrderByCreatedAtDesc(UUID chargingStationId);
}