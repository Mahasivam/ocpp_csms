package com.csms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "diagnostics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Diagnostics {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_station_id")
    private ChargingStation chargingStation;

    @Column(name = "location", nullable = false, length = 500)
    private String location;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "stop_time")
    private LocalDateTime stopTime;

    private Integer retries;

    @Column(name = "retry_interval")
    private Integer retryInterval;

    private String status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
