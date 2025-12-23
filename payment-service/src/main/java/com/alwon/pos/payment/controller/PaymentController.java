package com.alwon.pos.payment.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payments")
public class PaymentController {

    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(Map.of(
                "transactionId", UUID.randomUUID().toString(),
                "status", "PENDING",
                "paymentUrl", "https://mock-pse.com/pay/" + UUID.randomUUID()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String id) {
        return ResponseEntity.ok(Map.of(
                "transactionId", id,
                "status", "APPROVED",
                "completedAt", LocalDateTime.now()));
    }

    @Data
    static class PaymentRequest {
        private String sessionId;
        private String paymentMethod; // PSE or DEBIT
        private BigDecimal amount;
    }
}
