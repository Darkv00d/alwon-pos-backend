package com.alwon.pos.camera.controller;

import com.alwon.pos.camera.dto.*;
import com.alwon.pos.camera.service.CameraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/camera")
@RequiredArgsConstructor
@Tag(name = "Camera", description = "Camera and facial recognition endpoints")
public class CameraController {

    private final CameraService cameraService;

    @PostMapping("/facial-recognition")
    @Operation(summary = "Perform facial recognition")
    public ResponseEntity<FacialRecognitionResponse> performFacialRecognition(
            @Valid @RequestBody FacialRecognitionRequest request) {
        return ResponseEntity.ok(cameraService.performFacialRecognition(request));
    }

    @PostMapping("/capture")
    @Operation(summary = "Capture visual evidence")
    public ResponseEntity<VisualEvidenceDto> captureEvidence(
            @Valid @RequestBody CaptureEvidenceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cameraService.captureEvidence(request));
    }

    @GetMapping("/evidence/session/{sessionId}")
    @Operation(summary = "Get all evidence for a session")
    public ResponseEntity<List<VisualEvidenceDto>> getSessionEvidence(
            @PathVariable String sessionId) {
        return ResponseEntity.ok(cameraService.getSessionEvidence(sessionId));
    }

    @GetMapping("/evidence/session/{sessionId}/type/{evidenceType}")
    @Operation(summary = "Get session evidence by type")
    public ResponseEntity<List<VisualEvidenceDto>> getSessionEvidenceByType(
            @PathVariable String sessionId,
            @PathVariable String evidenceType) {
        return ResponseEntity.ok(cameraService.getSessionEvidenceByType(sessionId, evidenceType));
    }

    @GetMapping("/evidence/customer/{customerId}")
    @Operation(summary = "Get all evidence for a customer")
    public ResponseEntity<List<VisualEvidenceDto>> getCustomerEvidence(
            @PathVariable String customerId) {
        return ResponseEntity.ok(cameraService.getCustomerEvidence(customerId));
    }

    @GetMapping("/evidence/session/{sessionId}/facial")
    @Operation(summary = "Get latest facial photo for session")
    public ResponseEntity<VisualEvidenceDto> getLatestFacialPhoto(
            @PathVariable String sessionId) {
        return ResponseEntity.ok(cameraService.getLatestFacialPhoto(sessionId));
    }

    @GetMapping("/evidence/product/{productId}")
    @Operation(summary = "Get product evidence (videos/GIFs)")
    public ResponseEntity<List<VisualEvidenceDto>> getProductEvidence(
            @PathVariable Long productId) {
        return ResponseEntity.ok(cameraService.getProductEvidence(productId));
    }
}
