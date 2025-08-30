package com.csms.dto.ocpp;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataTransferRequest {
    private String vendorId;
    private String messageId;
    private JsonNode data;
}