package com.alwon.pos.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionDto {
    private Long id;
    private String transactionId;
    private String sessionId;
    private String paymentMethod;
    private BigDecimal amount;
    private String status;
    private String externalReference;
    private String responseCode;
    private String responseMessage;
    private String customerEmail;
    private String customerName;
    private String bankName;
    private String cardLastDigits;
    private String approvalCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}
