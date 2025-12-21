package com.alwon.kiosk.controller;

import com.alwon.kiosk.dto.CartModificationRequest;
import com.alwon.kiosk.dto.CustomerSessionRequest;
import com.alwon.kiosk.dto.CustomerSessionResponse;
import com.alwon.kiosk.service.KioskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/kiosk")
@CrossOrigin(origins = "*")
public class KioskController {

    @Autowired
    private KioskService kioskService;

    @Value("${kiosk.staff.pin:1234}")
    private String staffPin;

    @PostMapping("/customer-session")
    public ResponseEntity<?> createCustomerSession(@Valid @RequestBody CustomerSessionRequest request) {
        try {
            CustomerSessionResponse response = kioskService.createSession(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> getSession(@PathVariable String sessionId) {
        try {
            CustomerSessionResponse response = kioskService.getSession(sessionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(404).body(error);
        }
    }

    @PatchMapping("/session/{sessionId}/cart")
    public ResponseEntity<?> modifyCart(
            @PathVariable String sessionId,
            @Valid @RequestBody CartModificationRequest request,
            @RequestHeader(value = "X-Staff-Pin", required = false) String pin) {
        try {
            // Validate PIN
            if (pin == null || !pin.equals(staffPin)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid or missing staff PIN");
                return ResponseEntity.status(401).body(error);
            }

            CustomerSessionResponse response = kioskService.modifyCart(sessionId, request, pin);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/session/{sessionId}/payment")
    public ResponseEntity<?> processPayment(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> paymentRequest) {
        // Mock payment processing for now
        Map<String, Object> response = new HashMap<>();
        response.put("transactionId", "TRX-" + System.currentTimeMillis());
        response.put("status", "pending");
        response.put("paymentUrl", "https://bold.co/pay/mock123");
        return ResponseEntity.ok(response);
    }
}
