package com.alwon.pos.product.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    @Size(max = 200, message = "Name must be less than 200 characters")
    private String name;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    private Long categoryId;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @Min(value = 0, message = "Minimum stock cannot be negative")
    private Integer minStock;

    @Size(max = 500, message = "Image URL must be less than 500 characters")
    private String imageUrl;

    private Boolean active;

    private Boolean taxable;

    @DecimalMin(value = "0.0", message = "Tax rate cannot be negative")
    @DecimalMax(value = "100.0", message = "Tax rate cannot exceed 100%")
    private BigDecimal taxRate;

    @Size(max = 50, message = "Barcode must be less than 50 characters")
    private String barcode;

    @Size(max = 50, message = "Brand must be less than 50 characters")
    private String brand;

    @Size(max = 50, message = "Unit must be less than 50 characters")
    private String unit;
}
