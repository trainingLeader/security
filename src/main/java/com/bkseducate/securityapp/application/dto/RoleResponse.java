package com.bkseducate.securityapp.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

/**
 * DTO para respuesta de Role
 */
@Schema(description = "Rol del sistema")
public record RoleResponse(
    @Schema(description = "ID del rol", example = "550e8400-e29b-41d4-a716-446655440001")
    UUID id,
    
    @Schema(description = "Nombre del rol", example = "USER")
    String name,
    
    @Schema(description = "Authority del rol", example = "ROLE_USER")
    String authority
) {}
