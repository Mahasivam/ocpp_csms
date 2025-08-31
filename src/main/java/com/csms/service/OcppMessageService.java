package com.csms.service;

import com.csms.dto.ocpp.*;
import com.csms.model.ChargingStation;
import com.csms.model.Reservation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcppMessageService {

    private final ChargingStationService chargingStationService;
    private final TransactionService transactionService;
    private final AuthorizationService authorizationService;
    private final MeterValueService meterValueService;
    private final ReservationService reservationService;
    private final ConfigurationService configurationService;
    @Lazy
    private final FirmwareManagementService firmwareManagementService;
    private final ObjectMapper objectMapper;

    public String handleMessage(String chargePointId, String messageId, String action, JsonNode payload) {
        try {
            switch (action) {
                case "BootNotification":
                    return handleBootNotification(chargePointId, messageId, payload);
                case "StatusNotification":
                    return handleStatusNotification(chargePointId, messageId, payload);
                case "Heartbeat":
                    return handleHeartbeat(chargePointId, messageId, payload);
                case "Authorize":
                    return handleAuthorize(chargePointId, messageId, payload);
                case "StartTransaction":
                    return handleStartTransaction(chargePointId, messageId, payload);
                case "StopTransaction":
                    return handleStopTransaction(chargePointId, messageId, payload);
                case "MeterValues":
                    return handleMeterValues(chargePointId, messageId, payload);
                case "DataTransfer":
                    return handleDataTransfer(chargePointId, messageId, payload);
                case "DiagnosticsStatusNotification":
                    return handleDiagnosticsStatusNotification(chargePointId, messageId, payload);
                case "FirmwareStatusNotification":
                    return handleFirmwareStatusNotification(chargePointId, messageId, payload);
                
                // CSMS → Charge Point command handlers
                case "RemoteStartTransaction":
                    return handleRemoteStartTransaction(chargePointId, messageId, payload);
                case "RemoteStopTransaction":
                    return handleRemoteStopTransaction(chargePointId, messageId, payload);
                case "Reset":
                    return handleReset(chargePointId, messageId, payload);
                case "UnlockConnector":
                    return handleUnlockConnector(chargePointId, messageId, payload);
                case "ReserveNow":
                    return handleReserveNow(chargePointId, messageId, payload);
                case "CancelReservation":
                    return handleCancelReservation(chargePointId, messageId, payload);
                case "GetConfiguration":
                    return handleGetConfiguration(chargePointId, messageId, payload);
                case "ChangeConfiguration":
                    return handleChangeConfiguration(chargePointId, messageId, payload);
                case "ClearCache":
                    return handleClearCache(chargePointId, messageId, payload);
                case "TriggerMessage":
                    return handleTriggerMessage(chargePointId, messageId, payload);
                    
                default:
                    log.warn("Unknown action: {} from charge point: {}", action, chargePointId);
                    return createErrorResponse(messageId, "NotSupported", "Action not supported");
            }
        } catch (Exception e) {
            log.error("Error handling message {} from {}: {}", action, chargePointId, e.getMessage(), e);
            return createErrorResponse(messageId, "InternalError", "Internal server error");
        }
    }

    private String handleBootNotification(String chargePointId, String messageId, JsonNode payload) throws Exception {
        BootNotificationRequest request = objectMapper.treeToValue(payload, BootNotificationRequest.class);

        ChargingStation station = chargingStationService.registerChargingStation(
                chargePointId,
                request.getChargePointVendor(),
                request.getChargePointModel(),
                request.getChargePointSerialNumber(),
                request.getFirmwareVersion()
        );

        // Initialize default configuration if new station
        configurationService.initializeDefaultConfiguration(station);

        BootNotificationResponse response = new BootNotificationResponse(
                "Accepted",
                LocalDateTime.now(),
                300 // Heartbeat interval in seconds
        );

        return createCallResult(messageId, response);
    }

    private String handleStatusNotification(String chargePointId, String messageId, JsonNode payload) throws Exception {
        StatusNotificationRequest request = objectMapper.treeToValue(payload, StatusNotificationRequest.class);

        Optional<ChargingStation> stationOpt = chargingStationService.findByChargePointId(chargePointId);
        if (stationOpt.isPresent()) {
            chargingStationService.updateConnectorStatus(
                    stationOpt.get().getId(),
                    request.getConnectorId(),
                    request.getStatus(),
                    request.getErrorCode(),
                    request.getInfo()
            );
        }

        StatusNotificationResponse response = new StatusNotificationResponse();
        return createCallResult(messageId, response);
    }

    private String handleHeartbeat(String chargePointId, String messageId, JsonNode payload) throws Exception {
        chargingStationService.updateHeartbeat(chargePointId);

        HeartbeatResponse response = new HeartbeatResponse(LocalDateTime.now());
        return createCallResult(messageId, response);
    }

    private String handleAuthorize(String chargePointId, String messageId, JsonNode payload) throws Exception {
        AuthorizeRequest request = objectMapper.treeToValue(payload, AuthorizeRequest.class);

        StartTransactionResponse.IdTagInfo idTagInfo = authorizationService.authorize(request.getIdTag());
        AuthorizeResponse response = new AuthorizeResponse(idTagInfo);

        return createCallResult(messageId, response);
    }

    private String handleStartTransaction(String chargePointId, String messageId, JsonNode payload) throws Exception {
        StartTransactionRequest request = objectMapper.treeToValue(payload, StartTransactionRequest.class);

        Optional<ChargingStation> stationOpt = chargingStationService.findByChargePointId(chargePointId);
        if (stationOpt.isEmpty()) {
            return createErrorResponse(messageId, "GenericError", "Charge point not registered");
        }

        ChargingStation station = stationOpt.get();

        // Check for active reservations
        List<Reservation> activeReservations = reservationService
                .findActiveReservations(station.getId(), request.getConnectorId());

        boolean hasValidReservation = activeReservations.stream()
                .anyMatch(r -> r.getIdTag().equals(request.getIdTag()) ||
                        (r.getParentIdTag() != null && r.getParentIdTag().equals(request.getIdTag())));

        StartTransactionResponse.IdTagInfo idTagInfo = authorizationService.authorize(request.getIdTag());

        if ("Accepted".equals(idTagInfo.getStatus())) {
            Integer transactionId = transactionService.startTransaction(
                    station,
                    request.getConnectorId(),
                    request.getIdTag(),
                    request.getMeterStart(),
                    request.getTimestamp()
            );

            StartTransactionResponse response = new StartTransactionResponse(idTagInfo, transactionId);
            return createCallResult(messageId, response);
        } else {
            StartTransactionResponse response = new StartTransactionResponse(idTagInfo, -1);
            return createCallResult(messageId, response);
        }
    }

    private String handleStopTransaction(String chargePointId, String messageId, JsonNode payload) throws Exception {
        StopTransactionRequest request = objectMapper.treeToValue(payload, StopTransactionRequest.class);

        Optional<ChargingStation> stationOpt = chargingStationService.findByChargePointId(chargePointId);
        if (stationOpt.isPresent()) {
            // Store meter values if provided
            if (request.getTransactionData() != null && !request.getTransactionData().isEmpty()) {
                meterValueService.storeMeterValues(
                        stationOpt.get(),
                        null, // Connector ID not specified in stop transaction
                        request.getTransactionId(),
                        request.getTransactionData()
                );
            }
        }

        StartTransactionResponse.IdTagInfo idTagInfo = null;
        if (request.getIdTag() != null) {
            idTagInfo = authorizationService.authorize(request.getIdTag());
        } else {
            idTagInfo = new StartTransactionResponse.IdTagInfo("Accepted", null, null);
        }

        transactionService.stopTransaction(
                request.getTransactionId(),
                request.getIdTag(),
                request.getMeterStop(),
                request.getTimestamp(),
                request.getReason()
        );

        StopTransactionResponse response = new StopTransactionResponse(idTagInfo);
        return createCallResult(messageId, response);
    }

    private String handleMeterValues(String chargePointId, String messageId, JsonNode payload) throws Exception {
        MeterValuesRequest request = objectMapper.treeToValue(payload, MeterValuesRequest.class);
        
        log.info("Received MeterValues from {}: connectorId={}, transactionId={}, {} meter values", 
                chargePointId, request.getConnectorId(), request.getTransactionId(), 
                request.getMeterValue() != null ? request.getMeterValue().size() : 0);

        Optional<ChargingStation> stationOpt = chargingStationService.findByChargePointId(chargePointId);
        if (stationOpt.isPresent()) {
            // Use the new method for proper MeterValue DTOs
            meterValueService.storeMeterValuesFromRequest(
                    stationOpt.get(),
                    request.getConnectorId(),
                    request.getTransactionId(),
                    request.getMeterValue()
            );
            
            log.debug("Successfully stored meter values for transaction {} on connector {} for charge point {}", 
                     request.getTransactionId(), request.getConnectorId(), chargePointId);
        } else {
            log.warn("Charge point {} not found when processing MeterValues", chargePointId);
        }

        MeterValuesResponse response = new MeterValuesResponse();
        return createCallResult(messageId, response);
    }

    private String handleDataTransfer(String chargePointId, String messageId, JsonNode payload) throws Exception {
        DataTransferRequest request = objectMapper.treeToValue(payload, DataTransferRequest.class);
        log.info("Received data transfer from {}: vendorId={}, messageId={}", 
                chargePointId, request.getVendorId(), request.getMessageId());

        // For now, accept all data transfers
        // In production, implement vendor-specific handling based on vendorId and messageId
        DataTransferResponse response = new DataTransferResponse("Accepted", null);
        return createCallResult(messageId, response);
    }

    private String handleDiagnosticsStatusNotification(String chargePointId, String messageId, JsonNode payload) throws Exception {
        DiagnosticsStatusNotificationRequest request = objectMapper.treeToValue(payload, DiagnosticsStatusNotificationRequest.class);
        log.info("Received diagnostics status from {}: {}", chargePointId, request.getStatus());
        
        // Update diagnostics status
        firmwareManagementService.updateDiagnosticsStatus(chargePointId, request.getStatus());
        
        DiagnosticsStatusNotificationResponse response = new DiagnosticsStatusNotificationResponse();
        return createCallResult(messageId, response);
    }

    private String handleFirmwareStatusNotification(String chargePointId, String messageId, JsonNode payload) throws Exception {
        FirmwareStatusNotificationRequest request = objectMapper.treeToValue(payload, FirmwareStatusNotificationRequest.class);
        log.info("Received firmware status from {}: {}", chargePointId, request.getStatus());
        
        // Update firmware status
        firmwareManagementService.updateFirmwareStatus(chargePointId, request.getStatus());
        
        FirmwareStatusNotificationResponse response = new FirmwareStatusNotificationResponse();
        return createCallResult(messageId, response);
    }

    public void handleResponse(String chargePointId, String messageId, JsonNode payload) {
        log.info("Received response from {} for message {}: {}", chargePointId, messageId, payload);
        // Handle responses to messages sent by CSMS to charge points
    }

    public void handleError(String chargePointId, String messageId, String errorCode, String errorDescription) {
        log.error("Received error from {} for message {}: {} - {}",
                chargePointId, messageId, errorCode, errorDescription);
    }

    private String createCallResult(String messageId, Object payload) throws Exception {
        String payloadJson = objectMapper.writeValueAsString(payload);
        return String.format("[3,\"%s\",%s]", messageId, payloadJson);
    }

    private String createErrorResponse(String messageId, String errorCode, String errorDescription) {
        return String.format("[4,\"%s\",\"%s\",\"%s\",{}]", messageId, errorCode, errorDescription);
    }

    // CSMS → Charge Point command handlers (new methods for frontend integration)
    
    private String handleRemoteStartTransaction(String chargePointId, String messageId, JsonNode payload) throws Exception {
        RemoteStartTransactionRequest request = objectMapper.treeToValue(payload, RemoteStartTransactionRequest.class);
        log.info("Frontend requested RemoteStartTransaction for {} on connector {}", request.getIdTag(), request.getConnectorId());
        
        // Validate the request
        Optional<ChargingStation> stationOpt = chargingStationService.findByChargePointId(chargePointId);
        if (stationOpt.isEmpty()) {
            return createErrorResponse(messageId, "GenericError", "Charge point not registered");
        }

        ChargingStation station = stationOpt.get();
        
        // Validate IdTag authorization
        StartTransactionResponse.IdTagInfo idTagInfo = authorizationService.authorize(request.getIdTag());
        if (!"Accepted".equals(idTagInfo.getStatus())) {
            log.warn("RemoteStartTransaction rejected: IdTag {} not authorized", request.getIdTag());
            RemoteStartTransactionResponse response = new RemoteStartTransactionResponse("Rejected");
            return createCallResult(messageId, response);
        }

        try {
            // Actually start the transaction in the database
            Integer transactionId = transactionService.startTransaction(
                    station,
                    request.getConnectorId(),
                    request.getIdTag(),
                    0, // Initial meter value
                    LocalDateTime.now()
            );
            
            log.info("Successfully started transaction {} via RemoteStartTransaction for {} on connector {}", 
                     transactionId, request.getIdTag(), request.getConnectorId());
                     
            RemoteStartTransactionResponse response = new RemoteStartTransactionResponse("Accepted");
            return createCallResult(messageId, response);
            
        } catch (Exception e) {
            log.error("Error starting transaction via RemoteStartTransaction: {}", e.getMessage(), e);
            RemoteStartTransactionResponse response = new RemoteStartTransactionResponse("Rejected");
            return createCallResult(messageId, response);
        }
    }

    private String handleRemoteStopTransaction(String chargePointId, String messageId, JsonNode payload) throws Exception {
        RemoteStopTransactionRequest request = objectMapper.treeToValue(payload, RemoteStopTransactionRequest.class);
        log.info("Frontend requested RemoteStopTransaction for transaction {}", request.getTransactionId());
        
        // Validate the transaction exists
        Optional<com.csms.model.Transaction> transactionOpt = transactionService.findByTransactionId(request.getTransactionId());
        if (transactionOpt.isEmpty()) {
            RemoteStopTransactionResponse response = new RemoteStopTransactionResponse("Rejected");
            return createCallResult(messageId, response);
        }

        // Actually stop the transaction
        com.csms.model.Transaction transaction = transactionOpt.get();
        transactionService.stopTransaction(
                request.getTransactionId(),
                transaction.getIdTag(), // Use the transaction's original idTag
                null, // meterStop - will be updated when charge point sends StopTransaction
                java.time.LocalDateTime.now(), // timestamp
                "Remote" // reason
        );
        
        log.info("Successfully stopped transaction {} via remote command", request.getTransactionId());

        RemoteStopTransactionResponse response = new RemoteStopTransactionResponse("Accepted");
        return createCallResult(messageId, response);
    }

    private String handleReset(String chargePointId, String messageId, JsonNode payload) throws Exception {
        ResetRequest request = objectMapper.treeToValue(payload, ResetRequest.class);
        log.info("Frontend requested Reset ({}) for {}", request.getType(), chargePointId);
        
        // Accept all reset requests
        ResetResponse response = new ResetResponse("Accepted");
        return createCallResult(messageId, response);
    }

    private String handleUnlockConnector(String chargePointId, String messageId, JsonNode payload) throws Exception {
        UnlockConnectorRequest request = objectMapper.treeToValue(payload, UnlockConnectorRequest.class);
        log.info("Frontend requested UnlockConnector {} for {}", request.getConnectorId(), chargePointId);
        
        // Accept all unlock requests
        UnlockConnectorResponse response = new UnlockConnectorResponse("Accepted");
        return createCallResult(messageId, response);
    }

    private String handleReserveNow(String chargePointId, String messageId, JsonNode payload) throws Exception {
        ReserveNowRequest request = objectMapper.treeToValue(payload, ReserveNowRequest.class);
        log.info("Frontend requested ReserveNow for connector {} with idTag {}", request.getConnectorId(), request.getIdTag());
        
        // For now, accept all reservation requests
        ReserveNowResponse response = new ReserveNowResponse("Accepted");
        return createCallResult(messageId, response);
    }

    private String handleCancelReservation(String chargePointId, String messageId, JsonNode payload) throws Exception {
        CancelReservationRequest request = objectMapper.treeToValue(payload, CancelReservationRequest.class);
        log.info("Frontend requested CancelReservation for reservation {}", request.getReservationId());
        
        CancelReservationResponse response = new CancelReservationResponse("Accepted");
        return createCallResult(messageId, response);
    }

    private String handleGetConfiguration(String chargePointId, String messageId, JsonNode payload) throws Exception {
        GetConfigurationRequest request = objectMapper.treeToValue(payload, GetConfigurationRequest.class);
        log.info("Frontend requested GetConfiguration for {} keys", 
                request.getKey() != null ? request.getKey().size() : "all");
        
        // Return empty configuration for now
        GetConfigurationResponse response = new GetConfigurationResponse();
        return createCallResult(messageId, response);
    }

    private String handleChangeConfiguration(String chargePointId, String messageId, JsonNode payload) throws Exception {
        ChangeConfigurationRequest request = objectMapper.treeToValue(payload, ChangeConfigurationRequest.class);
        log.info("Frontend requested ChangeConfiguration: {}={}", request.getKey(), request.getValue());
        
        ChangeConfigurationResponse response = new ChangeConfigurationResponse("Accepted");
        return createCallResult(messageId, response);
    }

    private String handleClearCache(String chargePointId, String messageId, JsonNode payload) throws Exception {
        ClearCacheRequest request = objectMapper.treeToValue(payload, ClearCacheRequest.class);
        log.info("Frontend requested ClearCache for {}", chargePointId);
        
        ClearCacheResponse response = new ClearCacheResponse("Accepted");
        return createCallResult(messageId, response);
    }

    private String handleTriggerMessage(String chargePointId, String messageId, JsonNode payload) throws Exception {
        TriggerMessageRequest request = objectMapper.treeToValue(payload, TriggerMessageRequest.class);
        log.info("Frontend requested TriggerMessage: {} for connector {}", 
                request.getRequestedMessage(), request.getConnectorId());
        
        TriggerMessageResponse response = new TriggerMessageResponse("Accepted");
        return createCallResult(messageId, response);
    }
}