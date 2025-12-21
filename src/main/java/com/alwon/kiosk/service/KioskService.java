package com.alwon.kiosk.service;

import com.alwon.kiosk.dto.CartModificationRequest;
import com.alwon.kiosk.dto.CustomerSessionRequest;
import com.alwon.kiosk.dto.CustomerSessionResponse;
import com.alwon.kiosk.model.CartItem;
import com.alwon.kiosk.model.KioskSession;
import com.alwon.kiosk.model.Product;
import com.alwon.kiosk.repository.CartItemRepository;
import com.alwon.kiosk.repository.KioskSessionRepository;
import com.alwon.kiosk.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class KioskService {

    @Autowired
    private KioskSessionRepository sessionRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Value("${kiosk.session.expiration.minutes:10}")
    private int sessionExpirationMinutes;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public CustomerSessionResponse createSession(CustomerSessionRequest request) {
        // Create session entity
        KioskSession session = new KioskSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setCustomerId(request.getCustomerId());
        session.setCustomerName(request.getCustomerInfo().getName());
        session.setCustomerPhotoUrl(request.getCustomerInfo().getPhoto());
        session.setTower(request.getCustomerInfo().getTower());
        session.setApartment(request.getCustomerInfo().getApartment());
        session.setStatus("active");
        session.setExpiresAt(LocalDateTime.now().plusMinutes(sessionExpirationMinutes));

        // Store biometric data as JSON
        try {
            session.setBiometricDataJson(objectMapper.writeValueAsString(request.getBiometricData()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize biometric data", e);
        }

        session = sessionRepository.save(session);

        // Create cart items
        for (CustomerSessionRequest.CartItemRequest item : request.getCart()) {
            CartItem cartItem = new CartItem();
            cartItem.setSession(session);
            cartItem.setProductId(item.getProductId());
            cartItem.setProductName(item.getProductName());
            cartItem.setQuantity(item.getQuantity());
            cartItem.setUnitPrice(item.getUnitPrice());
            cartItem.setImageUrl(item.getImageUrl());
            cartItem.setAddedBy("system");
            cartItemRepository.save(cartItem);
        }

        return buildSessionResponse(session);
    }

    public CustomerSessionResponse getSession(String sessionId) {
        KioskSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            session.setStatus("expired");
            sessionRepository.save(session);
            throw new RuntimeException("Session has expired");
        }

        return buildSessionResponse(session);
    }

    @Transactional
    public CustomerSessionResponse modifyCart(String sessionId, CartModificationRequest request, String staffPin) {
        // Validate staff PIN (in production, use proper authentication)
        // For now, we'll skip validation in service layer

        KioskSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getStatus().equals("active")) {
            throw new RuntimeException("Session is not active");
        }

        for (CartModificationRequest.Modification mod : request.getModifications()) {
            switch (mod.getAction().toLowerCase()) {
                case "add":
                    addItemToCart(session, mod.getProductId(), mod.getQuantity(), request.getReason());
                    break;
                case "remove":
                    removeItemFromCart(session, mod.getProductId());
                    break;
                case "update_quantity":
                    updateItemQuantity(session, mod.getProductId(), mod.getNewQuantity());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown action: " + mod.getAction());
            }
        }

        return buildSessionResponse(session);
    }

    private void addItemToCart(KioskSession session, Long productId, Integer quantity, String reason) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem item = new CartItem();
        item.setSession(session);
        item.setProductId(productId);
        item.setProductName(product.getName());
        item.setQuantity(quantity);
        item.setUnitPrice(product.getPrice());
        item.setImageUrl(product.getImageUrl());
        item.setAddedBy("staff");
        item.setModificationReason(reason);
        cartItemRepository.save(item);
    }

    private void removeItemFromCart(KioskSession session, Long productId) {
        CartItem item = cartItemRepository.findBySessionAndProductId(session, productId)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));
        cartItemRepository.delete(item);
    }

    private void updateItemQuantity(KioskSession session, Long productId, Integer newQuantity) {
        CartItem item = cartItemRepository.findBySessionAndProductId(session, productId)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));
        item.setQuantity(newQuantity);
        cartItemRepository.save(item);
    }

    private CustomerSessionResponse buildSessionResponse(KioskSession session) {
        List<CartItem> cartItems = cartItemRepository.findBySession(session);

        List<CustomerSessionResponse.CartItemData> items = cartItems.stream()
                .map(item -> new CustomerSessionResponse.CartItemData(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getImageUrl()))
                .collect(Collectors.toList());

        BigDecimal subtotal = cartItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.19))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = subtotal.add(tax);

        CustomerSessionResponse.CartData cart = new CustomerSessionResponse.CartData(items, subtotal, tax, total);

        CustomerSessionResponse.CustomerData customer = new CustomerSessionResponse.CustomerData(
                session.getCustomerName(),
                session.getTower(),
                session.getApartment(),
                session.getCustomerPhotoUrl());

        return new CustomerSessionResponse(
                session.getSessionId(),
                session.getStatus(),
                session.getExpiresAt(),
                customer,
                cart);
    }
}
