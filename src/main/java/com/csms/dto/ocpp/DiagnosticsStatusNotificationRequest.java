package com.csms.dto.ocpp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticsStatusNotificationRequest {
    private String status; // Idle, Uploaded, UploadFailed, Uploading
}