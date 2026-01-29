package com.bkseducate.securityapp.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
            .info(new Info()
                .title("SecurityApp API")
                .version("1.0.0")
                .description("""
                    API de autenticación y autorización construida con Spring Boot y Spring Security 6.
                    
                    ## Autenticación
                    Esta API usa JWT (JSON Web Tokens) para autenticación. Para acceder a los endpoints protegidos:
                    
                    1. Registra un usuario con `POST /auth/register`
                    2. Inicia sesión con `POST /auth/login` para obtener un access token y refresh token
                    3. Incluye el token en el header: `Authorization: Bearer <access_token>`
                    
                    ## Refresh Token
                    Cuando el access token expire, usa `POST /auth/refresh` con el refresh token para obtener un nuevo access token.
                    
                    ## Roles
                    - **ROLE_USER**: Usuario estándar (asignado por defecto)
                    - **ROLE_ADMIN**: Administrador (requerido para asignar roles)
                    - **ROLE_MODERATOR**: Moderador
                    """)
                .contact(new Contact()
                    .name("SecurityApp Support")
                    .email("support@bkseducate.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:" + serverPort)
                    .description("Servidor de desarrollo"),
                new Server()
                    .url("https://api.example.com")
                    .description("Servidor de producción")))
            .addSecurityItem(new SecurityRequirement()
                .addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName,
                    new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token obtenido del endpoint /auth/login")));
    }
}
