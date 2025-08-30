package com.csms.repository;

import com.csms.model.LocalAuthorizationList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocalAuthorizationListRepository extends JpaRepository<LocalAuthorizationList, Long> {
    
    Optional<LocalAuthorizationList> findByChargingStationId(UUID chargingStationId);
    
    @Query("SELECT MAX(l.listVersion) FROM LocalAuthorizationList l WHERE l.chargingStation.id = :chargingStationId")
    Optional<Integer> findLatestVersionByChargingStationId(UUID chargingStationId);
}