package com.bkseducate.securityapp.application.dto;

import com.bkseducate.securityapp.domain.model.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import java.util.UUID;

/**
 * DTO para respuesta de usuario
 */
@Schema(description = "Información del usuario")
public record UserResponse(
    @Schema(description = "ID del usuario", example = "550e8400-e29b-41d4-a716-446655440000")
    UUID id,
    
    @Schema(description = "Email del usuario", example = "user@example.com")
    String email,
    
    @Schema(description = "Roles asignados al usuario")
    Set<RoleResponse> roles,
    
    @Schema(description = "Estado del usuario", example = "ACTIVE")
    UserStatus status
) {}
