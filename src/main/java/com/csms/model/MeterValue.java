package com.csms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "meter_values")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeterValue {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_station_id")
    private ChargingStation chargingStation;

    @Column(name = "connector_id")
    private Integer connectorId;

    @Column(name = "transaction_id")
    private Integer transactionId;

    private LocalDateTime timestamp;

    @Column(name = "meter_value")
    private Integer meterValue;

    private String context;

    private String format;

    private String measurand;

    private String phase;

    private String location;

    private String unit;
}