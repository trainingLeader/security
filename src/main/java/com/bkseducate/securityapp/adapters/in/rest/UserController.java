package com.bkseducate.securityapp.adapters.in.rest;

import com.bkseducate.securityapp.application.dto.AssignRoleRequest;
import com.bkseducate.securityapp.application.dto.UserResponse;
import com.bkseducate.securityapp.application.usecase.AssignRoleUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador REST para gestión de usuarios
 * Requiere autenticación y roles específicos
 */
@Tag(name = "Gestión de Usuarios", description = "Endpoints para administración de usuarios y roles")
@RestController
@RequestMapping("/users")
public class UserController {
    
    private final AssignRoleUseCase assignRoleUseCase;
    
    public UserController(AssignRoleUseCase assignRoleUseCase) {
        this.assignRoleUseCase = assignRoleUseCase;
    }
    
    @Operation(
        summary = "Asignar rol a usuario",
        description = "Asigna un rol específico a un usuario. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol asignado exitosamente",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene permisos (requiere ADMIN)"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> assignRole(
            @PathVariable UUID userId,
            @Valid @RequestBody AssignRoleRequest request) {
        UserResponse response = assignRoleUseCase.execute(userId, request);
        return ResponseEntity.ok(response);
    }
}
