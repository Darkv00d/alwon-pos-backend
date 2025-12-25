package com.alwon.pos.external.service;

import com.alwon.pos.external.dto.CustomerRequest;
import com.alwon.pos.external.dto.PurchaseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.session}")
    private String sessionServiceUrl;

    @Value("${services.cart}")
    private String cartServiceUrl;

    @Value("${services.product}")
    private String productServiceUrl;

    /**
     * Procesar cliente identificado del Concentrador
     * 1. Crear/actualizar sesión en Session Service
     * 2. Retornar sessionId
     */
    public String processCustomer(CustomerRequest request) {
        log.info("Processing customer: {} - Type: {}", request.getName(), request.getIdentificationType());

        // Preparar request para Session Service
        Map<String, Object> sessionRequest = new HashMap<>();
        sessionRequest.put("clientType", request.getIdentificationType());
        sessionRequest.put("customerName", request.getName());
        sessionRequest.put("customerPhoto", request.getPhoto());
        sessionRequest.put("tower", request.getTower());
        sessionRequest.put("apartment", request.getApartment());
        sessionRequest.put("phone", request.getPhone());
        sessionRequest.put("email", request.getEmail());

        // Llamar a Session Service para crear sesión
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(sessionRequest, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    sessionServiceUrl + "/sessions",
                    entity,
                    Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String sessionId = (String) response.getBody().get("sessionId");
                log.info("Session created successfully: {}", sessionId);
                return sessionId;
            } else {
                throw new RuntimeException("Failed to create session");
            }

        } catch (Exception e) {
            log.error("Error calling Session Service: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating session: " + e.getMessage());
        }
    }

    /**
     * Procesar productos detectados por IA
     * 1. Validar que sesión existe
     * 2. Para cada producto:
     * - Añadir al carrito via Cart Service
     * 3. Retornar resumen
     */
    public Map<String, Object> processPurchase(PurchaseRequest request) {
        log.info("Processing purchase for session: {}", request.getSessionId());

        int itemsAdded = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (PurchaseRequest.PurchaseItem item : request.getItems()) {
            try {
                // Preparar request para Cart Service
                Map<String, Object> cartItemRequest = new HashMap<>();
                cartItemRequest.put("sessionId", request.getSessionId());
                cartItemRequest.put("productSku", item.getProductSku());
                cartItemRequest.put("productName", item.getProductName());
                cartItemRequest.put("quantity", item.getQuantity());
                cartItemRequest.put("unitPrice", item.getUnitPrice());
                cartItemRequest.put("detectionConfidence", item.getDetectionConfidence());
                cartItemRequest.put("requiresReview", item.getRequiresReview());

                // Llamar a Cart Service para añadir item
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(cartItemRequest, headers);

                ResponseEntity<Map> response = restTemplate.postForEntity(
                        cartServiceUrl + "/carts/" + request.getSessionId() + "/items",
                        entity,
                        Map.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    itemsAdded++;
                    BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    totalAmount = totalAmount.add(subtotal);

                    log.info("Added item to cart: {} x{} = ${}",
                            item.getProductName(), item.getQuantity(), subtotal);
                } else {
                    log.warn("Failed to add item: {}", item.getProductSku());
                }

            } catch (Exception e) {
                log.error("Error adding item to cart: {}", e.getMessage(), e);
                // Continuar con siguiente item
            }
        }

        // Retornar resumen
        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", request.getSessionId());
        result.put("itemsAdded", itemsAdded);
        result.put("totalItems", request.getItems().size());
        result.put("totalAmount", totalAmount);
        result.put("success", itemsAdded > 0);

        log.info("Purchase processed: {}/{} items added, total: ${}",
                itemsAdded, request.getItems().size(), totalAmount);

        return result;
    }
}
