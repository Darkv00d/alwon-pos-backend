package com.alwon.pos.session.dto;

import com.alwon.pos.session.model.ClientType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSessionRequest {

    @NotNull(message = "Client type is required")
    private ClientType clientType;

    private String customerId;
    private String customerName;
    private String customerPhotoUrl;
}
