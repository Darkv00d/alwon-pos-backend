package com.alwon.pos.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddItemRequest {
    @NotNull
    private Long productId;

    @NotNull
    private String productName;

    private String productImageUrl;

    @NotNull
    @Positive
    private Integer quantity = 1;

    @NotNull
    @Positive
    private BigDecimal unitPrice;

    private String operatorPassword; // Required for manual modifications
}
