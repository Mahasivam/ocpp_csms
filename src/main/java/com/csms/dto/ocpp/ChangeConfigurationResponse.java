package com.csms.dto.ocpp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeConfigurationResponse {
    private String status; // Accepted, Rejected, RebootRequired, NotSupported
}
