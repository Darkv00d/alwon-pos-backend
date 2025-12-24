package com.alwon.pos.session.repository;

import com.alwon.pos.session.model.CustomerSession;
import com.alwon.pos.session.model.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerSessionRepository extends JpaRepository<CustomerSession, Long> {

    Optional<CustomerSession> findBySessionId(String sessionId);

    List<CustomerSession> findByStatus(SessionStatus status);

    List<CustomerSession> findByStatusOrderByCreatedAtDesc(SessionStatus status);
}
