package com.csms.dto.ocpp;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StartTransactionRequest {
    private Integer connectorId;
    private String idTag;
    private Integer meterStart;
    private Integer reservationId;
    private LocalDateTime timestamp;
}