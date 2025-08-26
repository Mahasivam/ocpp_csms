package com.csms.dto.ocpp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetConfigurationResponse {
    private List<KeyValue> configurationKey;
    private List<String> unknownKey;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KeyValue {
        private String key;
        private Boolean readonly;
        private String value;
    }
}
