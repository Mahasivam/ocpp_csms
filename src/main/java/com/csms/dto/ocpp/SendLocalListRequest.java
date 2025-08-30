package com.csms.dto.ocpp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendLocalListRequest {
    private Integer listVersion;
    private String updateType; // Differential, Full
    private List<AuthorizationData> localAuthorizationList;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorizationData {
        private String idTag;
        private IdTagInfo idTagInfo;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdTagInfo {
        private String status; // Accepted, Blocked, Expired, Invalid, ConcurrentTx
        private String expiryDate;
        private String parentIdTag;
    }
}