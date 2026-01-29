package com.bkseducate.securityapp.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitud de refresh token
 */
@Schema(description = "Solicitud para refrescar el access token")
public record RefreshTokenRequest(
    @Schema(description = "Refresh token obtenido del login", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    @NotBlank(message = "Refresh token es requerido")
    String refreshToken
) {}
