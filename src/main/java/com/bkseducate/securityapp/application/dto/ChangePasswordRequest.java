package com.bkseducate.securityapp.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para cambio de contraseña
 */
@Schema(description = "Solicitud para cambiar la contraseña del usuario")
public record ChangePasswordRequest(
    @Schema(description = "Contraseña actual", example = "oldpassword123", required = true)
    @NotBlank(message = "Password actual es requerido")
    String currentPassword,
    
    @Schema(description = "Nueva contraseña (mínimo 6 caracteres)", example = "newpassword456", required = true)
    @NotBlank(message = "Nueva password es requerida")
    @Size(min = 6, message = "Nueva password debe tener al menos 6 caracteres")
    String newPassword
) {}
