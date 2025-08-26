package com.csms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "connectors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Connector {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_station_id")
    @JsonBackReference
    private ChargingStation chargingStation;

    @Column(name = "connector_id", nullable = false)
    private Integer connectorId;

    private String status = "Unavailable";

    @Column(name = "error_code")
    private String errorCode = "NoError";

    @Column(name = "vendor_id")
    private String vendorId;

    @Column(name = "vendor_error_code")
    private String vendorErrorCode;

    private String info;

    @Column(name = "current_transaction_id")
    private UUID currentTransactionId;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}