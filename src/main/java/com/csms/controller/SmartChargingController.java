package com.csms.controller;

import com.csms.dto.ApiResponse;
import com.csms.dto.ocpp.SetChargingProfileRequest;
import com.csms.model.ChargingProfile;
import com.csms.service.SmartChargingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/smart-charging")
@RequiredArgsConstructor
@Slf4j
public class SmartChargingController {
    
    private final SmartChargingService smartChargingService;
    
    @PostMapping("/{chargePointId}/set-profile")
    public ResponseEntity<ApiResponse<String>> setChargingProfile(
            @PathVariable String chargePointId,
            @RequestParam Integer connectorId,
            @RequestBody SetChargingProfileRequest.ChargingProfile chargingProfile) {
        
        try {
            CompletableFuture<String> future = smartChargingService.setChargingProfile(
                    chargePointId, connectorId, chargingProfile);
            
            String result = future.get(); // This will wait for the response
            return ResponseEntity.ok(new ApiResponse<>(true, "Charging profile set successfully", result));
        } catch (Exception e) {
            log.error("Error setting charging profile for {}: {}", chargePointId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Error setting charging profile: " + e.getMessage(), null));
        }
    }
    
    @PostMapping("/{chargePointId}/clear-profile")
    public ResponseEntity<ApiResponse<String>> clearChargingProfile(
            @PathVariable String chargePointId,
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) Integer connectorId,
            @RequestParam(required = false) String chargingProfilePurpose,
            @RequestParam(required = false) Integer stackLevel) {
        
        try {
            CompletableFuture<String> future = smartChargingService.clearChargingProfile(
                    chargePointId, id, connectorId, chargingProfilePurpose, stackLevel);
            
            String result = future.get(); // This will wait for the response
            return ResponseEntity.ok(new ApiResponse<>(true, "Charging profile cleared successfully", result));
        } catch (Exception e) {
            log.error("Error clearing charging profile for {}: {}", chargePointId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Error clearing charging profile: " + e.getMessage(), null));
        }
    }
    
    @PostMapping("/{chargePointId}/composite-schedule")
    public ResponseEntity<ApiResponse<String>> getCompositeSchedule(
            @PathVariable String chargePointId,
            @RequestParam Integer connectorId,
            @RequestParam Integer duration,
            @RequestParam(required = false) String chargingRateUnit) {
        
        try {
            CompletableFuture<String> future = smartChargingService.getCompositeSchedule(
                    chargePointId, connectorId, duration, chargingRateUnit);
            
            String result = future.get(); // This will wait for the response
            return ResponseEntity.ok(new ApiResponse<>(true, "Composite schedule retrieved successfully", result));
        } catch (Exception e) {
            log.error("Error getting composite schedule for {}: {}", chargePointId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Error getting composite schedule: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/{chargePointId}/profiles")
    public ResponseEntity<ApiResponse<List<ChargingProfile>>> getChargingProfiles(
            @PathVariable String chargePointId,
            @RequestParam(required = false) Integer connectorId) {
        
        try {
            List<ChargingProfile> profiles = smartChargingService.getChargingProfiles(chargePointId, connectorId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Charging profiles retrieved successfully", profiles));
        } catch (Exception e) {
            log.error("Error getting charging profiles for {}: {}", chargePointId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Error getting charging profiles: " + e.getMessage(), null));
        }
    }
}