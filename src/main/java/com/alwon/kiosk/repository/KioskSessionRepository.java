package com.alwon.kiosk.repository;

import com.alwon.kiosk.model.KioskSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KioskSessionRepository extends JpaRepository<KioskSession, Long> {
    Optional<KioskSession> findBySessionId(String sessionId);
}
