package com.alwon.pos.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@SpringBootApplication
public class InventoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}

@RestController
@RequestMapping("/inventory")
class InventoryController {
    @PostMapping("/return")
    Map<String, String> returnProducts(@RequestBody Map<String, Object> request) {
        return Map.of("status", "OK", "message", "Products returned to inventory");
    }

    @GetMapping("/stock/{productId}")
    Map<String, Object> getStock(@PathVariable Long productId) {
        return Map.of("productId", productId, "available", 100, "reserved", 5);
    }
}
