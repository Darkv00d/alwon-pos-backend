package com.alwon.pos.auth.repository;

import com.alwon.pos.auth.model.OperatorSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OperatorSessionRepository extends JpaRepository<OperatorSession, Long> {

    Optional<OperatorSession> findByTokenJtiAndRevokedFalse(String tokenJti);

    void deleteByExpiresAtBefore(LocalDateTime now);

    void deleteByOperatorIdAndRevokedFalse(Long operatorId);
}
