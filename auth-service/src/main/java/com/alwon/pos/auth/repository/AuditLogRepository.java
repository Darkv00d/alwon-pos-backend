package com.alwon.pos.auth.repository;

import com.alwon.pos.auth.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    // Simple repository, no custom methods needed for now
}
