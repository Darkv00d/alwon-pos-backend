package com.alwon.pos.access.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "access_log", schema = "access")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "client_type", nullable = false, length = 20)
    private String clientType;

    @Column(name = "customer_id", length = 100)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_type", nullable = false, length = 10)
    private AccessType accessType;

    @Column(name = "entry_time")
    private LocalDateTime entryTime;

    @Column(name = "exit_time")
    private LocalDateTime exitTime;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum AccessType {
        ENTRY,
        EXIT
    }
}
