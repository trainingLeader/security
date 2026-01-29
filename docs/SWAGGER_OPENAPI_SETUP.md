# Configuración e Instalación de Swagger/OpenAPI

Este documento explica cómo está configurado Swagger/OpenAPI en el proyecto SecurityApp y cómo utilizarlo.

## 📚 Índice

- [¿Qué es Swagger/OpenAPI?](#qué-es-swaggeropenapi)
- [Dependencias](#dependencias)
- [Configuración](#configuración)
- [Acceso a la Documentación](#acceso-a-la-documentación)
- [Uso de la Interfaz Swagger UI](#uso-de-la-interfaz-swagger-ui)
- [Autenticación en Swagger](#autenticación-en-swagger)
- [Personalización](#personalización)
- [Producción](#producción)

## ¿Qué es Swagger/OpenAPI?

**OpenAPI** (anteriormente Swagger) es una especificación para describir APIs REST. **Swagger UI** es una interfaz web interactiva que permite:

- Visualizar todos los endpoints de la API
- Probar los endpoints directamente desde el navegador
- Ver los modelos de datos (DTOs)
- Entender los requisitos de autenticación
- Generar código cliente automáticamente

## Dependencias

El proyecto usa **SpringDoc OpenAPI**, que es la implementación moderna y recomendada para Spring Boot 3.x.

### Dependencia en `pom.xml`

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

**Versión**: 2.3.0 (compatible con Spring Boot 3.3.0)

## Configuración

### 1. Clase de Configuración (`OpenApiConfig.java`)

La configuración se encuentra en:
```
src/main/java/com/bkseducate/securityapp/infrastructure/config/OpenApiConfig.java
```

Esta clase define:
- **Información de la API**: Título, versión, descripción, contacto, licencia
- **Servidores**: URLs de desarrollo y producción
- **Esquema de seguridad**: Configuración JWT Bearer Token
- **Descripción de autenticación**: Instrucciones para usar JWT

### 2. Configuración en `application.yml`

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
    enabled: true
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    operations-sorter: method
    tags-sorter: alpha
    try-it-out-enabled: true
    filter: true
```

### 3. Seguridad

Los endpoints de Swagger están configurados como públicos en `SecurityConfig.java`:

```java
auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll();
```

Esto permite acceder a la documentación sin autenticación.

## Acceso a la Documentación

Una vez que la aplicación esté ejecutándose, puedes acceder a:

### Swagger UI (Interfaz Interactiva)
```
http://localhost:8080/swagger-ui.html
```

O también:
```
http://localhost:8080/swagger-ui/index.html
```

### API Docs JSON (Especificación OpenAPI)
```
http://localhost:8080/v3/api-docs
```

### API Docs YAML
```
http://localhost:8080/v3/api-docs.yaml
```

## Uso de la Interfaz Swagger UI

### 1. Explorar Endpoints

Swagger UI muestra todos los endpoints organizados por tags:
- **Autenticación**: Registro, login, refresh token, logout, cambio de contraseña
- **Gestión de Usuarios**: Asignación de roles

### 2. Ver Detalles de un Endpoint

Cada endpoint muestra:
- **Método HTTP** (GET, POST, PUT, DELETE)
- **Ruta** del endpoint
- **Descripción** del endpoint
- **Parámetros** requeridos y opcionales
- **Códigos de respuesta** posibles
- **Modelos de datos** (DTOs)

### 3. Probar Endpoints

1. Haz clic en un endpoint para expandirlo
2. Haz clic en **"Try it out"**
3. Completa los campos del request body (si aplica)
4. Haz clic en **"Execute"**
5. Verás la respuesta del servidor

## Autenticación en Swagger

### Configurar el Token JWT

Para probar endpoints protegidos:

1. **Primero, obtén un token**:
   - Ve al endpoint `POST /auth/login`
   - Haz clic en "Try it out"
   - Ingresa credenciales válidas:
     ```json
     {
       "email": "user@example.com",
       "password": "password123"
     }
     ```
   - Ejecuta y copia el `accessToken` de la respuesta

2. **Autoriza en Swagger**:
   - Haz clic en el botón **"Authorize"** 🔒 (arriba a la derecha)
   - En el campo "Value", pega el token (sin la palabra "Bearer")
   - Haz clic en **"Authorize"**
   - Cierra el diálogo

3. **Probar endpoints protegidos**:
   - Ahora puedes probar endpoints como `GET /auth/me` o `PUT /auth/change-password`
   - El token se incluirá automáticamente en el header `Authorization`

### Ejemplo de Token

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1NTA...
```

**Nota**: No incluyas la palabra "Bearer" al pegar el token en Swagger UI.

## Personalización

### Modificar Información de la API

Edita `OpenApiConfig.java` para cambiar:
- Título y descripción
- Información de contacto
- Servidores (URLs)
- Descripción de autenticación

### Agregar Anotaciones a Endpoints

Los controladores ya incluyen anotaciones OpenAPI:

```java
@Operation(
    summary = "Resumen corto",
    description = "Descripción detallada del endpoint"
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Éxito"),
    @ApiResponse(responseCode = "400", description = "Error de validación")
})
@SecurityRequirement(name = "bearerAuth")  // Para endpoints protegidos
@PostMapping("/endpoint")
public ResponseEntity<ResponseDTO> endpoint(...) {
    // ...
}
```

### Documentar DTOs

Los DTOs se documentan automáticamente. Para agregar descripciones:

```java
@Schema(description = "Descripción del DTO")
public record LoginRequest(
    @Schema(description = "Email del usuario", example = "user@example.com")
    String email,
    
    @Schema(description = "Contraseña", example = "password123")
    String password
) {}
```

## Producción

### Deshabilitar Swagger en Producción

Por seguridad, Swagger está **deshabilitado en producción** por defecto.

En `application-prod.yml`:

```yaml
springdoc:
  api-docs:
    enabled: false
  swagger-ui:
    enabled: false
```

### Alternativas para Producción

Si necesitas documentación en producción:

1. **Generar documentación estática**:
   ```bash
   curl http://localhost:8080/v3/api-docs > api-docs.json
   ```

2. **Usar herramientas externas**:
   - Importar `api-docs.json` en herramientas como Postman
   - Generar documentación HTML estática con herramientas como Redoc

3. **Habilitar solo para IPs específicas**:
   - Configurar Spring Security para permitir acceso solo desde IPs de administradores

## Troubleshooting

### Swagger UI no carga

1. Verifica que la aplicación esté ejecutándose
2. Verifica que el puerto sea correcto (por defecto 8080)
3. Revisa los logs para errores de compilación

### No puedo autenticarme

1. Asegúrate de haber obtenido un token válido con `/auth/login`
2. Verifica que el token no haya expirado (15 minutos por defecto)
3. No incluyas "Bearer" al pegar el token en Swagger UI

### Endpoints protegidos muestran 401

1. Verifica que hayas autorizado correctamente en Swagger UI
2. Verifica que el token sea válido y no haya expirado
3. Intenta obtener un nuevo token

### No veo todos los endpoints

1. Verifica que los controladores tengan las anotaciones `@RestController`
2. Verifica que los métodos tengan `@GetMapping`, `@PostMapping`, etc.
3. Revisa que no haya errores de compilación

## Recursos Adicionales

- [Documentación oficial de SpringDoc](https://springdoc.org/)
- [Especificación OpenAPI](https://swagger.io/specification/)
- [Guía de Swagger UI](https://swagger.io/tools/swagger-ui/)

## Ejemplo de Uso Completo

1. **Iniciar la aplicación**:
   ```bash
   mvn spring-boot:run
   ```

2. **Abrir Swagger UI**:
   ```
   http://localhost:8080/swagger-ui.html
   ```

3. **Registrar un usuario**:
   - Expandir `POST /auth/register`
   - Click en "Try it out"
   - Ingresar datos:
     ```json
     {
       "email": "test@example.com",
       "password": "password123"
     }
     ```
   - Click en "Execute"

4. **Hacer login**:
   - Expandir `POST /auth/login`
   - Click en "Try it out"
   - Ingresar las mismas credenciales
   - Copiar el `accessToken` de la respuesta

5. **Autorizar**:
   - Click en "Authorize" 🔒
   - Pegar el token
   - Click en "Authorize"

6. **Probar endpoint protegido**:
   - Expandir `GET /auth/me`
   - Click en "Try it out"
   - Click en "Execute"
   - Ver la información del usuario autenticado

¡Listo! Ya puedes explorar y probar toda la API desde Swagger UI.
