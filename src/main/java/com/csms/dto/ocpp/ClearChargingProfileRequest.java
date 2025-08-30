package com.csms.dto.ocpp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClearChargingProfileRequest {
    private Integer id;
    private Integer connectorId;
    private String chargingProfilePurpose; // ChargePointMaxProfile, TxDefaultProfile, TxProfile
    private Integer stackLevel;
}