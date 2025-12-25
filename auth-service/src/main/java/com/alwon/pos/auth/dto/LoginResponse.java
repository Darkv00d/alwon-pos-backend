package com.alwon.pos.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private boolean success;
    private String token;
    private OperatorInfo operator;
    private Long expiresIn; // seconds

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OperatorInfo {
        private Long id;
        private String username;
        private String name;
        private String role;
        private String verificationCode;
    }
}
