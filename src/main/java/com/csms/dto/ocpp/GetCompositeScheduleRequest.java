package com.csms.dto.ocpp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCompositeScheduleRequest {
    private Integer connectorId;
    private Integer duration;
    private String chargingRateUnit; // W, A
}