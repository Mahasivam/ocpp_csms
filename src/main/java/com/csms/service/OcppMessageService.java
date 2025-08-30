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
}