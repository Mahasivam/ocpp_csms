package com.csms.controller;

import com.csms.dto.ApiResponse;
import com.csms.service.RemoteTriggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/trigger")
@RequiredArgsConstructor
@Slf4j
public class RemoteTriggerController {
    
    private final RemoteTriggerService remoteTriggerService;
    
    @PostMapping("/{chargePointId}/message")
    public ResponseEntity<ApiResponse<String>> triggerMessage(
            @PathVariable String chargePointId,
            @RequestParam String requestedMessage,
            @RequestParam(required = false) Integer connectorId) {
        
        try {
            CompletableFuture<String> future = remoteTriggerService.triggerMessage(
                    chargePointId, requestedMessage, connectorId);
            
            String result = future.get(); // This will wait for the response
            return ResponseEntity.ok(new ApiResponse<>(true, "Message trigger sent successfully", result));
        } catch (Exception e) {
            log.error("Error triggering message {} for {}: {}", requestedMessage, chargePointId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Error triggering message: " + e.getMessage(), null));
        }
    }
    
    @PostMapping("/{chargePointId}/boot-notification")
    public ResponseEntity<ApiResponse<String>> triggerBootNotification(@PathVariable String chargePointId) {
        try {
            CompletableFuture<String> future = remoteTriggerService.triggerBootNotification(chargePointId);
            
            String result = future.get();
            return ResponseEntity.ok(new ApiResponse<>(true, "BootNotification trigger sent successfully", result));
        } catch (Exception e) {
            log.error("Error triggering BootNotification for {}: {}", chargePointId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Error triggering BootNotification: " + e.getMessage(), null));
        }
    }
    
    @PostMapping("/{chargePointId}/heartbeat")
    public ResponseEntity<ApiResponse<String>> triggerHeartbeat(@PathVariable String chargePointId) {
        try {
            CompletableFuture<String> future = remoteTriggerService.triggerHeartbeat(chargePointId);
            
            String result = future.get();
            return ResponseEntity.ok(new ApiResponse<>(true, "Heartbeat trigger sent successfully", result));
        } catch (Exception e) {
            log.error("Error triggering Heartbeat for {}: {}", chargePointId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Error triggering Heartbeat: " + e.getMessage(), null));
        }
    }
    
    @PostMapping("/{chargePointId}/status-notification")
    public ResponseEntity<ApiResponse<String>> triggerStatusNotification(
            @PathVariable String chargePointId,
            @RequestParam Integer connectorId) {
        
        try {
            CompletableFuture<String> future = remoteTriggerService.triggerStatusNotification(chargePointId, connectorId);
            
            String result = future.get();
            return ResponseEntity.ok(new ApiResponse<>(true, "StatusNotification trigger sent successfully", result));
        } catch (Exception e) {
            log.error("Error triggering StatusNotification for {}: {}", chargePointId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Error triggering StatusNotification: " + e.getMessage(), null));
        }
    }
    
    @PostMapping("/{chargePointId}/meter-values")
    public ResponseEntity<ApiResponse<String>> triggerMeterValues(
            @PathVariable String chargePointId,
            @RequestParam Integer connectorId) {
        
        try {
            CompletableFuture<String> future = remoteTriggerService.triggerMeterValues(chargePointId, connectorId);
            
            String result = future.get();
            return ResponseEntity.ok(new ApiResponse<>(true, "MeterValues trigger sent successfully", result));
        } catch (Exception e) {
            log.error("Error triggering MeterValues for {}: {}", chargePointId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Error triggering MeterValues: " + e.getMessage(), null));
        }
    }
    
    @PostMapping("/{chargePointId}/diagnostics-status")
    public ResponseEntity<ApiResponse<String>> triggerDiagnosticsStatusNotification(@PathVariable String chargePointId) {
        try {
            CompletableFuture<String> future = remoteTriggerService.triggerDiagnosticsStatusNotification(chargePointId);
            
            String result = future.get();
            return ResponseEntity.ok(new ApiResponse<>(true, "DiagnosticsStatusNotification trigger sent successfully", result));
        } catch (Exception e) {
            log.error("Error triggering DiagnosticsStatusNotification for {}: {}", chargePointId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Error triggering DiagnosticsStatusNotification: " + e.getMessage(), null));
        }
    }
    
    @PostMapping("/{chargePointId}/firmware-status")
    public ResponseEntity<ApiResponse<String>> triggerFirmwareStatusNotification(@PathVariable String chargePointId) {
        try {
            CompletableFuture<String> future = remoteTriggerService.triggerFirmwareStatusNotification(chargePointId);
            
            String result = future.get();
            return ResponseEntity.ok(new ApiResponse<>(true, "FirmwareStatusNotification trigger sent successfully", result));
        } catch (Exception e) {
            log.error("Error triggering FirmwareStatusNotification for {}: {}", chargePointId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Error triggering FirmwareStatusNotification: " + e.getMessage(), null));
        }
    }
}