package com.alwon.pos.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Client for validating operator credentials against Central System
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CentralSystemClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${central.system.url}")
    private String centralSystemUrl;

    @Value("${central.system.api.key:}")
    private String apiKey;

    @Value("${central.system.enabled:false}")
    private boolean enabled;

    /**
     * Validate operator credentials against Central System (Video Portero)
     */
    public boolean validateOperatorCredentials(String username, String password) {
        if (!enabled) {
            log.warn("Central System validation is DISABLED - allowing login for development");
            return true; // Dev mode - always allow
        }

        try {
            String url = centralSystemUrl + "/operators/validate";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (apiKey != null && !apiKey.isEmpty()) {
                headers.set("X-API-Key", apiKey);
            }

            Map<String, String> request = Map.of(
                    "username", username,
                    "password", password);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> body = response.getBody();
                Boolean valid = body != null ? (Boolean) body.get("valid") : false;
                log.info("Central System validation for {}: {}", username, valid);
                return Boolean.TRUE.equals(valid);
            }

            log.warn("Central System returned non-200 status: {}", response.getStatusCode());
            return false;

        } catch (Exception e) {
            log.error("Error validating credentials with Central System for {}: {}",
                    username, e.getMessage());
            // En producción, podrías querer fallar cerrado (return false)
            // Para desarrollo, permitimos el login si el sistema central falla
            return !enabled;
        }
    }
}
