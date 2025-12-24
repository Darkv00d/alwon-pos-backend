package com.alwon.pos.camera.service;

import com.alwon.pos.camera.dto.*;
import com.alwon.pos.camera.model.VisualEvidence;
import com.alwon.pos.camera.model.VisualEvidence.EvidenceType;
import com.alwon.pos.camera.repository.VisualEvidenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CameraService {

    private final VisualEvidenceRepository evidenceRepository;
    private final Random random = new Random();

    // Mock database of known faces
    private static final String[] MOCK_CUSTOMERS = {
            "CUST-001:Juan Pérez",
            "CUST-002:María García",
            "CUST-003:Carlos López",
            "CUST-004:Ana Martínez",
            "CUST-005:Pedro Rodríguez"
    };

    @Transactional
    public FacialRecognitionResponse performFacialRecognition(FacialRecognitionRequest request) {
        log.info("Performing facial recognition for session: {}", request.getSessionId());

        // Simulate processing delay
        simulateProcessing(500, 1500);

        FacialRecognitionResponse response = new FacialRecognitionResponse();
        response.setSuccess(true);

        // Mock facial detection (95% success rate)
        boolean faceDetected = random.nextInt(100) < 95;
        response.setFaceDetected(faceDetected);

        if (!faceDetected) {
            response.setMessage("No face detected in the image");
            return response;
        }

        // Generate or retrieve face ID
        String faceId = "FACE-" + UUID.randomUUID().toString().substring(0, 8);
        response.setFaceId(faceId);

        // Mock recognition (70% success rate for known customers)
        boolean recognized = random.nextInt(100) < 70;
        float confidenceScore = recognized ? 85.0f + random.nextFloat() * 13.0f : // 85-98%
                40.0f + random.nextFloat() * 40.0f; // 40-80%

        response.setConfidenceScore(confidenceScore);

        if (recognized && confidenceScore >= 85.0f) {
            // Return a random known customer
            String[] customerData = MOCK_CUSTOMERS[random.nextInt(MOCK_CUSTOMERS.length)].split(":");
            response.setCustomerId(customerData[0]);
            response.setCustomerName(customerData[1]);
            response.setMessage("Customer identified successfully");
        } else {
            response.setMessage("Face detected but customer not recognized");
        }

        // Save evidence
        String evidenceUrl = saveEvidenceToStorage(request.getImageData(), request.getMimeType());
        response.setEvidenceUrl(evidenceUrl);

        VisualEvidence evidence = new VisualEvidence();
        evidence.setSessionId(request.getSessionId());
        evidence.setEvidenceType(EvidenceType.FACIAL_PHOTO);
        evidence.setFileUrl(evidenceUrl);
        evidence.setMimeType(request.getMimeType());
        evidence.setFaceId(faceId);
        evidence.setCustomerId(response.getCustomerId());
        evidence.setConfidenceScore(confidenceScore);
        evidence.setFileSizeBytes(estimateFileSize(request.getImageData()));
        evidence.setMetadata(request.getMetadata());
        evidence.setCapturedAt(LocalDateTime.now());

        evidenceRepository.save(evidence);

        log.info("Facial recognition completed for session: {} - Face detected: {}, Recognized: {}",
                request.getSessionId(), faceDetected, recognized);

        return response;
    }

    @Transactional
    public VisualEvidenceDto captureEvidence(CaptureEvidenceRequest request) {
        log.info("Capturing evidence type {} for session: {}",
                request.getEvidenceType(), request.getSessionId());

        // Save file to storage
        String fileUrl = saveEvidenceToStorage(request.getFileData(), request.getMimeType());

        VisualEvidence evidence = new VisualEvidence();
        evidence.setSessionId(request.getSessionId());
        evidence.setEvidenceType(EvidenceType.valueOf(request.getEvidenceType()));
        evidence.setProductId(request.getProductId());
        evidence.setFileUrl(fileUrl);
        evidence.setMimeType(request.getMimeType());
        evidence.setDurationSeconds(request.getDurationSeconds());
        evidence.setFileSizeBytes(estimateFileSize(request.getFileData()));
        evidence.setMetadata(request.getMetadata());
        evidence.setCapturedAt(LocalDateTime.now());

        VisualEvidence saved = evidenceRepository.save(evidence);

        log.info("Evidence captured with ID: {}", saved.getId());

        return convertToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<VisualEvidenceDto> getSessionEvidence(String sessionId) {
        log.debug("Fetching evidence for session: {}", sessionId);
        return evidenceRepository.findBySessionId(sessionId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VisualEvidenceDto> getSessionEvidenceByType(String sessionId, String evidenceType) {
        log.debug("Fetching evidence type {} for session: {}", evidenceType, sessionId);
        EvidenceType type = EvidenceType.valueOf(evidenceType);
        return evidenceRepository.findBySessionIdAndEvidenceType(sessionId, type).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VisualEvidenceDto> getCustomerEvidence(String customerId) {
        log.debug("Fetching evidence for customer: {}", customerId);
        return evidenceRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VisualEvidenceDto getLatestFacialPhoto(String sessionId) {
        log.debug("Fetching latest facial photo for session: {}", sessionId);
        return evidenceRepository.findLatestFacialPhotoBySession(sessionId)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No facial photo found for session: " + sessionId));
    }

    @Transactional(readOnly = true)
    public List<VisualEvidenceDto> getProductEvidence(Long productId) {
        log.debug("Fetching product evidence for product ID: {}", productId);
        return evidenceRepository.findProductEvidence(productId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Mock file storage - in production, this would upload to S3, Azure Blob, etc.
     */
    private String saveEvidenceToStorage(String base64Data, String mimeType) {
        // Generate mock URL
        String fileId = UUID.randomUUID().toString();
        String extension = mimeType != null ? mimeType.split("/")[1] : "jpg";
        return String.format("/storage/evidence/%s.%s", fileId, extension);
    }

    private Long estimateFileSize(String base64Data) {
        if (base64Data == null)
            return 0L;
        // Base64 is roughly 4/3 the size of original
        return (long) (base64Data.length() * 0.75);
    }

    private void simulateProcessing(int minMs, int maxMs) {
        try {
            Thread.sleep(minMs + random.nextInt(maxMs - minMs));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private VisualEvidenceDto convertToDto(VisualEvidence evidence) {
        VisualEvidenceDto dto = new VisualEvidenceDto();
        dto.setId(evidence.getId());
        dto.setSessionId(evidence.getSessionId());
        dto.setEvidenceType(evidence.getEvidenceType().name());
        dto.setProductId(evidence.getProductId());
        dto.setFileUrl(evidence.getFileUrl());
        dto.setFileSizeBytes(evidence.getFileSizeBytes());
        dto.setMimeType(evidence.getMimeType());
        dto.setDurationSeconds(evidence.getDurationSeconds());
        dto.setConfidenceScore(evidence.getConfidenceScore());
        dto.setFaceId(evidence.getFaceId());
        dto.setCustomerId(evidence.getCustomerId());
        dto.setMetadata(evidence.getMetadata());
        dto.setCapturedAt(evidence.getCapturedAt());
        return dto;
    }
}

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
