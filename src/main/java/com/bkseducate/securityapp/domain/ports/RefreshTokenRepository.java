package com.bkseducate.securityapp.domain.ports;

import com.bkseducate.securityapp.domain.model.RefreshToken;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para persistencia de refresh tokens
 */
public interface RefreshTokenRepository {
    
    /**
     * Guarda un refresh token
     */
    RefreshToken save(RefreshToken refreshToken);
    
    /**
     * Busca un refresh token por token string
     */
    Optional<RefreshToken> findByToken(String token);
    
    /**
     * Busca todos los refresh tokens de un usuario
     */
    java.util.List<RefreshToken> findByUserId(UUID userId);
    
    /**
     * Elimina un refresh token
     */
    void delete(RefreshToken refreshToken);
    
    /**
     * Elimina todos los refresh tokens de un usuario
     */
    void deleteByUserId(UUID userId);
}
