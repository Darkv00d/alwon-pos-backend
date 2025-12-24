package com.alwon.pos.inventory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    public Map<String, Object> getProductStock(Long productId) {
        log.debug("Getting stock for product: {}", productId);
        Map<String, Object> response = new HashMap<>();
        response.put("productId", productId);
        response.put("availableQuantity", 100);
        response.put("reservedQuantity", 5);
        response.put("totalQuantity", 105);
        return response;
    }

    public Map<String, Object> adjustStock(Long productId, Integer quantity, String reason) {
        log.info("Adjusting stock for product {} by {} units. Reason: {}", productId, quantity, reason);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("productId", productId);
        response.put("adjustment", quantity);
        response.put("newQuantity", 100 + quantity);
        return response;
    }
}
