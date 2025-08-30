package com.csms.controller;

import com.csms.dto.ApiResponse;
import com.csms.model.Diagnostics;
import com.csms.model.FirmwareUpdate;
import com.csms.service.FirmwareManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/firmware")
@RequiredArgsConstructor
@Slf4j
public class FirmwareManagementController {
    
    private final FirmwareManagementService firmwareManagementService;
    
    @PostMapping("/{chargePointId}/update")
    public ResponseEntity<ApiResponse<String>> updateFirmware(
            @PathVariable String chargePointId,
            @RequestParam String location,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime retrieveDate,
            @RequestParam(required = false) Integer retries,
            @RequestParam(required = false) Integer retryInterval) {
        
        try {
            CompletableFuture<String> future = firmwareManagementService.updateFirmware(
                    chargePointId, location, retrieveDate, retries, retryInterval);
            
            String result = future.get(); // This will wait for the response
            return ResponseEntity.ok(new ApiResponse<>(true, "Firmware update initiated successfully", result));
        } catch (Exception e) {
            log.error("Error initiating firmware update for {}: {}", chargePointId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Error initiating firmware update: " + e.getMessage(), null));
        }
    }
    
    @PostMapping("/{chargePointId}/diagnostics")
    public ResponseEntity<ApiResponse<String>> getDiagnostics(
            @PathVariable String chargePointId,
            @RequestParam String location,
            @RequestParam(required = false) Integer retries,
            @RequestParam(required = false) Integer retryInterval,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime stopTime) {
        
        try {
            CompletableFuture<String> future = firmwareManagementService.getDiagnostics(
                    chargePointId, location, retries, retryInterval, startTime, stopTime);
            
            String result = future.get(); // This will wait for the response
            return ResponseEntity.ok(new ApiResponse<>(true, "Diagnostics retrieval initiated successfully", result));
        } catch (Exception e) {
            log.error("Error initiating diagnostics retrieval for {}: {}", chargePointId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Error initiating diagnostics retrieval: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/{chargePointId}/updates")
    public ResponseEntity<ApiResponse<List<FirmwareUpdate>>> getFirmwareUpdates(@PathVariable String chargePointId) {
        try {
            List<FirmwareUpdate> updates = firmwareManagementService.getFirmwareUpdates(chargePointId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Firmware updates retrieved successfully", updates));
        } catch (Exception e) {
            log.error("Error getting firmware updates for {}: {}", chargePointId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Error getting firmware updates: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/{chargePointId}/diagnostics-list")
    public ResponseEntity<ApiResponse<List<Diagnostics>>> getDiagnosticsList(@PathVariable String chargePointId) {
        try {
            List<Diagnostics> diagnosticsList = firmwareManagementService.getDiagnosticsList(chargePointId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Diagnostics list retrieved successfully", diagnosticsList));
        } catch (Exception e) {
            log.error("Error getting diagnostics list for {}: {}", chargePointId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Error getting diagnostics list: " + e.getMessage(), null));
        }
    }
}