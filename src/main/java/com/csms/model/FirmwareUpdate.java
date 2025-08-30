package com.csms.model;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "firmware_updates")
@Data
public class FirmwareUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "charging_station_id", nullable = false)
    private ChargingStation chargingStation;
    
    @Column(nullable = false)
    private String location;
    
    @Column(nullable = false)
    private LocalDateTime retrieveDate;
    
    private Integer retries;
    private Integer retryInterval;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FirmwareStatus status = FirmwareStatus.Idle;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public enum FirmwareStatus {
        Downloaded, DownloadFailed, Downloading, Idle, InstallationFailed, Installing, Installed
    }
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}