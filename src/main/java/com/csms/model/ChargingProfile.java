package com.csms.model;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "charging_profiles")
@Data
public class ChargingProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "charging_station_id", nullable = false)
    private ChargingStation chargingStation;
    
    @Column(nullable = false)
    private Integer chargingProfileId;
    
    private Integer connectorId;
    private Integer transactionId;
    private Integer stackLevel;
    
    @Column(nullable = false)
    private String chargingProfilePurpose; // ChargePointMaxProfile, TxDefaultProfile, TxProfile
    
    @Column(nullable = false) 
    private String chargingProfileKind; // Absolute, Recurring, Relative
    
    private String recurrencyKind; // Daily, Weekly
    
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    
    @OneToMany(mappedBy = "chargingProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChargingSchedule> chargingSchedules;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.Accepted;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public enum Status {
        Accepted, Rejected, NotSupported
    }
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}