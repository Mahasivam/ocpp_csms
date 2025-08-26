package com.csms.dto.ocpp;

import lombok.Data;

import java.util.List;

@Data
public class MeterValuesRequest {
    private Integer connectorId;
    private Integer transactionId;
    private List<StopTransactionRequest.MeterValue> meterValue;
}
