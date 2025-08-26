package com.csms.dto.ocpp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReserveNowRequest {
    private Integer connectorId;
    private LocalDateTime expiryDate;
    private String idTag;
    private String parentIdTag;
    private Integer reservationId;
}
