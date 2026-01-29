package com.bkseducate.securityapp.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para respuesta de login
 */
@Schema(description = "Respuesta de inicio de sesión con tokens JWT")
public record LoginResponse(
    @Schema(description = "Access token JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String accessToken,
    
    @Schema(description = "Refresh token para renovar el access token", example = "550e8400-e29b-41d4-a716-446655440000")
    String refreshToken,
    
    @Schema(description = "Tipo de token", example = "Bearer")
    String tokenType,
    
    @Schema(description = "Tiempo de expiración en segundos", example = "900")
    Long expiresIn
) {
    public static LoginResponse of(String accessToken, String refreshToken, Long expiresIn) {
        return new LoginResponse(accessToken, refreshToken, "Bearer", expiresIn);
    }
}
