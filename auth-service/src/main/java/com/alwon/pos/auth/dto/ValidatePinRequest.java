package com.alwon.pos.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ValidatePinRequest {

    @NotBlank(message = "PIN es requerido")
    @Size(min = 6, max = 6, message = "PIN debe tener exactamente 6 dígitos")
    private String pin;
}
