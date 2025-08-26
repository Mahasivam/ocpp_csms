package com.csms.dto.ocpp;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatusNotificationRequest {
    private Integer connectorId;
    private String status;
    private String errorCode;
    private String info;
    private LocalDateTime timestamp;
    private String vendorId;
    private String vendorErrorCode;
}