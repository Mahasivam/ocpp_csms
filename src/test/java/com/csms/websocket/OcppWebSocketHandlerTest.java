package com.csms.websocket;

import com.csms.service.OcppMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OcppWebSocketHandlerTest {

    @Mock
    private OcppMessageService ocppMessageService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private WebSocketSession webSocketSession;

    @InjectMocks
    private OcppWebSocketHandler webSocketHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAfterConnectionEstablished() throws Exception {
        // Given
        URI uri = new URI("ws://localhost:8080/ocpp/CP001");
        when(webSocketSession.getUri()).thenReturn(uri);

        // When
        webSocketHandler.afterConnectionEstablished(webSocketSession);

        // Then
        assertTrue(webSocketHandler.isConnected("CP001"));
    }

    @Test
    void testHandleBootNotificationMessage() throws Exception {
        // Given
        URI uri = new URI("ws://localhost:8080/ocpp/CP001");
        when(webSocketSession.getUri()).thenReturn(uri);
        
        String bootNotificationMessage = "[2,\"msg001\",\"BootNotification\",{\"chargePointVendor\":\"TestVendor\",\"chargePointModel\":\"TestModel\"}]";
        TextMessage textMessage = new TextMessage(bootNotificationMessage);
        
        when(ocppMessageService.handleMessage(anyString(), anyString(), anyString(), any()))
                .thenReturn("[3,\"msg001\",{\"status\":\"Accepted\",\"currentTime\":\"2024-01-15T10:30:00.000Z\",\"interval\":300}]");

        // When
        webSocketHandler.afterConnectionEstablished(webSocketSession);
        webSocketHandler.handleMessage(webSocketSession, textMessage);

        // Then
        verify(ocppMessageService).handleMessage(eq("CP001"), eq("msg001"), eq("BootNotification"), any());
        verify(webSocketSession).sendMessage(any(TextMessage.class));
    }

    @Test
    void testSendMessage() throws Exception {
        // Given
        URI uri = new URI("ws://localhost:8080/ocpp/CP001");
        when(webSocketSession.getUri()).thenReturn(uri);
        when(webSocketSession.isOpen()).thenReturn(true);

        webSocketHandler.afterConnectionEstablished(webSocketSession);

        // When
        webSocketHandler.sendMessage("CP001", "test message");

        // Then
        verify(webSocketSession).sendMessage(any(TextMessage.class));
    }
}