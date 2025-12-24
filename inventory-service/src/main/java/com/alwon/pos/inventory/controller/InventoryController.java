package com.alwon.pos.inventory.controller;

import com.alwon.pos.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/stock/{productId}")
    @Operation(summary = "Get product stock")
    public ResponseEntity<Map<String, Object>> getStock(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getProductStock(productId));
    }

    @PostMapping("/stock/{productId}/adjust")
    @Operation(summary = "Adjust product stock")
    public ResponseEntity<Map<String, Object>> adjustStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(inventoryService.adjustStock(productId, quantity, reason));
    }
}
