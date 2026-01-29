package com.bkseducate.securityapp.infrastructure.security;

import com.bkseducate.securityapp.domain.model.Role;
import com.bkseducate.securityapp.domain.ports.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación del puerto TokenService usando JWT
 * Esta es una implementación de infraestructura, no está en el dominio
 */
@Service
public class JwtTokenService implements TokenService {
    
    private final SecretKey secretKey;
    private final Long accessTokenExpiration;
    
    public JwtTokenService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") Long accessTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
    }
    
    @Override
    public String generateAccessToken(UUID userId, String email, Set<Role> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration);
        
        List<String> roleAuthorities = roles.stream()
            .map(Role::getAuthority)
            .collect(Collectors.toList());
        
        return Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("roles", roleAuthorities)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact();
    }
    
    @Override
    public String generateRefreshToken() {
        // Refresh token es un UUID aleatorio
        return UUID.randomUUID().toString();
    }
    
    @Override
    public UUID validateAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            
            return UUID.fromString(claims.getSubject());
        } catch (Exception e) {
            throw new RuntimeException("Token inválido", e);
        }
    }
    
    @Override
    public Set<Role> extractRoles(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            
            @SuppressWarnings("unchecked")
            List<String> roleAuthorities = claims.get("roles", List.class);
            
            if (roleAuthorities == null || roleAuthorities.isEmpty()) {
                return Set.of();
            }
            
            // Retornar roles como entidades temporales basadas en authority
            // En un sistema real, estos deberían buscarse desde la BD
            return roleAuthorities.stream()
                .map(authority -> {
                    // Crear rol temporal basado en authority
                    // En producción, deberías buscar desde RoleRepository
                    String name = authority.replace("ROLE_", "").toUpperCase();
                    return Role.reconstruct(
                        UUID.randomUUID(), // ID temporal, no se usa para comparación
                        name,
                        authority
                    );
                })
                .collect(Collectors.toSet());
        } catch (Exception e) {
            return Set.of(); // Retornar conjunto vacío en caso de error
        }
    }
    
    /**
     * Extrae el email del token
     */
    public String extractEmail(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        
        return claims.get("email", String.class);
    }
}
