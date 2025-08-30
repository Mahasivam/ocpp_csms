package com.csms.repository;

import com.csms.model.ChargingProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChargingProfileRepository extends JpaRepository<ChargingProfile, Long> {
    
    List<ChargingProfile> findByChargingStationId(UUID chargingStationId);
    
    List<ChargingProfile> findByChargingStationIdAndConnectorId(UUID chargingStationId, Integer connectorId);
    
    Optional<ChargingProfile> findByChargingStationIdAndChargingProfileId(UUID chargingStationId, Integer chargingProfileId);
    
    List<ChargingProfile> findByChargingStationIdAndChargingProfilePurpose(UUID chargingStationId, String purpose);
    
    @Query("SELECT cp FROM ChargingProfile cp WHERE cp.chargingStation.id = :chargingStationId " +
           "AND (:connectorId IS NULL OR cp.connectorId = :connectorId) " +
           "AND (:purpose IS NULL OR cp.chargingProfilePurpose = :purpose) " +
           "AND (:stackLevel IS NULL OR cp.stackLevel = :stackLevel)")
    List<ChargingProfile> findByFilters(UUID chargingStationId, Integer connectorId, String purpose, Integer stackLevel);
}