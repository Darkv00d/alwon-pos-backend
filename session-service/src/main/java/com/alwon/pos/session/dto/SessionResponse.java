package com.alwon.pos.session.dto;

import com.alwon.pos.session.model.ClientType;
import com.alwon.pos.session.model.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {
    private Long id;
    private String sessionId;
    private ClientType clientType;
    private String customerId;
    private String customerName;
    private String customerPhotoUrl;
    private SessionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
