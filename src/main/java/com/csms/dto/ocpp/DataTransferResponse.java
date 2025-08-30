package com.csms.dto.ocpp;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataTransferResponse {
    private String status; // Accepted, Rejected, UnknownMessageId, UnknownVendorId
    private JsonNode data;
}