package com.alwon.pos.camera.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "visual_evidence", schema = "camera")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisualEvidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, length = 100)
    private String sessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "evidence_type", nullable = false, length = 20)
    private EvidenceType evidenceType;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "duration_seconds")
    private Integer durationSeconds; // For videos

    @Column(name = "confidence_score")
    private Float confidenceScore; // For facial recognition

    @Column(name = "face_id", length = 100)
    private String faceId; // Unique face identifier

    @Column(name = "customer_id", length = 100)
    private String customerId;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON metadata

    @Column(name = "captured_at", nullable = false)
    private LocalDateTime capturedAt;

    @PrePersist
    protected void onCreate() {
        if (capturedAt == null) {
            capturedAt = LocalDateTime.now();
        }
    }

    public enum EvidenceType {
        FACIAL_PHOTO, // Foto facial del cliente
        PRODUCT_VIDEO, // Video del producto siendo tomado
        PRODUCT_GIF, // GIF del producto
        ENTRY_PHOTO, // Foto al entrar a la tienda
        EXIT_PHOTO // Foto al salir de la tienda
    }
}
