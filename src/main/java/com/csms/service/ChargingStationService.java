package com.csms.service;

import com.csms.model.ChargingStation;
import com.csms.model.Connector;
import com.csms.repository.ChargingStationRepository;
import com.csms.repository.ConnectorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChargingStationService {

    private final ChargingStationRepository chargingStationRepository;
    private final ConnectorRepository connectorRepository;

    @Transactional
    public ChargingStation registerChargingStation(String chargePointId,
                                                   String vendor,
                                                   String model,
                                                   String serialNumber,
                                                   String firmwareVersion) {
        Optional<ChargingStation> existingStation = chargingStationRepository.findByChargePointId(chargePointId);

        ChargingStation station;
        if (existingStation.isPresent()) {
            station = existingStation.get();
            station.setChargePointVendor(vendor);
            station.setChargePointModel(model);
            station.setChargePointSerialNumber(serialNumber);
            station.setFirmwareVersion(firmwareVersion);
            station.setRegistrationStatus("Accepted");
            station.setIsRegistered(true);
        } else {
            station = new ChargingStation();
            station.setChargePointId(chargePointId);
            station.setChargePointVendor(vendor);
            station.setChargePointModel(model);
            station.setChargePointSerialNumber(serialNumber);
            station.setFirmwareVersion(firmwareVersion);
            station.setRegistrationStatus("Accepted");
            station.setIsRegistered(true);
        }

        station.setLastHeartbeat(LocalDateTime.now());
        return chargingStationRepository.save(station);
    }

    @Transactional
    public void updateHeartbeat(String chargePointId) {
        chargingStationRepository.findByChargePointId(chargePointId)
                .ifPresent(station -> {
                    station.setLastHeartbeat(LocalDateTime.now());
                    chargingStationRepository.save(station);
                });
    }

    public Optional<ChargingStation> findByChargePointId(String chargePointId) {
        return chargingStationRepository.findByChargePointId(chargePointId);
    }

    public List<ChargingStation> findAll() {
        return chargingStationRepository.findAll();
    }

    public List<ChargingStation> findOfflineStations(int timeoutMinutes) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(timeoutMinutes);
        return chargingStationRepository.findOfflineStations(threshold);
    }

    @Transactional
    public void updateConnectorStatus(UUID chargingStationId,
                                      Integer connectorId,
                                      String status,
                                      String errorCode,
                                      String info) {
        Optional<Connector> connectorOpt = connectorRepository
                .findByChargingStationIdAndConnectorId(chargingStationId, connectorId);

        if (connectorOpt.isPresent()) {
            Connector connector = connectorOpt.get();
            connector.setStatus(status);
            connector.setErrorCode(errorCode != null ? errorCode : "NoError");
            connector.setInfo(info);
            connectorRepository.save(connector);
        } else {
            // Create new connector if it doesn't exist
            ChargingStation station = chargingStationRepository.findById(chargingStationId).orElse(null);
            if (station != null) {
                Connector connector = new Connector();
                connector.setChargingStation(station);
                connector.setConnectorId(connectorId);
                connector.setStatus(status);
                connector.setErrorCode(errorCode != null ? errorCode : "NoError");
                connector.setInfo(info);
                connectorRepository.save(connector);
            }
        }
    }
}
