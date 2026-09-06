package com.philosophia.repository;

import com.philosophia.models.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {

    boolean existsByJti(String jti);
    void deleteByExpiresAtBefore(LocalDateTime cutoff);
}