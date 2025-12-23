package com.alwon.pos.cart.controller;

import com.alwon.pos.cart.dto.AddItemRequest;
import com.alwon.pos.cart.dto.CartResponse;
import com.alwon.pos.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart management API")
public class CartController {

    private final CartService cartService;

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get cart by session ID")
    public ResponseEntity<CartResponse> getCart(@PathVariable String sessionId) {
        CartResponse cart = cartService.getOrCreateCart(sessionId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/{sessionId}/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<CartResponse> addItem(
            @PathVariable String sessionId,
            @Valid @RequestBody AddItemRequest request) {
        CartResponse cart = cartService.addItem(sessionId, request);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{sessionId}/items/{itemId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable String sessionId,
            @PathVariable Long itemId) {
        CartResponse cart = cartService.removeItem(sessionId, itemId);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/{sessionId}/items/{itemId}/quantity")
    @Operation(summary = "Update item quantity")
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable String sessionId,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        CartResponse cart = cartService.updateItemQuantity(sessionId, itemId, quantity);
        return ResponseEntity.ok(cart);
    }
}
