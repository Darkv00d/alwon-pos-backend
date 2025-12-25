package com.alwon.pos.cart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items", schema = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", referencedColumnName = "cart_id", nullable = false)
    private ShoppingCart cart;

    @Column(name = "product_sku", nullable = false)
    private String productSku;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;

    @Column(name = "detection_confidence")
    private BigDecimal detectionConfidence;

    @Column(name = "requires_review")
    private Boolean requiresReview = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateSubtotal();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateSubtotal();
    }

    private void calculateSubtotal() {
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // Getter for backward compatibility with ShoppingCart.recalculateTotals()
    public BigDecimal getTotalPrice() {
        return this.subtotal;
    }
}
