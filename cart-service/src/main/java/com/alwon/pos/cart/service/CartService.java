package com.alwon.pos.cart.service;

import com.alwon.pos.cart.dto.AddItemRequest;
import com.alwon.pos.cart.dto.CartResponse;
import com.alwon.pos.cart.model.CartItem;
import com.alwon.pos.cart.model.ShoppingCart;
import com.alwon.pos.cart.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final ShoppingCartRepository cartRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public CartResponse getOrCreateCart(String sessionId) {
        ShoppingCart cart = cartRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setCartId("cart-" + UUID.randomUUID().toString());
                    newCart.setSessionId(sessionId);
                    return cartRepository.save(newCart);
                });
        return mapToResponse(cart);
    }

    @Transactional
    public CartResponse addItem(String sessionId, AddItemRequest request) {
        ShoppingCart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Cart not found for session: " + sessionId));

        // Check if item already exists
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProductSku().equals(request.getProductSku()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductSku(request.getProductSku());
            newItem.setProductName(request.getProductName());
            newItem.setQuantity(request.getQuantity());
            newItem.setUnitPrice(request.getUnitPrice());
            cart.getItems().add(newItem);
        }

        cart.recalculateTotals();
        ShoppingCart saved = cartRepository.save(cart);

        log.info("Added item to cart. Session: {}, Product: {}", sessionId, request.getProductSku());
        publishCartEvent("cart.updated", saved);

        return mapToResponse(saved);
    }

    @Transactional
    public CartResponse removeItem(String sessionId, Long itemId) {
        ShoppingCart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        cart.recalculateTotals();
        ShoppingCart saved = cartRepository.save(cart);

        log.info("Removed item from cart. Session: {}, ItemId: {}", sessionId, itemId);
        publishCartEvent("cart.updated", saved);

        return mapToResponse(saved);
    }

    @Transactional
    public CartResponse updateItemQuantity(String sessionId, Long itemId, Integer quantity) {
        ShoppingCart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setQuantity(quantity);
        cart.recalculateTotals();
        ShoppingCart saved = cartRepository.save(cart);

        log.info("Updated item quantity. Session: {}, ItemId: {}, Quantity: {}", sessionId, itemId, quantity);
        publishCartEvent("cart.updated", saved);

        return mapToResponse(saved);
    }

    private void publishCartEvent(String eventType, ShoppingCart cart) {
        try {
            rabbitTemplate.convertAndSend("alwon.events", eventType, mapToResponse(cart));
        } catch (Exception e) {
            log.error("Failed to publish cart event: {}", eventType, e);
        }
    }

    private CartResponse mapToResponse(ShoppingCart cart) {
        List<CartResponse.CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> new CartResponse.CartItemResponse(
                        item.getId(),
                        item.getProductSku(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()))
                .collect(Collectors.toList());

        return new CartResponse(
                cart.getId(),
                cart.getCartId(),
                cart.getSessionId(),
                cart.getTotalAmount(),
                cart.getItemsCount(),
                cart.getStatus().name(),
                itemResponses,
                cart.getCreatedAt(),
                cart.getUpdatedAt());
    }
}
