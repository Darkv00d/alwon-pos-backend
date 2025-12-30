package com.alwon.pos.auth.controller;

import com.alwon.pos.auth.dto.*;
import com.alwon.pos.auth.service.AuthService;
import com.alwon.pos.auth.service.JwtService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller with PIN-based 2-level authentication
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Operator authentication endpoints with PIN-based 2-level security")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    @RateLimiter(name = "auth")
    @Operation(summary = "Operator login with credential validation", description = "Validates operator credentials against Central System, generates a 6-digit PIN, "
            +
            "stores it in Redis with 8-hour TTL, and sends notifications via WhatsApp and Email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful - PIN generated and sent", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class), examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "operator": {
                        "id": 1,
                        "username": "carlos.martinez",
                        "fullName": "Carlos Martínez",
                        "email": "carlos@alwon.com",
                        "phone": "+57 300 123 4567",
                        "role": "OPERATOR"
                      },
                      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                      "pin": "472915",
                      "pinExpiresAt": "2025-12-25T20:30:00",
                      "notifications": {
                        "sent": true,
                        "maskedPhone": "***-***-4567",
                        "maskedEmail": "c***@alwon.com"
                      }
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "error": "INVALID_CREDENTIALS",
                      "message": "Usuario o contraseña incorrectos"
                    }
                    """))),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded (5 attempts per minute)")
    })
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody @Parameter(description = "Login credentials") LoginRequest request,
            HttpServletRequest httpRequest) {
        try {
            String ipAddress = httpRequest.getRemoteAddr();
            String userAgent = httpRequest.getHeader("User-Agent");

            log.info("Login request for username: {} from IP: {}", request.getUsername(), ipAddress);

            LoginResponse response = authService.login(request, ipAddress, userAgent);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/validate-pin")
    @Operation(summary = "Validate operator PIN", description = "Validates the 6-digit PIN entered by the operator. Maximum 3 attempts allowed. "
            +
            "PIN is compared against BCrypt hash stored in Redis.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PIN validation result", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidatePinResponse.class), examples = {
                    @ExampleObject(name = "Valid PIN", value = """
                            {
                              "success": true,
                              "valid": true,
                              "operator": {
                                "id": 1,
                                "username": "carlos.martinez",
                                "fullName": "Carlos Martínez"
                              }
                            }
                            """),
                    @ExampleObject(name = "Invalid PIN (attempts remaining)", value = """
                            {
                              "success": false,
                              "valid": false,
                              "attemptsRemaining": 2,
                              "message": "Invalid PIN. 2 attempts remaining."
                            }
                            """),
                    @ExampleObject(name = "Max attempts exceeded", value = """
                            {
                              "success": false,
                              "valid": false,
                              "requiresLogin": true,
                              "message": "Maximum PIN attempts exceeded. Please login again."
                            }
                            """)
            })),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired token")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ValidatePinResponse> validatePin(
            @Valid @RequestBody @Parameter(description = "PIN to validate") ValidatePinRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long operatorId = extractOperatorIdFromToken(authHeader);
            ValidatePinResponse response = authService.validatePin(operatorId, request.getPin());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("PIN validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout operator", description = "Logs out the operator by deleting PIN from Redis and revoking JWT session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long operatorId = extractOperatorIdFromToken(authHeader);
            authService.logout(operatorId);

            log.info("Operator {} logged out successfully", operatorId);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Logout error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/session")
    @Operation(summary = "Check session status", description = "Verifies if the operator's session is still active and PIN is valid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session is active", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "active": true,
                      "operatorId": 1
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Session expired or invalid")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Boolean> checkSession(
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long operatorId = extractOperatorIdFromToken(authHeader);
            boolean isActive = authService.checkSession(operatorId);

            return ResponseEntity.ok(isActive);

        } catch (Exception e) {
            log.error("Session check error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }

    /**
     * Extract operator ID from JWT token in Authorization header
     */
    private Long extractOperatorIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        return jwtService.extractOperatorId(token);
    }
}
