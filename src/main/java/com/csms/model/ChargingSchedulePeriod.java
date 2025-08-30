package com.csms.model;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "charging_schedule_periods")
@Data
public class ChargingSchedulePeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "charging_schedule_id", nullable = false)
    private ChargingSchedule chargingSchedule;
    
    @Column(nullable = false)
    private Integer startPeriod;
    
    @Column(name = "limit_value", nullable = false)
    private Double limit;
    
    private Integer numberPhases;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}