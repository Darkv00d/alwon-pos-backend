package com.alwon.pos.payment.controller;

import com.alwon.pos.payment.dto.*;
import com.alwon.pos.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    @Operation(summary = "Initiate a new payment")
    public ResponseEntity<PaymentResponse> initiatePayment(
            @Valid @RequestBody InitiatePaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.initiatePayment(request));
    }

    @PostMapping("/{transactionId}/process")
    @Operation(summary = "Process a pending payment")
    public ResponseEntity<PaymentResponse> processPayment(
            @PathVariable String transactionId) {
        return ResponseEntity.ok(paymentService.processPayment(transactionId));
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "Get payment transaction by ID")
    public ResponseEntity<PaymentTransactionDto> getTransaction(
            @PathVariable String transactionId) {
        return ResponseEntity.ok(paymentService.getTransaction(transactionId));
    }

    @GetMapping("/session/{sessionId}")
    @Operation(summary = "Get all payments for a session")
    public ResponseEntity<List<PaymentTransactionDto>> getSessionPayments(
            @PathVariable String sessionId) {
        return ResponseEntity.ok(paymentService.getTransactionsBySession(sessionId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by status")
    public ResponseEntity<List<PaymentTransactionDto>> getPaymentsByStatus(
            @PathVariable String status) {
        return ResponseEntity.ok(paymentService.getTransactionsByStatus(status));
    }

    @PostMapping("/{transactionId}/cancel")
    @Operation(summary = "Cancel a pending payment")
    public ResponseEntity<PaymentResponse> cancelPayment(
            @PathVariable String transactionId) {
        return ResponseEntity.ok(paymentService.cancelPayment(transactionId));
    }
}
