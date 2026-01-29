package com.bkseducate.securityapp.adapters.in.rest;

import com.bkseducate.securityapp.application.dto.*;
import com.bkseducate.securityapp.application.usecase.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador REST para autenticación
 * Adaptador de entrada que expone los casos de uso a través de HTTP
 */
@Tag(name = "Autenticación", description = "Endpoints para registro, login, refresh token y gestión de sesión")
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final CreateUserUseCase createUserUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    
    public AuthController(
            CreateUserUseCase createUserUseCase,
            LoginUseCase loginUseCase,
            RefreshTokenUseCase refreshTokenUseCase,
            LogoutUseCase logoutUseCase,
            ChangePasswordUseCase changePasswordUseCase,
            GetCurrentUserUseCase getCurrentUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }
    
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea un nuevo usuario en el sistema. Se asigna automáticamente el rol ROLE_USER."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya existe"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = createUserUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un usuario y retorna un access token y refresh token JWT."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = loginUseCase.execute(request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Refrescar access token",
        description = "Genera un nuevo access token usando un refresh token válido."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refrescado exitosamente",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado")
    })
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = refreshTokenUseCase.execute(request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Cerrar sesión",
        description = "Invalida el refresh token, cerrando la sesión del usuario."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Sesión cerrada exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request, 
                                       Authentication authentication) {
        logoutUseCase.execute(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
    
    @Operation(
        summary = "Obtener usuario autenticado",
        description = "Retorna la información del usuario actualmente autenticado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario obtenido exitosamente",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        UserResponse response = getCurrentUserUseCase.execute(userId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Cambiar contraseña",
        description = "Permite al usuario autenticado cambiar su contraseña. Requiere la contraseña actual."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Contraseña cambiada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "Contraseña actual incorrecta o no autenticado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        changePasswordUseCase.execute(userId, request);
        return ResponseEntity.noContent().build();
    }
}
