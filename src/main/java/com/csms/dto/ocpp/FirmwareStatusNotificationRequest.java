package com.csms.dto.ocpp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirmwareStatusNotificationRequest {
    private String status; // Downloaded, DownloadFailed, Downloading, Idle, InstallationFailed, Installing, Installed
}