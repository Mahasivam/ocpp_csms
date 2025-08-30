package com.csms.service;

import com.csms.dto.ocpp.*;
import com.csms.model.ChargingStation;
import com.csms.model.Diagnostics;
import com.csms.model.FirmwareUpdate;
import com.csms.repository.DiagnosticsRepository;
import com.csms.repository.FirmwareUpdateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirmwareManagementService {
    
    private final FirmwareUpdateRepository firmwareUpdateRepository;
    private final DiagnosticsRepository diagnosticsRepository;
    @Lazy
    private final RemoteCommandService remoteCommandService;
    private final ChargingStationService chargingStationService;
    
    @Transactional
    public CompletableFuture<String> updateFirmware(String chargePointId, String location, 
                                                   LocalDateTime retrieveDate, Integer retries, Integer retryInterval) {
        try {
            Optional<ChargingStation> stationOpt = chargingStationService.findByChargePointId(chargePointId);
            if (stationOpt.isEmpty()) {
                log.error("Charging station not found: {}", chargePointId);
                return CompletableFuture.failedFuture(new RuntimeException("Charging station not found"));
            }
            
            ChargingStation station = stationOpt.get();
            
            // Create firmware update record
            FirmwareUpdate firmwareUpdate = new FirmwareUpdate();
            firmwareUpdate.setChargingStation(station);
            firmwareUpdate.setLocation(location);
            firmwareUpdate.setRetrieveDate(retrieveDate);
            firmwareUpdate.setRetries(retries);
            firmwareUpdate.setRetryInterval(retryInterval);
            firmwareUpdate.setStatus(FirmwareUpdate.FirmwareStatus.Idle);
            
            firmwareUpdateRepository.save(firmwareUpdate);
            
            // Send UpdateFirmware command to charge point
            UpdateFirmwareRequest request = new UpdateFirmwareRequest(location, retrieveDate, retries, retryInterval);
            return remoteCommandService.sendCommand(chargePointId, "UpdateFirmware", request);
            
        } catch (Exception e) {
            log.error("Error initiating firmware update for {}: {}", chargePointId, e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    @Transactional
    public CompletableFuture<String> getDiagnostics(String chargePointId, String location, 
                                                   Integer retries, Integer retryInterval,
                                                   LocalDateTime startTime, LocalDateTime stopTime) {
        try {
            Optional<ChargingStation> stationOpt = chargingStationService.findByChargePointId(chargePointId);
            if (stationOpt.isEmpty()) {
                log.error("Charging station not found: {}", chargePointId);
                return CompletableFuture.failedFuture(new RuntimeException("Charging station not found"));
            }
            
            ChargingStation station = stationOpt.get();
            
            // Create diagnostics record
            Diagnostics diagnostics = new Diagnostics();
            diagnostics.setChargingStation(station);
            diagnostics.setLocation(location);
            diagnostics.setRetries(retries);
            diagnostics.setRetryInterval(retryInterval);
            diagnostics.setStartTime(startTime);
            diagnostics.setStopTime(stopTime);
            diagnostics.setStatus("Idle");
            
            diagnosticsRepository.save(diagnostics);
            
            // Send GetDiagnostics command to charge point
            GetDiagnosticsRequest request = new GetDiagnosticsRequest(location, retries, retryInterval, startTime, stopTime);
            return remoteCommandService.sendCommand(chargePointId, "GetDiagnostics", request);
            
        } catch (Exception e) {
            log.error("Error initiating diagnostics retrieval for {}: {}", chargePointId, e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    @Transactional
    public void updateFirmwareStatus(String chargePointId, String status) {
        Optional<ChargingStation> stationOpt = chargingStationService.findByChargePointId(chargePointId);
        if (stationOpt.isEmpty()) {
            log.error("Charging station not found: {}", chargePointId);
            return;
        }
        
        ChargingStation station = stationOpt.get();
        Optional<FirmwareUpdate> firmwareUpdateOpt = firmwareUpdateRepository
                .findByChargingStationIdOrderByCreatedAtDesc(station.getId())
                .stream()
                .findFirst();
        
        if (firmwareUpdateOpt.isPresent()) {
            FirmwareUpdate firmwareUpdate = firmwareUpdateOpt.get();
            try {
                firmwareUpdate.setStatus(FirmwareUpdate.FirmwareStatus.valueOf(status));
                firmwareUpdateRepository.save(firmwareUpdate);
                log.info("Updated firmware status for {} to {}", chargePointId, status);
            } catch (IllegalArgumentException e) {
                log.error("Invalid firmware status: {}", status);
            }
        }
    }
    
    @Transactional
    public void updateDiagnosticsStatus(String chargePointId, String status) {
        Optional<ChargingStation> stationOpt = chargingStationService.findByChargePointId(chargePointId);
        if (stationOpt.isEmpty()) {
            log.error("Charging station not found: {}", chargePointId);
            return;
        }
        
        ChargingStation station = stationOpt.get();
        Optional<Diagnostics> diagnosticsOpt = diagnosticsRepository
                .findByChargingStationIdOrderByCreatedAtDesc(station.getId())
                .stream()
                .findFirst();
        
        if (diagnosticsOpt.isPresent()) {
            Diagnostics diagnostics = diagnosticsOpt.get();
            diagnostics.setStatus(status);
            diagnosticsRepository.save(diagnostics);
            log.info("Updated diagnostics status for {} to {}", chargePointId, status);
        }
    }
    
    public List<FirmwareUpdate> getFirmwareUpdates(String chargePointId) {
        Optional<ChargingStation> stationOpt = chargingStationService.findByChargePointId(chargePointId);
        if (stationOpt.isEmpty()) {
            return List.of();
        }
        return firmwareUpdateRepository.findByChargingStationIdOrderByCreatedAtDesc(stationOpt.get().getId());
    }
    
    public List<Diagnostics> getDiagnosticsList(String chargePointId) {
        Optional<ChargingStation> stationOpt = chargingStationService.findByChargePointId(chargePointId);
        if (stationOpt.isEmpty()) {
            return List.of();
        }
        return diagnosticsRepository.findByChargingStationIdOrderByCreatedAtDesc(stationOpt.get().getId());
    }
}