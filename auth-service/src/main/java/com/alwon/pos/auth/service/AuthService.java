package com.alwon.pos.auth.service;

import com.alwon.pos.auth.dto.*;
import com.alwon.pos.auth.model.AuditLog;
import com.alwon.pos.auth.model.Operator;
import com.alwon.pos.auth.model.OperatorSession;
import com.alwon.pos.auth.repository.AuditLogRepository;
import com.alwon.pos.auth.repository.OperatorRepository;
import com.alwon.pos.auth.repository.OperatorSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * Main authentication service with PIN-based 2-level auth
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final OperatorRepository operatorRepository;
    private final OperatorSessionRepository sessionRepository;
    private final AuditLogRepository auditLogRepository;
    private final PinService pinService;
    private final NotificationService notificationService;
    private final CentralSystemClient centralSystemClient;
    private final JwtService jwtService;

    /**
     * Login: Validate credentials, generate PIN, send notifications
     */
    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress, String userAgent) {
        log.info("Login attempt for username: {}", request.getUsername());

        // 1. Validate against Central System
        boolean isValid = centralSystemClient.validateOperatorCredentials(
                request.getUsername(),
                request.getPassword());

        if (!isValid) {
            logAudit(null, "LOGIN_FAILED", null, null, ipAddress, userAgent, false, "Invalid credentials");
            throw new RuntimeException("Invalid credentials");
        }

        // 2. Get operator from database
        Operator operator = operatorRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Operator not found in local database"));

        if (!operator.getActive()) {
            logAudit(operator.getId(), "LOGIN_FAILED", "OPERATOR", operator.getId().toString(),
                    ipAddress, userAgent, false, "Operator inactive");
            throw new RuntimeException("Operator account is inactive");
        }

        // 3. Generate PIN
        String pin = pinService.generatePin();
        pinService.storePinHash(operator.getId(), pin);

        // 4. Send notifications (async - don't block)
        CompletableFuture<Boolean> whatsappFuture = notificationService.sendPinViaWhatsApp(
                operator.getPhone(),
                operator.getFullName(),
                pin);

        CompletableFuture<Boolean> emailFuture = notificationService.sendPinViaEmail(
                operator.getEmail(),
                operator.getFullName(),
                pin);

        // 5. Generate JWT
        String token = jwtService.createToken(operator);
        String jti = jwtService.extractJti(token);

        // 6. Save session
        OperatorSession session = new OperatorSession();
        session.setOperatorId(operator.getId());
        session.setTokenJti(jti);
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setExpiresAt(LocalDateTime.now().plusHours(8));
        sessionRepository.save(session);

        // 7. Update last login
        operator.setLastLoginAt(LocalDateTime.now());
        operatorRepository.save(operator);

        // 8. Log audit
        logAudit(operator.getId(), "LOGIN", "OPERATOR", operator.getId().toString(),
                ipAddress, userAgent, true, null);

        // 9. Wait for notifications to complete (with timeout)
        boolean whatsappSent = false;
        boolean emailSent = false;

        try {
            whatsappSent = whatsappFuture.get();
            emailSent = emailFuture.get();
        } catch (Exception e) {
            log.error("Error waiting for notifications: {}", e.getMessage());
        }

        // 10. Build response with OperatorInfo
        LoginResponse.OperatorInfo operatorInfo = new LoginResponse.OperatorInfo(
                operator.getId(),
                operator.getUsername(),
                operator.getFullName(),
                operator.getRole().name(),
                pin); // Include the actual PIN

        // Build notification info
        LoginResponse.NotificationInfo notificationInfo = LoginResponse.NotificationInfo.builder()
                .whatsapp(LoginResponse.WhatsAppNotification.builder()
                        .sent(whatsappSent)
                        .maskedPhone(maskPhone(operator.getPhone()))
                        .build())
                .email(LoginResponse.EmailNotification.builder()
                        .sent(emailSent)
                        .maskedEmail(maskEmail(operator.getEmail()))
                        .build())
                .build();

        // Calculate PIN expiration (8 hours from now)
        String pinExpiresAt = LocalDateTime.now().plusHours(8).toString();

        return LoginResponse.builder()
                .success(true)
                .operator(operatorInfo)
                .token(token)
                .expiresIn(8 * 3600L) // 8 hours in seconds
                .pin(pin) // Include PIN in response (only shown once)
                .pinExpiresAt(pinExpiresAt)
                .notifications(notificationInfo)
                .build();
    }

    /**
     * Mask phone number for privacy (show last 4 digits)
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4)
            return "***";
        return "***-***-" + phone.substring(phone.length() - 4);
    }

    /**
     * Mask email for privacy (show first char and domain)
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@"))
            return "***";
        String[] parts = email.split("@");
        return parts[0].charAt(0) + "***@" + parts[1];
    }

    /**
     * Validate PIN entered by operator
     */
    @Transactional
    public ValidatePinResponse validatePin(Long operatorId, String pin) {
        log.info("PIN validation attempt for operator: {}", operatorId);

        Operator operator = operatorRepository.findById(operatorId)
                .orElseThrow(() -> new RuntimeException("Operator not found"));

        PinService.PinValidationResult result = pinService.validatePin(operatorId, pin);

        if (result.isValid()) {
            logAudit(operatorId, "PIN_VALIDATED", "PIN", null, null, null, true, null);

            return ValidatePinResponse.builder()
                    .success(true)
                    .valid(true)
                    .operator(toDTO(operator))
                    .build();

        } else if (result.isMaxAttemptsExceeded()) {
            logAudit(operatorId, "MAX_ATTEMPTS_EXCEEDED", "PIN", null, null, null, false, "Max attempts reached");

            return ValidatePinResponse.builder()
                    .success(false)
                    .valid(false)
                    .requiresLogin(true)
                    .message("Maximum PIN attempts exceeded. Please login again.")
                    .build();

        } else if (result.isExpired()) {
            logAudit(operatorId, "PIN_EXPIRED", "PIN", null, null, null, false, "PIN expired");

            return ValidatePinResponse.builder()
                    .success(false)
                    .valid(false)
                    .requiresLogin(true)
                    .message("PIN has expired. Please login again.")
                    .build();

        } else {
            logAudit(operatorId, "PIN_FAILED", "PIN", null, null, null, false, "Invalid PIN");

            return ValidatePinResponse.builder()
                    .success(false)
                    .valid(false)
                    .attemptsRemaining(result.getAttemptsRemaining())
                    .message("Invalid PIN. " + result.getAttemptsRemaining() + " attempts remaining.")
                    .build();
        }
    }

    /**
     * Logout: Revoke session and delete PIN
     */
    @Transactional
    public void logout(Long operatorId) {
        log.info("Logout for operator: {}", operatorId);

        // Delete PIN from Redis
        pinService.deletePinData(operatorId);

        // Revoke all active sessions for this operator
        sessionRepository.deleteByOperatorIdAndRevokedFalse(operatorId);

        // Log audit
        logAudit(operatorId, "LOGOUT", "SESSION", null, null, null, true, null);
    }

    /**
     * Check if session is still valid
     */
    public boolean checkSession(Long operatorId) {
        Operator operator = operatorRepository.findById(operatorId).orElse(null);
        return operator != null && operator.getActive() && pinService.pinExists(operatorId);
    }

    /**
     * Convert Operator entity to DTO
     */
    private OperatorDTO toDTO(Operator operator) {
        return OperatorDTO.builder()
                .id(operator.getId())
                .username(operator.getUsername())
                .fullName(operator.getFullName())
                .email(operator.getEmail())
                .phone(operator.getPhone())
                .role(operator.getRole().name())
                .build();
    }

    /**
     * Log audit trail
     */
    private void logAudit(Long operatorId, String action, String entityType, String entityId,
            String ipAddress, String userAgent, boolean success, String errorMessage) {
        try {
            AuditLog log = new AuditLog();
            log.setOperatorId(operatorId);
            log.setAction(action);
            log.setEntityType(entityType);
            log.setEntityId(entityId);
            log.setIpAddress(ipAddress);
            log.setUserAgent(userAgent);
            log.setSuccess(success);
            log.setErrorMessage(errorMessage);
            auditLogRepository.save(log);
        } catch (Exception e) {
            log.error("Error saving audit log: {}", e.getMessage());
        }
    }
}
