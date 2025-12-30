package com.alwon.pos.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationStatus {
    private boolean sent;
    private String maskedPhone;
    private String maskedEmail;
}
