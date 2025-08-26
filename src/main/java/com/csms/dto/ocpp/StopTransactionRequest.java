package com.csms.dto.ocpp;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class StopTransactionRequest {
    private String idTag;
    private Integer meterStop;
    private LocalDateTime timestamp;
    private Integer transactionId;
    private String reason;
    private List<MeterValue> transactionData;

    @Data
    public static class MeterValue {
        private LocalDateTime timestamp;
        private List<SampledValue> sampledValue;

        @Data
        public static class SampledValue {
            private String value;
            private String context;
            private String format;
            private String measurand;
            private String phase;
            private String location;
            private String unit;
        }
    }
}