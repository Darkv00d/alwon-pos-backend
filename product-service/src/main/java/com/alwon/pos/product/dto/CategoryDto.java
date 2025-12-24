package com.alwon.pos.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private Integer productCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
