package com.alwon.pos.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Service for PIN generation, storage, and validation using Redis
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PinService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Value("${pin.expiration.hours:8}")
    private int pinExpirationHours;

    @Value("${pin.max.attempts:3}")
    private int maxPinAttempts;

    private static final String PIN_KEY_PREFIX = "pin:operator:";

    /**
     * Generate a random 6-digit PIN
     */
    public String generatePin() {
        Random random = new Random();
        int pin = 100000 + random.nextInt(900000); // Generates 100000 to 999999
        return String.valueOf(pin);
    }

    /**
     * Store PIN hash in Redis with TTL
     */
    public void storePinHash(Long operatorId, String pin) {
        String key = PIN_KEY_PREFIX + operatorId;
        String pinHash = passwordEncoder.encode(pin);

        PinData pinData = new PinData();
        pinData.setPinHash(pinHash);
        pinData.setAttempts(0);
        pinData.setCreatedAt(LocalDateTime.now());
        pinData.setExpiresAt(LocalDateTime.now().plusHours(pinExpirationHours));

        try {
            // Store as JSON in Redis with TTL
            long ttlSeconds = pinExpirationHours * 3600L;
            redisTemplate.opsForValue().set(key, pinData, ttlSeconds, TimeUnit.SECONDS);

            log.info("PIN stored for operator {} with TTL {} hours", operatorId, pinExpirationHours);
        } catch (Exception e) {
            log.error("Error storing PIN in Redis for operator {}", operatorId, e);
            throw new RuntimeException("Failed to store PIN", e);
        }
    }

    /**
     * Validate PIN against stored hash
     */
    public PinValidationResult validatePin(Long operatorId, String pin) {
        String key = PIN_KEY_PREFIX + operatorId;

        try {
            Object data = redisTemplate.opsForValue().get(key);

            if (data == null) {
                log.warn("No PIN found for operator {} - may be expired or not generated", operatorId);
                return PinValidationResult.expired();
            }

            PinData pinData = objectMapper.convertValue(data, PinData.class);

            // Check if max attempts exceeded
            if (pinData.getAttempts() >= maxPinAttempts) {
                log.warn("Max PIN attempts exceeded for operator {}", operatorId);
                deletePinData(operatorId);
                return PinValidationResult.maxAttemptsExceeded();
            }

            // Validate PIN
            boolean isValid = passwordEncoder.matches(pin, pinData.getPinHash());

            if (isValid) {
                log.info("PIN validated successfully for operator {}", operatorId);
                resetPinAttempts(operatorId);
                return PinValidationResult.valid();
            } else {
                log.warn("Invalid PIN attempt for operator {} - attempt {}",
                        operatorId, pinData.getAttempts() + 1);
                incrementPinAttempts(operatorId, pinData);

                int remaining = maxPinAttempts - (pinData.getAttempts() + 1);
                return PinValidationResult.invalid(remaining);
            }

        } catch (Exception e) {
            log.error("Error validating PIN for operator {}", operatorId, e);
            throw new RuntimeException("Failed to validate PIN", e);
        }
    }

    /**
     * Delete PIN data from Redis
     */
    public void deletePinData(Long operatorId) {
        String key = PIN_KEY_PREFIX + operatorId;
        redisTemplate.delete(key);
        log.info("PIN data deleted for operator {}", operatorId);
    }

    /**
     * Reset PIN attempts counter
     */
    private void resetPinAttempts(Long operatorId) {
        String key = PIN_KEY_PREFIX + operatorId;
        try {
            Object data = redisTemplate.opsForValue().get(key);
            if (data != null) {
                PinData pinData = objectMapper.convertValue(data, PinData.class);
                pinData.setAttempts(0);

                // Maintain existing TTL
                Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
                if (ttl != null && ttl > 0) {
                    redisTemplate.opsForValue().set(key, pinData, ttl, TimeUnit.SECONDS);
                }
            }
        } catch (Exception e) {
            log.error("Error resetting PIN attempts for operator {}", operatorId, e);
        }
    }

    /**
     * Increment PIN attempts counter
     */
    private void incrementPinAttempts(Long operatorId, PinData pinData) {
        String key = PIN_KEY_PREFIX + operatorId;
        try {
            pinData.setAttempts(pinData.getAttempts() + 1);

            // Maintain existing TTL
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (ttl != null && ttl > 0) {
                redisTemplate.opsForValue().set(key, pinData, ttl, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("Error incrementing PIN attempts for operator {}", operatorId, e);
        }
    }

    /**
     * Check if PIN exists and is valid
     */
    public boolean pinExists(Long operatorId) {
        String key = PIN_KEY_PREFIX + operatorId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // ========================================
    // Inner Classes
    // ========================================

    @Data
    public static class PinData {
        private String pinHash;
        private int attempts;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
    }

    @Data
    public static class PinValidationResult {
        private boolean valid;
        private boolean expired;
        private boolean maxAttemptsExceeded;
        private Integer attemptsRemaining;

        public static PinValidationResult valid() {
            PinValidationResult result = new PinValidationResult();
            result.setValid(true);
            return result;
        }

        public static PinValidationResult invalid(int attemptsRemaining) {
            PinValidationResult result = new PinValidationResult();
            result.setValid(false);
            result.setAttemptsRemaining(attemptsRemaining);
            return result;
        }

        public static PinValidationResult expired() {
            PinValidationResult result = new PinValidationResult();
            result.setValid(false);
            result.setExpired(true);
            return result;
        }

        public static PinValidationResult maxAttemptsExceeded() {
            PinValidationResult result = new PinValidationResult();
            result.setValid(false);
            result.setMaxAttemptsExceeded(true);
            return result;
        }
    }
}
