package com.alwon.pos.external.controller;

import com.alwon.pos.external.dto.ApiResponse;
import com.alwon.pos.external.dto.CustomerRequest;
import com.alwon.pos.external.dto.PurchaseRequest;
import com.alwon.pos.external.service.ExternalApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/external")
@RequiredArgsConstructor
public class ExternalApiController {

    private final ExternalApiService externalApiService;

    /**
     * CAPA 0 - Endpoint 1: Recibir información de cliente identificado
     * Llamado por Sistema Concentrador cuando detecta un cliente
     */
    @PostMapping("/customer")
    public ResponseEntity<ApiResponse<Map<String, String>>> receiveCustomer(
            @Valid @RequestBody CustomerRequest request) {

        log.info("Received customer from Concentrador: {}", request.getName());

        try {
            String sessionId = externalApiService.processCustomer(request);

            Map<String, String> data = Map.of(
                    "sessionId", sessionId,
                    "customerName", request.getName(),
                    "identificationType", request.getIdentificationType());

            return ResponseEntity.ok(
                    ApiResponse.success("Cliente registrado correctamente", data));

        } catch (Exception e) {
            log.error("Error processing customer: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al procesar cliente: " + e.getMessage()));
        }
    }

    /**
     * CAPA 0 - Endpoint 2: Recibir productos detectados por IA
     * Llamado por Sistema Concentrador cuando detecta que cliente tomó productos
     */
    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<Map<String, Object>>> receivePurchase(
            @Valid @RequestBody PurchaseRequest request) {

        log.info("Received purchase from Concentrador for session: {}", request.getSessionId());
        log.info("Items detected: {}", request.getItems().size());

        try {
            Map<String, Object> result = externalApiService.processPurchase(request);

            return ResponseEntity.ok(
                    ApiResponse.success("Productos agregados al carrito", result));

        } catch (Exception e) {
            log.error("Error processing purchase: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al procesar compra: " + e.getMessage()));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(
                ApiResponse.success("External API Service is running", "OK"));
    }
}
