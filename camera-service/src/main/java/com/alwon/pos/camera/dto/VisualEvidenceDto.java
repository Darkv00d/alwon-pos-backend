package com.alwon.pos.camera.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisualEvidenceDto {
    private Long id;
    private String sessionId;
    private String evidenceType;
    private Long productId;
    private String fileUrl;
    private Long fileSizeBytes;
    private String mimeType;
    private Integer durationSeconds;
    private Float confidenceScore;
    private String faceId;
    private String customerId;
    private String metadata;
    private LocalDateTime capturedAt;
}
