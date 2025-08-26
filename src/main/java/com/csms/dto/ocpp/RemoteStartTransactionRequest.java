package com.csms.dto.ocpp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoteStartTransactionRequest {
    private String idTag;
    private Integer connectorId;
    private ChargingProfile chargingProfile;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChargingProfile {
        private Integer chargingProfileId;
        private Integer transactionId;
        private Integer stackLevel;
        private String chargingProfilePurpose;
        private String chargingProfileKind;
        private String recurrencyKind;
        private String validFrom;
        private String validTo;
        private ChargingSchedule chargingSchedule;

        @Data
        public static class ChargingSchedule {
            private String duration;
            private String startSchedule;
            private String chargingRateUnit;
            private java.util.List<ChargingSchedulePeriod> chargingSchedulePeriod;

            @Data
            public static class ChargingSchedulePeriod {
                private Integer startPeriod;
                private Double limit;
                private Integer numberPhases;
            }
        }
    }
}