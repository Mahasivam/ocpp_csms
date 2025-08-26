package com.csms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ocpp_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcppMessage {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_station_id")
    private ChargingStation chargingStation;

    @Column(name = "message_type", nullable = false)
    private String messageType;

    @Column(name = "message_direction", nullable = false)
    private String messageDirection;

    @Column(name = "unique_id", nullable = false)
    private String uniqueId;

    private String action;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String payload;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_description")
    private String errorDescription;

    @CreationTimestamp
    private LocalDateTime timestamp;
}
