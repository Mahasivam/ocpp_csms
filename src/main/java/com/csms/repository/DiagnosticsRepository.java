package com.csms.repository;

import com.csms.model.Diagnostics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiagnosticsRepository extends JpaRepository<Diagnostics, UUID> {
    
    List<Diagnostics> findByChargingStationId(UUID chargingStationId);
    
    Optional<Diagnostics> findByChargingStationIdAndStatus(UUID chargingStationId, String status);
    
    @Query("SELECT d FROM Diagnostics d WHERE d.chargingStation.id = :chargingStationId ORDER BY d.createdAt DESC")
    List<Diagnostics> findByChargingStationIdOrderByCreatedAtDesc(UUID chargingStationId);
}