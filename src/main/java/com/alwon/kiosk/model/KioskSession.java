package com.alwon.kiosk.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "kiosk_sessions")
@Data
public class KioskSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sessionId;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String customerName;

    private String customerPhotoUrl;

    private String tower;

    private String apartment;

    @Column(columnDefinition = "TEXT")
    private String biometricDataJson;

    @Column(nullable = false)
    private String status; // active, completed, expired, cancelled

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime completedAt;
}
