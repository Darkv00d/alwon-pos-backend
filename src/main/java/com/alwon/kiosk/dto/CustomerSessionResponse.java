package com.alwon.kiosk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class CustomerSessionResponse {
    private String sessionId;
    private String status;
    private LocalDateTime expiresAt;
    private CustomerData customer;
    private CartData cart;

    @Data
    @AllArgsConstructor
    public static class CustomerData {
        private String name;
        private String tower;
        private String apartment;
        private String photoUrl;
    }

    @Data
    @AllArgsConstructor
    public static class CartData {
        private List<CartItemData> items;
        private java.math.BigDecimal subtotal;
        private java.math.BigDecimal tax;
        private java.math.BigDecimal total;
    }

    @Data
    @AllArgsConstructor
    public static class CartItemData {
        private Long productId;
        private String productName;
        private Integer quantity;
        private java.math.BigDecimal unitPrice;
        private String imageUrl;
    }
}
