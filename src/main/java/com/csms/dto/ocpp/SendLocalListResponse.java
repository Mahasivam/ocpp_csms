package com.csms.dto.ocpp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendLocalListResponse {
    private String status; // Accepted, Failed, NotSupported, VersionMismatch
}