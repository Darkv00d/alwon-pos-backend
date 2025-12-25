package com.alwon.pos.external.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseRequest {
    private String sessionId;
    private List<PurchaseItem> items;

    @Data
    public static class PurchaseItem {
        private String productSku;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private String imageUrl;
        private Double detectionConfidence; // 0.0 - 1.0
        private Boolean requiresReview;
        private LocalDateTime timestamp;
    }
}
