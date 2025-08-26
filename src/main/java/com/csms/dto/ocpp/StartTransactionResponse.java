package com.csms.dto.ocpp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartTransactionResponse {
    private IdTagInfo idTagInfo;
    private Integer transactionId;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IdTagInfo {
        private String status; // Accepted, Blocked, Expired, Invalid, ConcurrentTx
        private String parentIdTag;
        private LocalDateTime expiryDate;
    }
}
