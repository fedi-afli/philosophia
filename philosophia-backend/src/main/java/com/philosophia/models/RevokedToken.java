package com.philosophia.models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "revoked_tokens")
public class RevokedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String jti;

    @Column(nullable = false)
    private Instant revokedAt = Instant.now();

    @Column(nullable = false)
    private Instant expiresAt;

    public Long getId() { return id; }
    public String getJti() { return jti; }
    public void setJti(String jti) { this.jti = jti; }
    public Instant getRevokedAt() { return revokedAt; }
    public void setRevokedAt(Instant revokedAt) { this.revokedAt = revokedAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}