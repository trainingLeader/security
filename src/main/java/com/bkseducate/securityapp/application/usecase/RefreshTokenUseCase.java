package com.bkseducate.securityapp.application.usecase;

import com.bkseducate.securityapp.application.dto.LoginResponse;
import com.bkseducate.securityapp.application.dto.RefreshTokenRequest;
import com.bkseducate.securityapp.domain.exceptions.DomainException;
import com.bkseducate.securityapp.domain.exceptions.InvalidCredentialsException;
import com.bkseducate.securityapp.domain.exceptions.UserNotFoundException;
import com.bkseducate.securityapp.domain.model.RefreshToken;
import com.bkseducate.securityapp.domain.model.User;
import com.bkseducate.securityapp.domain.ports.RefreshTokenRepository;
import com.bkseducate.securityapp.domain.ports.TokenService;
import com.bkseducate.securityapp.domain.ports.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Caso de uso: Refrescar token
 * Valida el refresh token y genera un nuevo access token
 */
@Service
public class RefreshTokenUseCase {
    
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final Long accessTokenExpiration;
    
    public RefreshTokenUseCase(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            TokenService tokenService,
            @Value("${jwt.access-token-expiration}") Long accessTokenExpiration) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.accessTokenExpiration = accessTokenExpiration;
    }
    
    @Transactional
    public LoginResponse execute(RefreshTokenRequest request) {
        // Buscar refresh token
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
            .orElseThrow(() -> new InvalidCredentialsException("Refresh token inválido"));
        
        // Verificar si es válido
        if (!refreshToken.isValid()) {
            throw new InvalidCredentialsException("Refresh token expirado o revocado");
        }
        
        // Buscar usuario
        User user = userRepository.findById(refreshToken.getUserId())
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        
        // Verificar si está activo
        if (!user.isActive()) {
            throw new InvalidCredentialsException("Usuario bloqueado o inactivo");
        }
        
        // Generar nuevo access token
        String accessToken = tokenService.generateAccessToken(
            user.getId(),
            user.getEmail(),
            user.getRoles()
        );
        
        // Retornar nuevo access token con el mismo refresh token
        return LoginResponse.of(accessToken, request.refreshToken(), accessTokenExpiration / 1000);
    }
}
