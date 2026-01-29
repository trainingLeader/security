package com.bkseducate.securityapp.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitud de login
 */
@Schema(description = "Solicitud de inicio de sesión")
public record LoginRequest(
    @Schema(description = "Email del usuario", example = "user@example.com", required = true)
    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe tener un formato válido")
    String email,
    
    @Schema(description = "Contraseña del usuario", example = "password123", required = true)
    @NotBlank(message = "Password es requerido")
    String password
) {}
