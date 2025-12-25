package com.alwon.pos.auth.service;

import com.alwon.pos.auth.dto.LoginRequest;
import com.alwon.pos.auth.dto.LoginResponse;
import com.alwon.pos.auth.model.Operator;
import com.alwon.pos.auth.repository.OperatorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final OperatorRepository operatorRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());

        // Find operator
        Operator operator = operatorRepository.findByUsernameAndActiveTrue(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Check if account is locked (3+ failed attempts in last hour)
        if (operator.getFailedLoginAttempts() >= 3 &&
                operator.getLastFailedLogin() != null &&
                operator.getLastFailedLogin().isAfter(LocalDateTime.now().minusHours(1))) {
            throw new RuntimeException("Account temporarily locked. Try again later.");
        }

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), operator.getPasswordHash())) {
            // Increment failed attempts
            operator.setFailedLoginAttempts(operator.getFailedLoginAttempts() + 1);
            operator.setLastFailedLogin(LocalDateTime.now());
            operatorRepository.save(operator);

            log.warn("Failed login attempt for user: {}", request.getUsername());
            throw new RuntimeException("Invalid credentials");
        }

        // Reset failed attempts on successful login
        operator.setFailedLoginAttempts(0);
        operator.setLastFailedLogin(null);
        operatorRepository.save(operator);

        // Generate JWT token
        String token = jwtService.generateToken(
                operator.getId(),
                operator.getUsername(),
                operator.getRole().name());

        // Build response
        LoginResponse.OperatorInfo operatorInfo = new LoginResponse.OperatorInfo(
                operator.getId(),
                operator.getUsername(),
                operator.getName(),
                operator.getRole().name(),
                operator.getVerificationCode());

        log.info("Login successful for user: {}", request.getUsername());

        return new LoginResponse(true, token, operatorInfo, 28800L);
    }

    public boolean verifyCode(Long operatorId, String code) {
        Operator operator = operatorRepository.findById(operatorId)
                .orElseThrow(() -> new RuntimeException("Operator not found"));

        return operator.getVerificationCode().equals(code);
    }
}
