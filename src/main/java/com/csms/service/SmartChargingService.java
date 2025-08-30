package com.csms.service;

import com.csms.dto.ocpp.*;
import com.csms.model.ChargingProfile;
import com.csms.model.ChargingSchedule;
import com.csms.model.ChargingSchedulePeriod;
import com.csms.model.ChargingStation;
import com.csms.repository.ChargingProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmartChargingService {
    
    private final ChargingProfileRepository chargingProfileRepository;
    private final RemoteCommandService remoteCommandService;
    private final ChargingStationService chargingStationService;
    
    @Transactional
    public CompletableFuture<String> setChargingProfile(String chargePointId, Integer connectorId, 
                                                       SetChargingProfileRequest.ChargingProfile csChargingProfile) {
        try {
            Optional<ChargingStation> stationOpt = chargingStationService.findByChargePointId(chargePointId);
            if (stationOpt.isEmpty()) {
                log.error("Charging station not found: {}", chargePointId);
                return CompletableFuture.failedFuture(new RuntimeException("Charging station not found"));
            }
            
            ChargingStation station = stationOpt.get();
            
            // Create charging profile record
            ChargingProfile chargingProfile = new ChargingProfile();
            chargingProfile.setChargingStation(station);
            chargingProfile.setChargingProfileId(csChargingProfile.getChargingProfileId());
            chargingProfile.setConnectorId(connectorId);
            chargingProfile.setTransactionId(csChargingProfile.getTransactionId());
            chargingProfile.setStackLevel(csChargingProfile.getStackLevel());
            chargingProfile.setChargingProfilePurpose(csChargingProfile.getChargingProfilePurpose());
            chargingProfile.setChargingProfileKind(csChargingProfile.getChargingProfileKind());
            chargingProfile.setRecurrencyKind(csChargingProfile.getRecurrencyKind());
            chargingProfile.setValidFrom(csChargingProfile.getValidFrom());
            chargingProfile.setValidTo(csChargingProfile.getValidTo());
            chargingProfile.setStatus(ChargingProfile.Status.Accepted);
            
            chargingProfile = chargingProfileRepository.save(chargingProfile);
            
            // Create charging schedules
            if (csChargingProfile.getChargingSchedule() != null) {
                ChargingSchedule schedule = new ChargingSchedule();
                schedule.setChargingProfile(chargingProfile);
                schedule.setDuration(csChargingProfile.getChargingSchedule().getDuration());
                schedule.setStartSchedule(csChargingProfile.getChargingSchedule().getStartSchedule());
                schedule.setChargingRateUnit(csChargingProfile.getChargingSchedule().getChargingRateUnit());
                schedule.setMinChargingRate(csChargingProfile.getChargingSchedule().getMinChargingRate());
                
                // Create charging schedule periods
                List<ChargingSchedulePeriod> periods = new ArrayList<>();
                if (csChargingProfile.getChargingSchedule().getChargingSchedulePeriod() != null) {
                    for (SetChargingProfileRequest.ChargingSchedulePeriod csPeriod : 
                         csChargingProfile.getChargingSchedule().getChargingSchedulePeriod()) {
                        ChargingSchedulePeriod period = new ChargingSchedulePeriod();
                        period.setChargingSchedule(schedule);
                        period.setStartPeriod(csPeriod.getStartPeriod());
                        period.setLimit(csPeriod.getLimit());
                        period.setNumberPhases(csPeriod.getNumberPhases());
                        periods.add(period);
                    }
                }
                schedule.setChargingSchedulePeriods(periods);
                
                List<ChargingSchedule> schedules = new ArrayList<>();
                schedules.add(schedule);
                chargingProfile.setChargingSchedules(schedules);
            }
            
            // Send SetChargingProfile command to charge point
            SetChargingProfileRequest request = new SetChargingProfileRequest(connectorId, csChargingProfile);
            return remoteCommandService.sendCommand(chargePointId, "SetChargingProfile", request);
            
        } catch (Exception e) {
            log.error("Error setting charging profile for {}: {}", chargePointId, e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    @Transactional
    public CompletableFuture<String> clearChargingProfile(String chargePointId, Integer id, Integer connectorId, 
                                                         String chargingProfilePurpose, Integer stackLevel) {
        try {
            Optional<ChargingStation> stationOpt = chargingStationService.findByChargePointId(chargePointId);
            if (stationOpt.isEmpty()) {
                log.error("Charging station not found: {}", chargePointId);
                return CompletableFuture.failedFuture(new RuntimeException("Charging station not found"));
            }
            
            ChargingStation station = stationOpt.get();
            
            // Find and remove matching charging profiles
            List<ChargingProfile> profilesToRemove = chargingProfileRepository.findByFilters(
                    station.getId(), connectorId, chargingProfilePurpose, stackLevel);
            
            if (id != null) {
                profilesToRemove = profilesToRemove.stream()
                        .filter(profile -> profile.getChargingProfileId().equals(id))
                        .toList();
            }
            
            chargingProfileRepository.deleteAll(profilesToRemove);
            
            // Send ClearChargingProfile command to charge point
            ClearChargingProfileRequest request = new ClearChargingProfileRequest(id, connectorId, chargingProfilePurpose, stackLevel);
            return remoteCommandService.sendCommand(chargePointId, "ClearChargingProfile", request);
            
        } catch (Exception e) {
            log.error("Error clearing charging profile for {}: {}", chargePointId, e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    public CompletableFuture<String> getCompositeSchedule(String chargePointId, Integer connectorId, 
                                                         Integer duration, String chargingRateUnit) {
        try {
            // Send GetCompositeSchedule command to charge point
            GetCompositeScheduleRequest request = new GetCompositeScheduleRequest(connectorId, duration, chargingRateUnit);
            return remoteCommandService.sendCommand(chargePointId, "GetCompositeSchedule", request);
            
        } catch (Exception e) {
            log.error("Error getting composite schedule from {}: {}", chargePointId, e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    public List<ChargingProfile> getChargingProfiles(String chargePointId, Integer connectorId) {
        Optional<ChargingStation> stationOpt = chargingStationService.findByChargePointId(chargePointId);
        if (stationOpt.isEmpty()) {
            return List.of();
        }
        
        if (connectorId != null) {
            return chargingProfileRepository.findByChargingStationIdAndConnectorId(stationOpt.get().getId(), connectorId);
        } else {
            return chargingProfileRepository.findByChargingStationId(stationOpt.get().getId());
        }
    }
}