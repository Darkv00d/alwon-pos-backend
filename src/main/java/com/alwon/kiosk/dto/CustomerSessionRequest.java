package com.alwon.kiosk.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CustomerSessionRequest {

    @NotNull(message = "Customer ID is required")
    private String customerId;

    @Valid
    @NotNull(message = "Biometric data is required")
    private BiometricData biometricData;

    @Valid
    @NotNull(message = "Customer info is required")
    private CustomerInfo customerInfo;

    @NotEmpty(message = "Cart must have at least one item")
    private List<CartItemRequest> cart;

    @Data
    public static class BiometricData {
        private String faceId;
        private Double confidence;
        private String timestamp;
    }

    @Data
    public static class CustomerInfo {
        @NotNull(message = "Name is required")
        private String name;
        private String photo;
        private String tower;
        private String apartment;
    }

    @Data
    public static class CartItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Product name is required")
        private String productName;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        @NotNull(message = "Unit price is required")
        private java.math.BigDecimal unitPrice;

        private String imageUrl;
    }
}
