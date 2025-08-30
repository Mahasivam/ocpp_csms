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
public class GetCompositeScheduleResponse {
    private String status; // Accepted, Rejected
    private Integer connectorId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime scheduleStart;
    
    private ChargingSchedule chargingSchedule;
    
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