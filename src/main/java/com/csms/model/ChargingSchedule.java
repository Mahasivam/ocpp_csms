package com.csms.model;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "charging_schedules")
@Data
public class ChargingSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "charging_profile_id", nullable = false)
    private ChargingProfile chargingProfile;
    
    private Integer duration;
    private LocalDateTime startSchedule;
    
    @Column(nullable = false)
    private String chargingRateUnit; // W, A
    
    @OneToMany(mappedBy = "chargingSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChargingSchedulePeriod> chargingSchedulePeriods;
    
    private Double minChargingRate;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}