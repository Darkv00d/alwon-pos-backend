package com.alwon.pos.websocket.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/session.update")
    @SendTo("/topic/sessions")
    public Map<String, Object> handleSessionUpdate(Map<String, Object> message) {
        log.info("Session update received: {}", message);
        return message;
    }

    @MessageMapping("/cart.update")
    @SendTo("/topic/carts")
    public Map<String, Object> handleCartUpdate(Map<String, Object> message) {
        log.info("Cart update received: {}", message);
        return message;
    }

    @MessageMapping("/payment.update")
    @SendTo("/topic/payments")
    public Map<String, Object> handlePaymentUpdate(Map<String, Object> message) {
        log.info("Payment update received: {}", message);
        return message;
    }

    public void sendSessionNotification(String sessionId, Map<String, Object> data) {
        messagingTemplate.convertAndSend("/topic/sessions/" + sessionId, data);
    }
}
