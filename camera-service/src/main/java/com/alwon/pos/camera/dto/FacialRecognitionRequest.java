package com.alwon.pos.camera.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacialRecognitionRequest {

    @NotBlank(message = "Session ID is required")
    @Size(max = 100, message = "Session ID must be less than 100 characters")
    private String sessionId;

    @NotBlank(message = "Image data is required")
    private String imageData; // Base64 encoded image

    @Pattern(regexp = "image/(jpeg|png|jpg)", message = "Only JPEG and PNG images are supported")
    private String mimeType = "image/jpeg";

    private String metadata; // Optional JSON metadata
}
