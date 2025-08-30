package com.csms.dto.ocpp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetChargingProfileRequest {
    private Integer connectorId;
    private ChargingProfile csChargingProfiles;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChargingProfile {
        private Integer chargingProfileId;
        private Integer transactionId;
        private Integer stackLevel;
        private String chargingProfilePurpose; // ChargePointMaxProfile, TxDefaultProfile, TxProfile
        private String chargingProfileKind; // Absolute, Recurring, Relative
        private String recurrencyKind; // Daily, Weekly
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        private LocalDateTime validFrom;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        private LocalDateTime validTo;
        
        private ChargingSchedule chargingSchedule;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChargingSchedule {
        private Integer duration;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        private LocalDateTime startSchedule;
        
        private String chargingRateUnit; // W, A
        private List<ChargingSchedulePeriod> chargingSchedulePeriod;
        private Double minChargingRate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChargingSchedulePeriod {
        private Integer startPeriod;
        private Double limit;
        private Integer numberPhases;
    }
}