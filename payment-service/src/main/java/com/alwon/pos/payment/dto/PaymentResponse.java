package com.alwon.pos.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private boolean success;
    private String transactionId;
    private String status;
    private String message;
    private String approvalCode;
    private String externalReference;
}
