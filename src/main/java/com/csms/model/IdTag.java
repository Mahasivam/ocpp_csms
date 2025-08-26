package com.csms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "id_tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdTag {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "id_tag", unique = true, nullable = false)
    private String idTag;

    @Column(name = "parent_id_tag")
    private String parentIdTag;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    private String status = "Accepted";

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
