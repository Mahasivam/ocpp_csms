package com.csms.repository;

import com.csms.model.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChargingStationRepository extends JpaRepository<ChargingStation, UUID> {
    Optional<ChargingStation> findByChargePointId(String chargePointId);
    List<ChargingStation> findByRegistrationStatus(String status);

    @Query("SELECT cs FROM ChargingStation cs WHERE cs.lastHeartbeat < :threshold")
    List<ChargingStation> findOfflineStations(LocalDateTime threshold);
}
