package com.alwon.pos.session.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_sessions", schema = "sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", unique = true, nullable = false)
    private String sessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false)
    private ClientType clientType;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "tower")
    private String tower;

    @Column(name = "apartment")
    private String apartment;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "photo_url")
    private String customerPhotoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
