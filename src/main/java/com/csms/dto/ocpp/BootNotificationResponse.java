package com.csms.dto.ocpp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BootNotificationResponse {
    private String status; // Accepted, Pending, Rejected
    private LocalDateTime currentTime;
    private Integer interval;
}