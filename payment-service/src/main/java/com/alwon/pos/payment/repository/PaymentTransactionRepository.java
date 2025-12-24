package com.alwon.pos.payment.repository;

import com.alwon.pos.payment.model.PaymentTransaction;
import com.alwon.pos.payment.model.PaymentTransaction.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByTransactionId(String transactionId);

    List<PaymentTransaction> findBySessionId(String sessionId);

    List<PaymentTransaction> findByStatus(PaymentStatus status);

    @Query("SELECT p FROM PaymentTransaction p WHERE p.status = :status AND p.createdAt >= :since")
    List<PaymentTransaction> findByStatusAndCreatedAtAfter(
            @Param("status") PaymentStatus status,
            @Param("since") LocalDateTime since);

    @Query("SELECT p FROM PaymentTransaction p WHERE p.sessionId = :sessionId AND p.status = 'APPROVED'")
    Optional<PaymentTransaction> findApprovedPaymentBySessionId(@Param("sessionId") String sessionId);

    @Query("SELECT p FROM PaymentTransaction p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    List<PaymentTransaction> findTransactionsBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
