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
public class MeterValue {
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;
    
    private List<SampledValue> sampledValue;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SampledValue {
        private String value;
        private String context; // Interruption.Begin, Interruption.End, Sample.Clock, Sample.Periodic, Transaction.Begin, Transaction.End, Trigger, Other
        private String format; // Raw, SignedData
        private String measurand; // Current.Export, Current.Import, Current.Offered, Energy.Active.Export.Register, Energy.Active.Import.Register, Energy.Reactive.Export.Register, Energy.Reactive.Import.Register, Energy.Active.Export.Interval, Energy.Active.Import.Interval, Energy.Reactive.Export.Interval, Energy.Reactive.Import.Interval, Frequency, Power.Active.Export, Power.Active.Import, Power.Factor, Power.Offered, Power.Reactive.Export, Power.Reactive.Import, RPM, SoC, Temperature, Voltage
        private String phase; // L1, L2, L3, N, L1-N, L2-N, L3-N, L1-L2, L2-L3, L3-L1
        private String location; // Body, Cable, EV, Inlet, Outlet
        private String unit; // Wh, kWh, varh, kvarh, W, kW, VA, kVA, var, kvar, A, V, Celsius, Fahrenheit, K, Percent
    }
}