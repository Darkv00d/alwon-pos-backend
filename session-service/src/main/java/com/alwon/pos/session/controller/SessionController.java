package com.alwon.pos.session.controller;

import com.alwon.pos.session.dto.CreateSessionRequest;
import com.alwon.pos.session.dto.SessionResponse;
import com.alwon.pos.session.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
@Tag(name = "Sessions", description = "Session management API")
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    @Operation(summary = "Create new customer session")
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody CreateSessionRequest request) {
        SessionResponse response = sessionService.createSession(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active sessions")
    public ResponseEntity<List<SessionResponse>> getActiveSessions() {
        List<SessionResponse> sessions = sessionService.getActiveSessions();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get session by ID")
    public ResponseEntity<SessionResponse> getSession(@PathVariable String sessionId) {
        SessionResponse response = sessionService.getSessionById(sessionId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{sessionId}")
    @Operation(summary = "Close session")
    public ResponseEntity<SessionResponse> closeSession(@PathVariable String sessionId) {
        SessionResponse response = sessionService.closeSession(sessionId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{sessionId}/suspend")
    @Operation(summary = "Suspend session")
    public ResponseEntity<SessionResponse> suspendSession(@PathVariable String sessionId) {
        SessionResponse response = sessionService.suspendSession(sessionId);
        return ResponseEntity.ok(response);
    }
}
