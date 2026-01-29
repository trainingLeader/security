package com.bkseducate.securityapp.domain.model;

import com.bkseducate.securityapp.domain.exceptions.DomainException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de dominio RefreshToken
 * Representa un token de refresco para renovar el access token
 */
public class RefreshToken {
    
    private UUID id;
    private UUID userId;
    private String token;
    private LocalDateTime expiresAt;
    private boolean revoked;
    
    private RefreshToken() {
    }
    
    /**
     * Factory method para crear un nuevo refresh token
     */
    public static RefreshToken create(UUID userId, String token, LocalDateTime expiresAt) {
        if (userId == null) {
            throw new DomainException("UserId no puede ser nulo");
        }
        if (token == null || token.isBlank()) {
            throw new DomainException("Token no puede estar vacío");
        }
        if (expiresAt == null) {
            throw new DomainException("Fecha de expiración no puede ser nula");
        }
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.id = UUID.randomUUID();
        refreshToken.userId = userId;
        refreshToken.token = token;
        refreshToken.expiresAt = expiresAt;
        refreshToken.revoked = false;
        
        return refreshToken;
    }
    
    /**
     * Factory method para reconstruir desde persistencia
     */
    public static RefreshToken reconstruct(UUID id, UUID userId, String token, 
                                          LocalDateTime expiresAt, boolean revoked) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.id = id;
        refreshToken.userId = userId;
        refreshToken.token = token;
        refreshToken.expiresAt = expiresAt;
        refreshToken.revoked = revoked;
        return refreshToken;
    }
    
    /**
     * Revoca el token
     */
    public void revoke() {
        this.revoked = true;
    }
    
    /**
     * Verifica si el token está expirado
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    /**
     * Verifica si el token es válido (no revocado y no expirado)
     */
    public boolean isValid() {
        return !revoked && !isExpired();
    }
    
    // Getters
    public UUID getId() {
        return id;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public String getToken() {
        return token;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public boolean isRevoked() {
        return revoked;
    }
}
