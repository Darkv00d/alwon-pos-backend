package com.alwon.pos.auth.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "operators", schema = "auth")
@NoArgsConstructor
@AllArgsConstructor
public class Operator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OperatorRole role;

    @Column(name = "store_id", nullable = false)
    private Integer storeId;

    @Column(name = "verification_code", nullable = false, length = 6)
    private String verificationCode;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;

    @Column(name = "last_failed_login")
    private LocalDateTime lastFailedLogin;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum OperatorRole {
        CASHIER, SUPERVISOR, ADMIN
    }
}
