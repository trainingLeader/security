package com.bkseducate.securityapp.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para asignación de roles
 * Puede recibir el nombre del rol (USER, ADMIN, MODERATOR) o el authority (ROLE_USER, ROLE_ADMIN, etc.)
 */
@Schema(description = "Solicitud para asignar un rol a un usuario")
public record AssignRoleRequest(
    @Schema(description = "Nombre del rol o authority (ej: USER, ADMIN, ROLE_USER, ROLE_ADMIN)", example = "ADMIN", required = true)
    @NotBlank(message = "Nombre del rol es requerido")
    String roleName
) {}
