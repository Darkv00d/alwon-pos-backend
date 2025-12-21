package com.alwon.kiosk.repository;

import com.alwon.kiosk.model.CartItem;
import com.alwon.kiosk.model.KioskSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findBySession(KioskSession session);

    Optional<CartItem> findBySessionAndProductId(KioskSession session, Long productId);
}
