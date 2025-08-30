package com.csms.service;

import com.csms.dto.ocpp.*;
import com.csms.websocket.OcppWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@Lazy
public class RemoteCommandService {

    @Lazy
    @Autowired
    private OcppWebSocketHandler webSocketHandler;
    
    private final ObjectMapper objectMapper;

    public RemoteCommandService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Store pending requests awaiting responses
    private final ConcurrentHashMap<String, CompletableFuture<String>> pendingRequests = new ConcurrentHashMap<>();

    public CompletableFuture<String> sendRemoteStartTransaction(String chargePointId, String idTag, Integer connectorId) {
        try {
            RemoteStartTransactionRequest request = new RemoteStartTransactionRequest(idTag, connectorId, null);
            return sendCommand(chargePointId, "RemoteStartTransaction", request);
        } catch (Exception e) {
            log.error("Error sending RemoteStartTransaction to {}: {}", chargePointId, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<String> sendRemoteStopTransaction(String chargePointId, Integer transactionId) {
        try {
            RemoteStopTransactionRequest request = new RemoteStopTransactionRequest(transactionId);
            return sendCommand(chargePointId, "RemoteStopTransaction", request);
        } catch (Exception e) {
            log.error("Error sending RemoteStopTransaction to {}: {}", chargePointId, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<String> sendReset(String chargePointId, String type) {
        try {
            ResetRequest request = new ResetRequest(type);
            return sendCommand(chargePointId, "Reset", request);
        } catch (Exception e) {
            log.error("Error sending Reset to {}: {}", chargePointId, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<String> sendUnlockConnector(String chargePointId, Integer connectorId) {
        try {
            UnlockConnectorRequest request = new UnlockConnectorRequest(connectorId);
            return sendCommand(chargePointId, "UnlockConnector", request);
        } catch (Exception e) {
            log.error("Error sending UnlockConnector to {}: {}", chargePointId, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<String> sendReserveNow(String chargePointId, Integer connectorId,
                                                    LocalDateTime expiryDate, String idTag, String parentIdTag) {
        try {
            Integer reservationId = (int) (System.currentTimeMillis() % 1000000);
            ReserveNowRequest request = new ReserveNowRequest(connectorId, expiryDate, idTag, parentIdTag, reservationId);
            return sendCommand(chargePointId, "ReserveNow", request);
        } catch (Exception e) {
            log.error("Error sending ReserveNow to {}: {}", chargePointId, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<String> sendCancelReservation(String chargePointId, Integer reservationId) {
        try {
            CancelReservationRequest request = new CancelReservationRequest(reservationId);
            return sendCommand(chargePointId, "CancelReservation", request);
        } catch (Exception e) {
            log.error("Error sending CancelReservation to {}: {}", chargePointId, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<String> sendGetConfiguration(String chargePointId, List<String> keys) {
        try {
            GetConfigurationRequest request = new GetConfigurationRequest(keys);
            return sendCommand(chargePointId, "GetConfiguration", request);
        } catch (Exception e) {
            log.error("Error sending GetConfiguration to {}: {}", chargePointId, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<String> sendChangeConfiguration(String chargePointId, String key, String value) {
        try {
            ChangeConfigurationRequest request = new ChangeConfigurationRequest(key, value);
            return sendCommand(chargePointId, "ChangeConfiguration", request);
        } catch (Exception e) {
            log.error("Error sending ChangeConfiguration to {}: {}", chargePointId, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<String> sendClearCache(String chargePointId) {
        try {
            return sendCommand(chargePointId, "ClearCache", "{}");
        } catch (Exception e) {
            log.error("Error sending ClearCache to {}: {}", chargePointId, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<String> sendUpdateFirmware(String chargePointId, String location, 
                                                       LocalDateTime retrieveDate, Integer retries, Integer retryInterval) {
        try {
            UpdateFirmwareRequest request = new UpdateFirmwareRequest(location, retrieveDate, retries, retryInterval);
            return sendCommand(chargePointId, "UpdateFirmware", request);
        } catch (Exception e) {
            log.error("Error sending UpdateFirmware to {}: {}", chargePointId, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<String> sendGetDiagnostics(String chargePointId, String location, 
                                                       Integer retries, Integer retryInterval,
                                                       LocalDateTime startTime, LocalDateTime stopTime) {
        try {
            GetDiagnosticsRequest request = new GetDiagnosticsRequest(location, retries, retryInterval, startTime, stopTime);
            return sendCommand(chargePointId, "GetDiagnostics", request);
        } catch (Exception e) {
            log.error("Error sending GetDiagnostics to {}: {}", chargePointId, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<String> sendLocalList(String chargePointId, Integer listVersion, 
                                                  String updateType, List<SendLocalListRequest.AuthorizationData> authList) {
        try {
            SendLocalListRequest request = new SendLocalListRequest(listVersion, updateType, authList);
            return sendCommand(chargePointId, "SendLocalList", request);
        } catch (Exception e) {
            log.error("Error sending SendLocalList to {}: {}", chargePointId, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<String> sendGetLocalListVersion(String chargePointId) {
        try {
            GetLocalListVersionRequest request = new GetLocalListVersionRequest();
            return sendCommand(chargePointId, "GetLocalListVersion", request);
        } catch (Exception e) {
            log.error("Error sending GetLocalListVersion to {}: {}", chargePointId, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<String> sendTriggerMessage(String chargePointId, String requestedMessage, Integer connectorId) {
        try {
            TriggerMessageRequest request = new TriggerMessageRequest(requestedMessage, connectorId);
            return sendCommand(chargePointId, "TriggerMessage", request);
        } catch (Exception e) {
            log.error("Error sending TriggerMessage to {}: {}", chargePointId, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<String> sendSetChargingProfile(String chargePointId, Integer connectorId, 
                                                           SetChargingProfileRequest.ChargingProfile chargingProfile) {
        try {
            SetChargingProfileRequest request = new SetChargingProfileRequest(connectorId, chargingProfile);
            return sendCommand(chargePointId, "SetChargingProfile", request);
        } catch (Exception e) {
            log.error("Error sending SetChargingProfile to {}: {}", chargePointId, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<String> sendClearChargingProfile(String chargePointId, Integer id, Integer connectorId, 
                                                             String chargingProfilePurpose, Integer stackLevel) {
        try {
            ClearChargingProfileRequest request = new ClearChargingProfileRequest(id, connectorId, chargingProfilePurpose, stackLevel);
            return sendCommand(chargePointId, "ClearChargingProfile", request);
        } catch (Exception e) {
            log.error("Error sending ClearChargingProfile to {}: {}", chargePointId, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<String> sendGetCompositeSchedule(String chargePointId, Integer connectorId, 
                                                             Integer duration, String chargingRateUnit) {
        try {
            GetCompositeScheduleRequest request = new GetCompositeScheduleRequest(connectorId, duration, chargingRateUnit);
            return sendCommand(chargePointId, "GetCompositeSchedule", request);
        } catch (Exception e) {
            log.error("Error sending GetCompositeSchedule to {}: {}", chargePointId, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<String> sendCommand(String chargePointId, String action, Object payload) throws Exception {
        String messageId = UUID.randomUUID().toString();
        String payloadJson = payload instanceof String ? (String) payload : objectMapper.writeValueAsString(payload);
        String message = String.format("[2,\"%s\",\"%s\",%s]", messageId, action, payloadJson);

        CompletableFuture<String> future = new CompletableFuture<>();
        pendingRequests.put(messageId, future);

        // Set timeout for the request
        future.orTimeout(30, TimeUnit.SECONDS)
                .whenComplete((result, ex) -> pendingRequests.remove(messageId));

        webSocketHandler.sendMessage(chargePointId, message);
        log.info("Sent {} command to {} with message ID: {}", action, chargePointId, messageId);

        return future;
    }

    public void completeRequest(String messageId, String response) {
        CompletableFuture<String> future = pendingRequests.remove(messageId);
        if (future != null) {
            future.complete(response);
        }
    }

    // Additional DTOs for remote commands
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class ResetRequest {
        private String type; // Hard, Soft
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class UnlockConnectorRequest {
        private Integer connectorId;
    }
}
