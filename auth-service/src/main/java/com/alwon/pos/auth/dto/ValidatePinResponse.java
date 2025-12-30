package com.alwon.pos.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidatePinResponse {
    private boolean success;
    private boolean valid;
    private OperatorDTO operator;
    private Integer attemptsRemaining;
    private String message;
    private boolean requiresLogin;
}
