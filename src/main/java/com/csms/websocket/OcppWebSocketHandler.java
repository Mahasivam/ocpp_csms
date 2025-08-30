package com.csms.websocket;

import com.csms.service.OcppMessageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class OcppWebSocketHandler implements WebSocketHandler {

    @Lazy
    private final OcppMessageService ocppMessageService;
    private final ObjectMapper objectMapper;

    // Store active WebSocket sessions by charge point ID
    private final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String chargePointId = extractChargePointId(session);
        if (chargePointId != null) {
            activeSessions.put(chargePointId, session);
            log.info("WebSocket connection established for charge point: {}", chargePointId);
        } else {
            log.warn("Invalid WebSocket connection - no charge point ID found");
            session.close();
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String chargePointId = extractChargePointId(session);
        if (chargePointId == null) {
            log.error("No charge point ID found in session");
            return;
        }

        String payload = message.getPayload().toString();
        log.debug("Received message from {}: {}", chargePointId, payload);

        try {
            // Parse OCPP message array [MessageType, MessageId, Action, Payload]
            JsonNode messageArray = objectMapper.readTree(payload);

            if (messageArray.isArray() && messageArray.size() >= 3) {
                int messageType = messageArray.get(0).asInt();
                String messageId = messageArray.get(1).asText();

                if (messageType == 2) { // CALL message
                    String action = messageArray.get(2).asText();
                    JsonNode messagePayload = messageArray.size() > 3 ? messageArray.get(3) : null;

                    String response = ocppMessageService.handleMessage(
                            chargePointId, messageId, action, messagePayload);

                    if (response != null) {
                        session.sendMessage(new TextMessage(response));
                    }
                } else if (messageType == 3) { // CALLRESULT message
                    // Handle response from charge point
                    JsonNode responsePayload = messageArray.get(2);
                    ocppMessageService.handleResponse(chargePointId, messageId, responsePayload);
                } else if (messageType == 4) { // CALLERROR message
                    String errorCode = messageArray.get(2).asText();
                    String errorDescription = messageArray.get(3).asText();
                    ocppMessageService.handleError(chargePointId, messageId, errorCode, errorDescription);
                }
            }
        } catch (Exception e) {
            log.error("Error processing message from {}: {}", chargePointId, e.getMessage(), e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String chargePointId = extractChargePointId(session);
        log.error("WebSocket transport error for charge point {}: {}", chargePointId, exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String chargePointId = extractChargePointId(session);
        if (chargePointId != null) {
            activeSessions.remove(chargePointId);
            log.info("WebSocket connection closed for charge point: {} - {}", chargePointId, closeStatus);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void sendMessage(String chargePointId, String message) throws IOException {
        WebSocketSession session = activeSessions.get(chargePointId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
            log.debug("Sent message to {}: {}", chargePointId, message);
        } else {
            log.warn("No active session found for charge point: {}", chargePointId);
        }
    }

    public boolean isConnected(String chargePointId) {
        WebSocketSession session = activeSessions.get(chargePointId);
        return session != null && session.isOpen();
    }

    private String extractChargePointId(WebSocketSession session) {
        String path = session.getUri().getPath();
        String[] segments = path.split("/");
        if (segments.length >= 3) {
            return segments[segments.length - 1]; // Last segment is charge point ID
        }
        return null;
    }
}
