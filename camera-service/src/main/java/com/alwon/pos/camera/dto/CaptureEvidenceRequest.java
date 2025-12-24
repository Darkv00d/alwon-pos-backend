package com.alwon.pos.camera.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptureEvidenceRequest {

    @NotBlank(message = "Session ID is required")
    @Size(max = 100, message = "Session ID must be less than 100 characters")
    private String sessionId;

    @NotBlank(message = "Evidence type is required")
    private String evidenceType; // FACIAL_PHOTO, PRODUCT_VIDEO, PRODUCT_GIF, etc.

    @NotNull(message = "File data is required")
    private String fileData; // Base64 encoded file

    private Long productId;

    private String mimeType;

    private Integer durationSeconds;

    private String metadata;
}
