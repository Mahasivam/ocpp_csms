package com.csms.dto.ocpp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetChargingProfileResponse {
    private String status; // Accepted, Rejected, NotSupported
}