package com.bkseducate.securityapp.domain.ports;

import com.bkseducate.securityapp.domain.model.Role;
import java.util.Set;
import java.util.UUID;

/**
 * Puerto de salida para generación de tokens JWT
 * El dominio necesita generar tokens pero no conoce la implementación JWT
 */
public interface TokenService {
    
    /**
     * Genera un access token para un usuario
     */
    String generateAccessToken(UUID userId, String email, Set<Role> roles);
    
    /**
     * Genera un refresh token
     */
    String generateRefreshToken();
    
    /**
     * Valida un access token y retorna el userId
     */
    UUID validateAccessToken(String token);
    
    /**
     * Extrae los roles de un token
     */
    Set<Role> extractRoles(String token);
}
