package com.csms.service;

import com.csms.dto.ocpp.TriggerMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemoteTriggerService {
    
    private final RemoteCommandService remoteCommandService;
    
    public CompletableFuture<String> triggerMessage(String chargePointId, String requestedMessage, Integer connectorId) {
        try {
            // Validate requested message
            if (!isValidTriggerMessage(requestedMessage)) {
                log.error("Invalid trigger message: {}", requestedMessage);
                return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid trigger message"));
            }
            
            // Send TriggerMessage command to charge point
            TriggerMessageRequest request = new TriggerMessageRequest(requestedMessage, connectorId);
            return remoteCommandService.sendCommand(chargePointId, "TriggerMessage", request);
            
        } catch (Exception e) {
            log.error("Error triggering message {} for {}: {}", requestedMessage, chargePointId, e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    private boolean isValidTriggerMessage(String message) {
        return message != null && (
                "BootNotification".equals(message) ||
                "DiagnosticsStatusNotification".equals(message) ||
                "FirmwareStatusNotification".equals(message) ||
                "Heartbeat".equals(message) ||
                "MeterValues".equals(message) ||
                "StatusNotification".equals(message)
        );
    }
    
    // Convenience methods for specific triggers
    
    public CompletableFuture<String> triggerBootNotification(String chargePointId) {
        return triggerMessage(chargePointId, "BootNotification", null);
    }
    
    public CompletableFuture<String> triggerHeartbeat(String chargePointId) {
        return triggerMessage(chargePointId, "Heartbeat", null);
    }
    
    public CompletableFuture<String> triggerStatusNotification(String chargePointId, Integer connectorId) {
        return triggerMessage(chargePointId, "StatusNotification", connectorId);
    }
    
    public CompletableFuture<String> triggerMeterValues(String chargePointId, Integer connectorId) {
        return triggerMessage(chargePointId, "MeterValues", connectorId);
    }
    
    public CompletableFuture<String> triggerDiagnosticsStatusNotification(String chargePointId) {
        return triggerMessage(chargePointId, "DiagnosticsStatusNotification", null);
    }
    
    public CompletableFuture<String> triggerFirmwareStatusNotification(String chargePointId) {
        return triggerMessage(chargePointId, "FirmwareStatusNotification", null);
    }
}