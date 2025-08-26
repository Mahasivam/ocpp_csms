package com.csms.repository;

import com.csms.model.Connector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConnectorRepository extends JpaRepository<Connector, UUID> {
    List<Connector> findByChargingStationId(UUID chargingStationId);
    Optional<Connector> findByChargingStationIdAndConnectorId(UUID chargingStationId, Integer connectorId);
    List<Connector> findByStatus(String status);
}

