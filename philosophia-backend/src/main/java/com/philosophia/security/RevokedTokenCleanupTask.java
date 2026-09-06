package com.philosophia.security;

import com.philosophia.repository.RevokedTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RevokedTokenCleanupTask {

    private final RevokedTokenRepository revokedTokenRepository;

    public RevokedTokenCleanupTask(RevokedTokenRepository revokedTokenRepository) {
        this.revokedTokenRepository = revokedTokenRepository;
    }

    @Scheduled(cron = "0 0 3 * * *") // daily at 3am
    public void purgeExpired() {
        revokedTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}