package com.alwon.pos.auth.repository;

import com.alwon.pos.auth.model.Operator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, Long> {

    Optional<Operator> findByUsername(String username);

    Optional<Operator> findByUsernameAndActiveTrue(String username);
}
