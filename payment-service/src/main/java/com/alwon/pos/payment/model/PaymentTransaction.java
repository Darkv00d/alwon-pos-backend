package com.alwon.pos.payment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions", schema = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", unique = true, nullable = false, length = 100)
    private String transactionId;

    @Column(name = "session_id", nullable = false, length = 100)
    private String sessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "external_reference", length = 200)
    private String externalReference;

    @Column(name = "response_code", length = 50)
    private String responseCode;

    @Column(name = "response_message", columnDefinition = "TEXT")
    private String responseMessage;

    @Column(name = "customer_email", length = 200)
    private String customerEmail;

    @Column(name = "customer_name", length = 200)
    private String customerName;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "card_last_digits", length = 4)
    private String cardLastDigits;

    @Column(name = "approval_code", length = 50)
    private String approvalCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum PaymentMethod {
        PSE, // Pagos Seguros en Línea (Colombia)
        DEBIT, // Débito
        CREDIT // Crédito
    }

    public enum PaymentStatus {
        PENDING, // Iniciado, esperando confirmación
        PROCESSING, // En proceso de verificación
        APPROVED, // Aprobado
        REJECTED, // Rechazado
        FAILED, // Falló técnicamente
        CANCELLED // Cancelado por el usuario
    }
}
