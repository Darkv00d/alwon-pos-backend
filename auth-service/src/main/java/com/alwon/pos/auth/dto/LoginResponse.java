package com.alwon.pos.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private boolean success;
    private String token;
    private OperatorInfo operator;
    private Long expiresIn; // seconds

    // PIN fields - Required by frontend
    private String pin; // 6-digit PIN (only shown once)
    private String pinExpiresAt; // ISO datetime

    private NotificationInfo notifications;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OperatorInfo {
        private Long id;
        private String username;
        private String name;
        private String role;
        private String verificationCode; // Legacy field
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NotificationInfo {
        private WhatsAppNotification whatsapp;
        private EmailNotification email;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WhatsAppNotification {
        private boolean sent;
        private String maskedPhone;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmailNotification {
        private boolean sent;
        private String maskedEmail;
    }
}
