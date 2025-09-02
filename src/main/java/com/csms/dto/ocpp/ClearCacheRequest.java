package com.csms.dto.ocpp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClearCacheRequest {
    // ClearCache request has no payload in OCPP 1.6J specification
    // This DTO exists for structural completeness and future extensibility
}