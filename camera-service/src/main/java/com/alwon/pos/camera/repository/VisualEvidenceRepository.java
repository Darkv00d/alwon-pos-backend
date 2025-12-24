package com.alwon.pos.camera.repository;

import com.alwon.pos.camera.model.VisualEvidence;
import com.alwon.pos.camera.model.VisualEvidence.EvidenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisualEvidenceRepository extends JpaRepository<VisualEvidence, Long> {

    List<VisualEvidence> findBySessionId(String sessionId);

    List<VisualEvidence> findBySessionIdAndEvidenceType(String sessionId, EvidenceType evidenceType);

    List<VisualEvidence> findByCustomerId(String customerId);

    List<VisualEvidence> findByFaceId(String faceId);

    @Query("SELECT v FROM VisualEvidence v WHERE v.sessionId = :sessionId AND v.evidenceType = 'FACIAL_PHOTO' ORDER BY v.capturedAt DESC")
    Optional<VisualEvidence> findLatestFacialPhotoBySession(@Param("sessionId") String sessionId);

    @Query("SELECT v FROM VisualEvidence v WHERE v.productId = :productId AND v.evidenceType IN ('PRODUCT_VIDEO', 'PRODUCT_GIF')")
    List<VisualEvidence> findProductEvidence(@Param("productId") Long productId);

    @Query("SELECT v FROM VisualEvidence v WHERE v.capturedAt BETWEEN :startDate AND :endDate")
    List<VisualEvidence> findEvidenceBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT v FROM VisualEvidence v WHERE v.evidenceType = 'FACIAL_PHOTO' AND v.confidenceScore >= :minConfidence")
    List<VisualEvidence> findHighConfidenceFacialRecognitions(@Param("minConfidence") Float minConfidence);
}
