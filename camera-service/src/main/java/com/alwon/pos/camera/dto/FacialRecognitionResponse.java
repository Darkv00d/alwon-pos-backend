package com.alwon.pos.camera.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacialRecognitionResponse {
    private boolean success;
    private boolean faceDetected;
    private String faceId;
    private String customerId;
    private String customerName;
    private Float confidenceScore;
    private String message;
    private String evidenceUrl;
}
