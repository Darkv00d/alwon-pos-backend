package com.alwon.pos.payment.service;

import com.alwon.pos.payment.dto.*;
import com.alwon.pos.payment.model.PaymentTransaction;
import com.alwon.pos.payment.model.PaymentTransaction.PaymentMethod;
import com.alwon.pos.payment.model.PaymentTransaction.PaymentStatus;
import com.alwon.pos.payment.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentTransactionRepository paymentRepository;
    private final Random random = new Random();

    @Transactional
    public PaymentResponse initiatePayment(InitiatePaymentRequest request) {
        log.info("Initiating payment for session: {} with method: {}",
                request.getSessionId(), request.getPaymentMethod());

        // Generate transaction ID
        String transactionId = UUID.randomUUID().toString();

        // Create payment transaction
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionId(transactionId);
        transaction.setSessionId(request.getSessionId());
        transaction.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()));
        transaction.setAmount(request.getAmount());
        transaction.setStatus(PaymentStatus.PENDING);
        transaction.setCustomerEmail(request.getCustomerEmail());
        transaction.setCustomerName(request.getCustomerName());
        transaction.setBankName(request.getBankName());
        transaction.setCardLastDigits(request.getCardLastDigits());

        paymentRepository.save(transaction);

        log.info("Payment transaction created with ID: {}", transactionId);

        PaymentResponse response = new PaymentResponse();
        response.setSuccess(true);
        response.setTransactionId(transactionId);
        response.setStatus(PaymentStatus.PENDING.name());
        response.setMessage("Payment initiated successfully. Processing...");

        return response;
    }

    @Transactional
    public PaymentResponse processPayment(String transactionId) {
        log.info("Processing payment for transaction: {}", transactionId);

        PaymentTransaction transaction = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction not found with ID: " + transactionId));

        if (transaction.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException(
                    "Transaction " + transactionId + " is not in PENDING status");
        }

        transaction.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(transaction);

        // Mock payment processing
        PaymentResponse response = mockPaymentGateway(transaction);

        // Update transaction based on response
        if (response.isSuccess()) {
            transaction.setStatus(PaymentStatus.APPROVED);
            transaction.setApprovalCode(response.getApprovalCode());
            transaction.setExternalReference(response.getExternalReference());
            transaction.setResponseCode("00");
            transaction.setResponseMessage("Payment approved");
            transaction.setCompletedAt(LocalDateTime.now());
        } else {
            transaction.setStatus(PaymentStatus.REJECTED);
            transaction.setResponseCode("99");
            transaction.setResponseMessage(response.getMessage());
        }

        paymentRepository.save(transaction);

        log.info("Payment processed for transaction: {} with status: {}",
                transactionId, transaction.getStatus());

        return response;
    }

    @Transactional(readOnly = true)
    public PaymentTransactionDto getTransaction(String transactionId) {
        log.debug("Fetching transaction: {}", transactionId);
        PaymentTransaction transaction = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction not found with ID: " + transactionId));
        return convertToDto(transaction);
    }

    @Transactional(readOnly = true)
    public List<PaymentTransactionDto> getTransactionsBySession(String sessionId) {
        log.debug("Fetching transactions for session: {}", sessionId);
        return paymentRepository.findBySessionId(sessionId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentTransactionDto> getTransactionsByStatus(String status) {
        log.debug("Fetching transactions with status: {}", status);
        PaymentStatus paymentStatus = PaymentStatus.valueOf(status);
        return paymentRepository.findByStatus(paymentStatus).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentResponse cancelPayment(String transactionId) {
        log.info("Cancelling payment for transaction: {}", transactionId);

        PaymentTransaction transaction = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction not found with ID: " + transactionId));

        if (transaction.getStatus() == PaymentStatus.APPROVED) {
            throw new IllegalStateException(
                    "Cannot cancel an approved transaction. Use refund instead.");
        }

        transaction.setStatus(PaymentStatus.CANCELLED);
        transaction.setResponseMessage("Cancelled by user");
        transaction.setCompletedAt(LocalDateTime.now());
        paymentRepository.save(transaction);

        PaymentResponse response = new PaymentResponse();
        response.setSuccess(true);
        response.setTransactionId(transactionId);
        response.setStatus(PaymentStatus.CANCELLED.name());
        response.setMessage("Payment cancelled successfully");

        return response;
    }

    /**
     * Mock payment gateway - simulates PSE, DEBIT, and CREDIT processing
     * In production, this would integrate with real payment providers
     */
    private PaymentResponse mockPaymentGateway(PaymentTransaction transaction) {
        log.debug("Mock processing payment via {}", transaction.getPaymentMethod());

        // Simulate processing delay
        try {
            Thread.sleep(1000 + random.nextInt(2000)); // 1-3 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        PaymentResponse response = new PaymentResponse();
        response.setTransactionId(transaction.getTransactionId());

        // 90% success rate for mock
        boolean success = random.nextInt(100) < 90;

        if (success) {
            response.setSuccess(true);
            response.setStatus(PaymentStatus.APPROVED.name());
            response.setMessage("Payment approved");
            response.setApprovalCode(generateApprovalCode());
            response.setExternalReference("EXT-" + UUID.randomUUID().toString().substring(0, 8));
        } else {
            response.setSuccess(false);
            response.setStatus(PaymentStatus.REJECTED.name());
            response.setMessage(getRandomRejectionReason());
        }

        return response;
    }

    private String generateApprovalCode() {
        return "APP-" + (100000 + random.nextInt(900000));
    }

    private String getRandomRejectionReason() {
        String[] reasons = {
                "Insufficient funds",
                "Card declined",
                "Bank timeout",
                "Invalid card",
                "Transaction limit exceeded"
        };
        return reasons[random.nextInt(reasons.length)];
    }

    private PaymentTransactionDto convertToDto(PaymentTransaction transaction) {
        PaymentTransactionDto dto = new PaymentTransactionDto();
        dto.setId(transaction.getId());
        dto.setTransactionId(transaction.getTransactionId());
        dto.setSessionId(transaction.getSessionId());
        dto.setPaymentMethod(transaction.getPaymentMethod().name());
        dto.setAmount(transaction.getAmount());
        dto.setStatus(transaction.getStatus().name());
        dto.setExternalReference(transaction.getExternalReference());
        dto.setResponseCode(transaction.getResponseCode());
        dto.setResponseMessage(transaction.getResponseMessage());
        dto.setCustomerEmail(transaction.getCustomerEmail());
        dto.setCustomerName(transaction.getCustomerName());
        dto.setBankName(transaction.getBankName());
        dto.setCardLastDigits(transaction.getCardLastDigits());
        dto.setApprovalCode(transaction.getApprovalCode());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setUpdatedAt(transaction.getUpdatedAt());
        dto.setCompletedAt(transaction.getCompletedAt());
        return dto;
    }
}

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
