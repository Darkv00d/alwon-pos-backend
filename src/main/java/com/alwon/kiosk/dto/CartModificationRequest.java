package com.alwon.kiosk.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class CartModificationRequest {

    @NotEmpty(message = "Modifications list cannot be empty")
    @Valid
    private List<Modification> modifications;

    private String reason;

    @Data
    public static class Modification {
        @NotEmpty(message = "Action is required")
        private String action; // add, remove, update_quantity

        @javax.validation.constraints.NotNull(message = "Product ID is required")
        private Long productId;

        private Integer newQuantity; // for update_quantity action
        private Integer quantity; // for add action
    }
}
