package com.bkseducate.securityapp.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA para RefreshToken
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {
    
    @Id
    @Column(columnDefinition = "CHAR(36)", length = 36)
    private UUID id;
    

    @Column(name = "user_id", nullable = false, columnDefinition = "CHAR(36)", length = 36)
    private UUID userId;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    private boolean revoked;
    
    // Constructores
    public RefreshTokenEntity() {
    }
    
    public RefreshTokenEntity(UUID id, UUID userId, String token, 
                            LocalDateTime expiresAt, boolean revoked) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
    }
    
    // Getters y Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public boolean isRevoked() {
        return revoked;
    }
    
    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
}
