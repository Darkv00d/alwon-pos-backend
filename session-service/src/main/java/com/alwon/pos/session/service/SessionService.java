package com.alwon.pos.session.service;

import com.alwon.pos.session.dto.CreateSessionRequest;
import com.alwon.pos.session.dto.SessionResponse;
import com.alwon.pos.session.model.CustomerSession;
import com.alwon.pos.session.model.CustomerSession.SessionStatus;
import com.alwon.pos.session.repository.CustomerSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {

    private final CustomerSessionRepository sessionRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public SessionResponse createSession(CreateSessionRequest request) {
        CustomerSession session = new CustomerSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setClientType(request.getClientType());
        session.setCustomerId(request.getCustomerId());
        session.setCustomerName(request.getCustomerName());
        session.setCustomerPhotoUrl(request.getCustomerPhotoUrl());
        session.setStatus(SessionStatus.ACTIVE);

        CustomerSession saved = sessionRepository.save(session);
        log.info("Created new session: {} for client type: {}", saved.getSessionId(), saved.getClientType());

        // Publish event to RabbitMQ
        publishSessionEvent("session.created", saved);

        return mapToResponse(saved);
    }

    public List<SessionResponse> getActiveSessions() {
        return sessionRepository.findByStatusOrderByCreatedAtDesc(SessionStatus.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public SessionResponse getSessionById(String sessionId) {
        CustomerSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
        return mapToResponse(session);
    }

    @Transactional
    public SessionResponse closeSession(String sessionId) {
        CustomerSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        session.setStatus(SessionStatus.CLOSED);
        session.setClosedAt(LocalDateTime.now());
        CustomerSession updated = sessionRepository.save(session);

        log.info("Closed session: {}", sessionId);
        publishSessionEvent("session.closed", updated);

        return mapToResponse(updated);
    }

    @Transactional
    public SessionResponse suspendSession(String sessionId) {
        CustomerSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        session.setStatus(SessionStatus.SUSPENDED);
        CustomerSession updated = sessionRepository.save(session);

        log.info("Suspended session: {}", sessionId);
        publishSessionEvent("session.suspended", updated);

        return mapToResponse(updated);
    }

    private void publishSessionEvent(String eventType, CustomerSession session) {
        try {
            rabbitTemplate.convertAndSend("alwon.events", eventType, mapToResponse(session));
        } catch (Exception e) {
            log.error("Failed to publish session event: {}", eventType, e);
        }
    }

    private SessionResponse mapToResponse(CustomerSession session) {
        return new SessionResponse(
                session.getId(),
                session.getSessionId(),
                session.getClientType(),
                session.getCustomerId(),
                session.getCustomerName(),
                session.getCustomerPhotoUrl(),
                session.getStatus(),
                session.getCreatedAt(),
                session.getUpdatedAt());
    }
}
