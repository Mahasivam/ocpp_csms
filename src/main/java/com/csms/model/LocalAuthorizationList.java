package com.csms.model;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "local_authorization_list")
@Data
public class LocalAuthorizationList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "charging_station_id", nullable = false)
    private ChargingStation chargingStation;
    
    @Column(nullable = false)
    private Integer listVersion;
    
    @Column(nullable = false)
    private String updateType; // Differential, Full
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.Accepted;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public enum Status {
        Accepted, Failed, NotSupported, VersionMismatch
    }
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}