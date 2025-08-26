package com.csms.dto.ocpp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReserveNowResponse {
    private String status; // Accepted, Faulted, Occupied, Rejected, Unavailable
}