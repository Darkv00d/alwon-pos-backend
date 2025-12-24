package com.alwon.pos.payment.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitiatePaymentRequest {

    @NotBlank(message = "Session ID is required")
    @Size(max = 100, message = "Session ID must be less than 100 characters")
    private String sessionId;

    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "PSE|DEBIT|CREDIT", message = "Payment method must be PSE, DEBIT, or CREDIT")
    private String paymentMethod;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Email(message = "Customer email must be valid")
    @Size(max = 200, message = "Email must be less than 200 characters")
    private String customerEmail;

    @Size(max = 200, message = "Customer name must be less than 200 characters")
    private String customerName;

    // PSE specific fields
    @Size(max = 100, message = "Bank name must be less than 100 characters")
    private String bankName;

    // Card specific fields (DEBIT/CREDIT)
    @Size(max = 4, message = "Card last digits must be 4 characters")
    private String cardLastDigits;
}
