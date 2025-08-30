package com.csms.service;

import com.csms.dto.ocpp.*;
import com.csms.model.ChargingStation;
import com.csms.model.LocalAuthorizationList;
import com.csms.repository.LocalAuthorizationListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalAuthListService {
    
    private final LocalAuthorizationListRepository localAuthListRepository;
    private final RemoteCommandService remoteCommandService;
    private final ChargingStationService chargingStationService;
    
    @Transactional
    public CompletableFuture<String> sendLocalList(String chargePointId, Integer listVersion, 
                                                  String updateType, List<SendLocalListRequest.AuthorizationData> authList) {
        try {
            Optional<ChargingStation> stationOpt = chargingStationService.findByChargePointId(chargePointId);
            if (stationOpt.isEmpty()) {
                log.error("Charging station not found: {}", chargePointId);
                return CompletableFuture.failedFuture(new RuntimeException("Charging station not found"));
            }
            
            ChargingStation station = stationOpt.get();
            
            // Create local authorization list record
            LocalAuthorizationList localAuthList = new LocalAuthorizationList();
            localAuthList.setChargingStation(station);
            localAuthList.setListVersion(listVersion);
            localAuthList.setUpdateType(updateType);
            localAuthList.setStatus(LocalAuthorizationList.Status.Accepted);
            
            localAuthListRepository.save(localAuthList);
            
            // Send SendLocalList command to charge point
            SendLocalListRequest request = new SendLocalListRequest(listVersion, updateType, authList);
            return remoteCommandService.sendCommand(chargePointId, "SendLocalList", request);
            
        } catch (Exception e) {
            log.error("Error sending local list to {}: {}", chargePointId, e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    public CompletableFuture<String> getLocalListVersion(String chargePointId) {
        try {
            // Send GetLocalListVersion command to charge point
            GetLocalListVersionRequest request = new GetLocalListVersionRequest();
            return remoteCommandService.sendCommand(chargePointId, "GetLocalListVersion", request);
            
        } catch (Exception e) {
            log.error("Error getting local list version from {}: {}", chargePointId, e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    public Integer getCurrentListVersion(String chargePointId) {
        Optional<ChargingStation> stationOpt = chargingStationService.findByChargePointId(chargePointId);
        if (stationOpt.isEmpty()) {
            return 0;
        }
        
        return localAuthListRepository.findLatestVersionByChargingStationId(stationOpt.get().getId())
                .orElse(0);
    }
    
    @Transactional
    public void updateLocalListStatus(String chargePointId, Integer listVersion, String status) {
        Optional<ChargingStation> stationOpt = chargingStationService.findByChargePointId(chargePointId);
        if (stationOpt.isEmpty()) {
            log.error("Charging station not found: {}", chargePointId);
            return;
        }
        
        ChargingStation station = stationOpt.get();
        Optional<LocalAuthorizationList> authListOpt = localAuthListRepository.findByChargingStationId(station.getId());
        
        if (authListOpt.isPresent()) {
            LocalAuthorizationList authList = authListOpt.get();
            try {
                authList.setStatus(LocalAuthorizationList.Status.valueOf(status));
                localAuthListRepository.save(authList);
                log.info("Updated local list status for {} to {}", chargePointId, status);
            } catch (IllegalArgumentException e) {
                log.error("Invalid local list status: {}", status);
            }
        }
    }
}