package com.alwon.pos.external.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CustomerRequest {
    private String customerId;
    private String name;
    private String photo; // Base64 encoded
    private String tower;
    private String apartment;
    private String phone;
    private String email;
    private String identificationType; // FACIAL, PIN, NO_ID
    private LocalDateTime timestamp;
}
