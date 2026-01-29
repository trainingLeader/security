# Sistema de Autenticación JWT - Documentación Completa

## 📋 Tabla de Contenidos

1. [Introducción](#introducción)
2. [Arquitectura del Sistema](#arquitectura-del-sistema)
3. [Componentes Principales](#componentes-principales)
4. [Flujo de Autenticación](#flujo-de-autenticación)
5. [Estructura de Tokens JWT](#estructura-de-tokens-jwt)
6. [Endpoints y Casos de Uso](#endpoints-y-casos-de-uso)
7. [Configuración](#configuración)
8. [Seguridad](#seguridad)
9. [Diagramas de Flujo](#diagramas-de-flujo)
10. [Ejemplos de Uso](#ejemplos-de-uso)
11. [Troubleshooting](#troubleshooting)

---

## Introducción

Este documento describe el sistema completo de autenticación y autorización basado en **JWT (JSON Web Tokens)** implementado en SecurityApp. El sistema utiliza una arquitectura hexagonal (puertos y adaptadores) con Domain-Driven Design (DDD), garantizando separación de responsabilidades y mantenibilidad.

### Características Principales

- ✅ Autenticación con JWT (Access Token + Refresh Token)
- ✅ Refresh Token persistido en base de datos
- ✅ Tokens revocables mediante logout
- ✅ Autorización basada en roles (RBAC)
- ✅ Arquitectura Hexagonal pura
- ✅ Dominio sin dependencias de frameworks
- ✅ Configuración flexible y extensible

---

## Arquitectura del Sistema

### Principios de Diseño

El sistema sigue **Arquitectura Hexagonal** (también conocida como Ports & Adapters), donde:

- **Dominio**: Contiene la lógica de negocio pura, sin dependencias externas
- **Aplicación**: Contiene los casos de uso que orquestan el dominio
- **Infraestructura**: Implementa los detalles técnicos (JWT, JPA, Spring Security)
- **Adaptadores**: Conectan las capas externas con el dominio

### Estructura de Capas

```
┌─────────────────────────────────────────────────────────┐
│                  ADAPTERS (IN)                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │  AuthController, UserController (REST)            │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│                  APPLICATION                            │
│  ┌──────────────────────────────────────────────────┐  │
│  │  Use Cases: Login, Refresh, Logout, etc.        │  │
│  │  DTOs: LoginRequest, LoginResponse, etc.        │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│                  DOMAIN                                 │
│  ┌──────────────────────────────────────────────────┐  │
│  │  Models: User, Role, RefreshToken                │  │
│  │  Ports: TokenService, PasswordService, etc.    │  │
│  │  Exceptions: DomainException, etc.                │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│            INFRASTRUCTURE + ADAPTERS (OUT)               │
│  ┌──────────────────────────────────────────────────┐  │
│  │  JwtTokenService, PasswordServiceImpl           │  │
│  │  UserRepositoryAdapter, RefreshTokenAdapter     │  │
│  │  SecurityConfig, JwtAuthenticationFilter        │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

---

## Componentes Principales

### 1. Dominio

#### 1.1 Modelo: User

**Ubicación**: `domain/model/User.java`

Entidad de dominio que representa un usuario del sistema.

**Características**:
- Factory methods: `create()`, `reconstruct()`
- Validaciones de negocio (email, password)
- Gestión de roles y estado
- Sin dependencias de frameworks

**Métodos principales**:
- `assignRole(Role role)`: Asigna un rol al usuario
- `removeRole(Role role)`: Remueve un rol
- `hasRole(Role role)`: Verifica si tiene un rol
- `changePassword(String newPassword)`: Cambia la contraseña
- `block()` / `activate()`: Gestiona el estado del usuario

#### 1.2 Modelo: Role

**Ubicación**: `domain/model/Role.java`

Entidad de dominio que representa un rol del sistema.

**Características**:
- `id`: UUID único
- `name`: Nombre del rol (USER, ADMIN, MODERATOR)
- `authority`: Authority de Spring Security (ROLE_USER, ROLE_ADMIN, etc.)

**Roles disponibles**:
- `USER` → `ROLE_USER` (asignado por defecto)
- `ADMIN` → `ROLE_ADMIN` (requerido para gestión de roles)
- `MODERATOR` → `ROLE_MODERATOR`

#### 1.3 Modelo: RefreshToken

**Ubicación**: `domain/model/RefreshToken.java`

Entidad de dominio que representa un refresh token.

**Características**:
- `id`: UUID único
- `userId`: ID del usuario propietario
- `token`: String del token (UUID)
- `expiresAt`: Fecha de expiración
- `revoked`: Flag de revocación

**Métodos principales**:
- `revoke()`: Revoca el token
- `isExpired()`: Verifica si está expirado
- `isValid()`: Verifica si es válido (no revocado y no expirado)

#### 1.4 Puertos (Interfaces)

**TokenService** (`domain/ports/TokenService.java`):
- `generateAccessToken()`: Genera access token JWT
- `generateRefreshToken()`: Genera refresh token (UUID)
- `validateAccessToken()`: Valida y extrae userId
- `extractRoles()`: Extrae roles del token

**PasswordService** (`domain/ports/PasswordService.java`):
- `hash()`: Hashea contraseña en texto plano
- `matches()`: Verifica contraseña contra hash

**UserRepository** (`domain/ports/UserRepository.java`):
- `save()`, `findByEmail()`, `findById()`, `existsByEmail()`, `delete()`

**RefreshTokenRepository** (`domain/ports/RefreshTokenRepository.java`):
- `save()`, `findByToken()`, `findByUserId()`, `delete()`, `deleteByUserId()`

### 2. Aplicación (Casos de Uso)

#### 2.1 LoginUseCase

**Ubicación**: `application/usecase/LoginUseCase.java`

**Responsabilidad**: Autenticar usuario y generar tokens.

**Flujo**:
1. Buscar usuario por email
2. Verificar que esté activo
3. Verificar contraseña con BCrypt
4. Generar access token JWT
5. Generar refresh token (UUID)
6. Crear y persistir RefreshToken
7. Retornar ambos tokens

**Dependencias**:
- `UserRepository`
- `PasswordService`
- `TokenService`
- `RefreshTokenRepository`

#### 2.2 RefreshTokenUseCase

**Ubicación**: `application/usecase/RefreshTokenUseCase.java`

**Responsabilidad**: Renovar access token usando refresh token.

**Flujo**:
1. Buscar refresh token en BD
2. Verificar que sea válido (no revocado, no expirado)
3. Buscar usuario asociado
4. Verificar que usuario esté activo
5. Generar nuevo access token
6. Retornar nuevo access token (mismo refresh token)

#### 2.3 LogoutUseCase

**Ubicación**: `application/usecase/LogoutUseCase.java`

**Responsabilidad**: Invalidar refresh token.

**Flujo**:
1. Buscar refresh token
2. Marcar como revocado (`revoked = true`)
3. Guardar cambios

#### 2.4 Otros Casos de Uso

- **CreateUserUseCase**: Registro de usuario (asigna ROLE_USER automáticamente)
- **GetCurrentUserUseCase**: Obtener información del usuario autenticado
- **ChangePasswordUseCase**: Cambiar contraseña del usuario autenticado
- **AssignRoleUseCase**: Asignar rol a usuario (requiere ADMIN)

### 3. Infraestructura

#### 3.1 JwtTokenService

**Ubicación**: `infrastructure/security/JwtTokenService.java`

**Implementación**: `TokenService` usando la librería `jjwt`.

**Características**:
- Algoritmo: HMAC SHA-256
- Claims incluidos:
  - `sub`: UserId (subject)
  - `email`: Email del usuario
  - `roles`: Lista de authorities (ROLE_USER, ROLE_ADMIN, etc.)
  - `iat`: Issued at (fecha de emisión)
  - `exp`: Expiration (fecha de expiración)

**Métodos**:
- `generateAccessToken()`: Crea JWT con claims
- `generateRefreshToken()`: Genera UUID aleatorio
- `validateAccessToken()`: Valida firma y expiración
- `extractRoles()`: Extrae roles del token
- `extractEmail()`: Extrae email del token

#### 3.2 JwtAuthenticationFilter

**Ubicación**: `infrastructure/security/JwtAuthenticationFilter.java`

**Tipo**: `OncePerRequestFilter` (se ejecuta una vez por request)

**Responsabilidad**: 
- Extraer token del header `Authorization: Bearer <token>`
- Validar token
- Establecer autenticación en `SecurityContext`

**Flujo**:
1. Extraer token del header
2. Si existe token:
   - Validar con `JwtTokenService`
   - Extraer userId y roles
   - Crear `UsernamePasswordAuthenticationToken`
   - Establecer en `SecurityContextHolder`
3. Continuar con el filtro chain

#### 3.3 SecurityConfig

**Ubicación**: `infrastructure/security/SecurityConfig.java`

**Configuración Spring Security 6**:
- **Stateless**: Sin sesiones (`SessionCreationPolicy.STATELESS`)
- **CSRF**: Deshabilitado (no necesario para APIs REST)
- **CORS**: Configurado para permitir todos los orígenes
- **Filtros**: `JwtAuthenticationFilter` antes de `UsernamePasswordAuthenticationFilter`
- **Endpoints públicos**: `/auth/login`, `/auth/register`, `/auth/refresh`, Swagger UI
- **Endpoints protegidos**: Todos los demás requieren autenticación

#### 3.4 PasswordServiceImpl

**Ubicación**: `infrastructure/security/PasswordServiceImpl.java`

**Implementación**: `PasswordService` usando BCrypt de Spring Security.

**Características**:
- Algoritmo: BCrypt (salt automático)
- Strength: Por defecto de Spring Security (10 rounds)

### 4. Adaptadores

#### 4.1 AuthController (Adapter IN)

**Ubicación**: `adapters/in/rest/AuthController.java`

**Endpoints**:
- `POST /auth/register`: Registro de usuario
- `POST /auth/login`: Login (retorna tokens)
- `POST /auth/refresh`: Refrescar access token
- `POST /auth/logout`: Cerrar sesión (requiere autenticación)
- `GET /auth/me`: Obtener usuario autenticado (requiere autenticación)
- `PUT /auth/change-password`: Cambiar contraseña (requiere autenticación)

#### 4.2 Repository Adapters (Adapter OUT)

**UserRepositoryAdapter**: Convierte entre `User` (dominio) ↔ `UserEntity` (JPA)

**RefreshTokenRepositoryAdapter**: Convierte entre `RefreshToken` (dominio) ↔ `RefreshTokenEntity` (JPA)

---

## Flujo de Autenticación

### 1. Registro de Usuario

```
Cliente → POST /auth/register
    ↓
AuthController.register()
    ↓
CreateUserUseCase.execute()
    ↓
1. Validar email único
2. Hashear contraseña (BCrypt)
3. Crear User (dominio)
4. Buscar Role USER desde BD
5. Asignar rol USER
6. Guardar usuario
    ↓
Retornar UserResponse (201 Created)
```

### 2. Login

```
Cliente → POST /auth/login {email, password}
    ↓
AuthController.login()
    ↓
LoginUseCase.execute()
    ↓
1. Buscar User por email
2. Verificar usuario activo
3. Verificar contraseña (BCrypt)
4. Generar Access Token JWT
   - Claims: userId, email, roles
   - Expiración: 15 minutos
5. Generar Refresh Token (UUID)
6. Crear RefreshToken (dominio)
   - Expiración: 7 días
7. Persistir RefreshToken
    ↓
Retornar LoginResponse {accessToken, refreshToken, tokenType, expiresIn}
```

### 3. Acceso a Endpoint Protegido

```
Cliente → GET /auth/me
    Header: Authorization: Bearer <access_token>
    ↓
JwtAuthenticationFilter.doFilterInternal()
    ↓
1. Extraer token del header
2. Validar token (JwtTokenService.validateAccessToken())
   - Verificar firma
   - Verificar expiración
3. Extraer userId y roles
4. Crear UsernamePasswordAuthenticationToken
5. Establecer en SecurityContextHolder
    ↓
AuthController.getCurrentUser()
    ↓
GetCurrentUserUseCase.execute()
    ↓
Retornar UserResponse
```

### 4. Refresh Token

```
Cliente → POST /auth/refresh {refreshToken}
    ↓
AuthController.refresh()
    ↓
RefreshTokenUseCase.execute()
    ↓
1. Buscar RefreshToken en BD
2. Verificar válido (no revocado, no expirado)
3. Buscar User asociado
4. Verificar usuario activo
5. Generar nuevo Access Token
    ↓
Retornar LoginResponse {newAccessToken, sameRefreshToken}
```

### 5. Logout

```
Cliente → POST /auth/logout {refreshToken}
    Header: Authorization: Bearer <access_token>
    ↓
JwtAuthenticationFilter (valida access token)
    ↓
AuthController.logout()
    ↓
LogoutUseCase.execute()
    ↓
1. Buscar RefreshToken
2. Marcar como revocado (revoked = true)
3. Guardar cambios
    ↓
Retornar 204 No Content
```

---

## Estructura de Tokens JWT

### Access Token

**Formato**: JWT (JSON Web Token)

**Estructura**:
```
Header:
{
  "alg": "HS256",
  "typ": "JWT"
}

Payload:
{
  "sub": "550e8400-e29b-41d4-a716-446655440000",  // UserId
  "email": "user@example.com",
  "roles": ["ROLE_USER", "ROLE_ADMIN"],
  "iat": 1706486400,  // Issued at (timestamp)
  "exp": 1706487300   // Expiration (timestamp, 15 min después)
}

Signature:
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

**Características**:
- **Algoritmo**: HMAC SHA-256
- **Expiración**: 15 minutos (configurable)
- **Claims**: userId (sub), email, roles, iat, exp
- **Firma**: Con clave secreta configurada

### Refresh Token

**Formato**: UUID (String)

**Ejemplo**: `550e8400-e29b-41d4-a716-446655440000`

**Características**:
- **Tipo**: UUID aleatorio generado
- **Almacenamiento**: Persistido en base de datos
- **Expiración**: 7 días (configurable)
- **Revocable**: Puede ser invalidado mediante logout
- **Validación**: Verificada contra base de datos

**Tabla `refresh_tokens`**:
```sql
CREATE TABLE refresh_tokens (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    token VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE
);
```

---

## Endpoints y Casos de Uso

### Endpoints Públicos

#### POST /auth/register

**Descripción**: Registra un nuevo usuario en el sistema.

**Request**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response** (201 Created):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "roles": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "name": "USER",
      "authority": "ROLE_USER"
    }
  ],
  "status": "ACTIVE"
}
```

**Caso de Uso**: `CreateUserUseCase`
- Valida email único
- Hashea contraseña
- Asigna ROLE_USER automáticamente

#### POST /auth/login

**Descripción**: Autentica un usuario y retorna tokens JWT.

**Request**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response** (200 OK):
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

**Caso de Uso**: `LoginUseCase`
- Valida credenciales
- Genera access token y refresh token
- Persiste refresh token

#### POST /auth/refresh

**Descripción**: Renueva el access token usando un refresh token válido.

**Request**:
```json
{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response** (200 OK):
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

**Caso de Uso**: `RefreshTokenUseCase`
- Valida refresh token
- Genera nuevo access token

### Endpoints Protegidos

#### GET /auth/me

**Descripción**: Obtiene información del usuario autenticado.

**Headers**:
```
Authorization: Bearer <access_token>
```

**Response** (200 OK):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "roles": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "name": "USER",
      "authority": "ROLE_USER"
    }
  ],
  "status": "ACTIVE"
}
```

**Caso de Uso**: `GetCurrentUserUseCase`
- Extrae userId del token (SecurityContext)
- Busca y retorna usuario

#### POST /auth/logout

**Descripción**: Cierra sesión invalidando el refresh token.

**Headers**:
```
Authorization: Bearer <access_token>
```

**Request**:
```json
{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response** (204 No Content)

**Caso de Uso**: `LogoutUseCase`
- Revoca refresh token

#### PUT /auth/change-password

**Descripción**: Cambia la contraseña del usuario autenticado.

**Headers**:
```
Authorization: Bearer <access_token>
```

**Request**:
```json
{
  "currentPassword": "oldpassword123",
  "newPassword": "newpassword456"
}
```

**Response** (204 No Content)

**Caso de Uso**: `ChangePasswordUseCase`
- Verifica contraseña actual
- Hashea nueva contraseña
- Actualiza usuario

#### PUT /users/{userId}/roles

**Descripción**: Asigna un rol a un usuario (requiere ADMIN).

**Headers**:
```
Authorization: Bearer <admin_access_token>
```

**Request**:
```json
{
  "roleName": "ADMIN"
}
```

**Response** (200 OK):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "roles": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "name": "USER",
      "authority": "ROLE_USER"
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440002",
      "name": "ADMIN",
      "authority": "ROLE_ADMIN"
    }
  ],
  "status": "ACTIVE"
}
```

**Caso de Uso**: `AssignRoleUseCase`
- Busca rol por nombre o authority
- Asigna rol al usuario

---

## Configuración

### Variables de Entorno

#### Desarrollo (`application-dev.yml`)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/security_app_dev
    username: root
    password: root
  
  jpa:
    hibernate:
      ddl-auto: update

jwt:
  secret: ${JWT_SECRET:default-secret-key}
  access-token-expiration: 900000    # 15 minutos
  refresh-token-expiration: 604800000 # 7 días
```

#### Producción (`application-prod.yml`)

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  
  jpa:
    hibernate:
      ddl-auto: validate  # NUNCA usar 'create' o 'update'

jwt:
  secret: ${JWT_SECRET}  # OBLIGATORIO en producción
  access-token-expiration: ${JWT_ACCESS_TOKEN_EXPIRATION:900000}
  refresh-token-expiration: ${JWT_REFRESH_TOKEN_EXPIRATION:604800000}
```

### Propiedades JWT

| Propiedad | Descripción | Valor por Defecto | Producción |
|-----------|-------------|-------------------|------------|
| `jwt.secret` | Clave secreta para firmar tokens | Valor por defecto | **OBLIGATORIO** |
| `jwt.access-token-expiration` | Expiración access token (ms) | 900000 (15 min) | Configurable |
| `jwt.refresh-token-expiration` | Expiración refresh token (ms) | 604800000 (7 días) | Configurable |

### Generar Secreto JWT Seguro

```bash
# Opción 1: Usando OpenSSL
openssl rand -base64 64

# Opción 2: Usando Java
java -cp . -c "System.out.println(javax.crypto.KeyGenerator.getInstance(\"HmacSHA256\").generateKey())"

# Opción 3: Usando Python
python -c "import secrets; print(secrets.token_hex(32))"
```

---

## Seguridad

### Medidas Implementadas

1. **Contraseñas**:
   - Hash con BCrypt (salt automático)
   - Nunca se almacenan en texto plano
   - Verificación segura con `matches()`

2. **Access Tokens**:
   - Firma HMAC SHA-256
   - Expiración corta (15 minutos)
   - Claims mínimos necesarios
   - Validación de firma y expiración

3. **Refresh Tokens**:
   - UUID aleatorio (no predecible)
   - Persistido en BD (revocable)
   - Expiración configurable (7 días)
   - Validación contra BD

4. **Spring Security**:
   - Stateless (sin sesiones)
   - CSRF deshabilitado (APIs REST)
   - CORS configurado
   - Filtro JWT personalizado

5. **Validaciones**:
   - Email único
   - Contraseña mínima 6 caracteres
   - Usuario activo para login
   - Refresh token válido para refresh

### Buenas Prácticas Aplicadas

✅ **OWASP Top 10**:
- Contraseñas hasheadas (A02:2021 - Cryptographic Failures)
- Validación de entrada (A03:2021 - Injection)
- Autenticación segura (A07:2021 - Identification and Authentication Failures)
- Tokens con expiración (A01:2021 - Broken Access Control)

✅ **Principios SOLID**:
- Single Responsibility: Cada clase tiene una responsabilidad
- Dependency Inversion: Dependencias de interfaces (puertos)
- Open/Closed: Extensible sin modificar código existente

✅ **Arquitectura Hexagonal**:
- Dominio independiente de frameworks
- Fácil cambio de implementaciones
- Testeable

---

## Diagramas de Flujo

### Flujo Completo de Autenticación

```
┌─────────┐
│ Cliente │
└────┬────┘
     │
     │ 1. POST /auth/register
     ▼
┌─────────────────┐
│ AuthController  │
└────┬────────────┘
     │
     │ 2. CreateUserUseCase
     ▼
┌─────────────────┐      ┌──────────────┐
│ UserRepository  │──────▶│  MySQL BD    │
└─────────────────┘      └──────────────┘
     │
     │ 3. Retorna UserResponse
     ▼
┌─────────┐
│ Cliente │
└────┬────┘
     │
     │ 4. POST /auth/login
     ▼
┌─────────────────┐
│ LoginUseCase    │
└────┬────────────┘
     │
     ├─▶ Verifica contraseña (BCrypt)
     ├─▶ Genera Access Token (JWT)
     ├─▶ Genera Refresh Token (UUID)
     └─▶ Persiste RefreshToken
     │
     │ 5. Retorna tokens
     ▼
┌─────────┐
│ Cliente │
└────┬────┘
     │
     │ 6. GET /auth/me
     │    Header: Authorization: Bearer <token>
     ▼
┌─────────────────────────┐
│ JwtAuthenticationFilter │
└────┬────────────────────┘
     │
     ├─▶ Extrae token
     ├─▶ Valida token
     ├─▶ Extrae userId y roles
     └─▶ Establece SecurityContext
     │
     ▼
┌─────────────────┐
│ AuthController  │
└────┬────────────┘
     │
     │ 7. GetCurrentUserUseCase
     ▼
┌─────────────────┐      ┌──────────────┐
│ UserRepository  │──────▶│  MySQL BD    │
└─────────────────┘      └──────────────┘
     │
     │ 8. Retorna UserResponse
     ▼
┌─────────┐
│ Cliente │
└─────────┘
```

### Flujo de Refresh Token

```
┌─────────┐
│ Cliente │
└────┬────┘
     │
     │ POST /auth/refresh
     │ {refreshToken: "uuid"}
     ▼
┌──────────────────────┐
│ RefreshTokenUseCase  │
└────┬─────────────────┘
     │
     ├─▶ Busca RefreshToken en BD
     ├─▶ Verifica válido (no revocado, no expirado)
     ├─▶ Busca User asociado
     ├─▶ Verifica usuario activo
     └─▶ Genera nuevo Access Token
     │
     │ Retorna nuevo Access Token
     ▼
┌─────────┐
│ Cliente │
└─────────┘
```

### Flujo de Logout

```
┌─────────┐
│ Cliente │
└────┬────┘
     │
     │ POST /auth/logout
     │ Header: Authorization: Bearer <token>
     │ Body: {refreshToken: "uuid"}
     ▼
┌─────────────────┐
│ LogoutUseCase   │
└────┬────────────┘
     │
     ├─▶ Busca RefreshToken
     ├─▶ Marca revoked = true
     └─▶ Guarda cambios
     │
     │ Retorna 204 No Content
     ▼
┌─────────┐
│ Cliente │
└─────────┘
```

---

## Ejemplos de Uso

### 1. Registro y Login Completo

```bash
# 1. Registrar usuario
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'

# Respuesta:
# {
#   "id": "550e8400-e29b-41d4-a716-446655440000",
#   "email": "test@example.com",
#   "roles": [{"id": "...", "name": "USER", "authority": "ROLE_USER"}],
#   "status": "ACTIVE"
# }

# 2. Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'

# Respuesta:
# {
#   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
#   "tokenType": "Bearer",
#   "expiresIn": 900
# }

# 3. Obtener usuario autenticado
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# 4. Refrescar token
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
  }'

# 5. Logout
curl -X POST http://localhost:8080/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

### 2. Cambio de Contraseña

```bash
curl -X PUT http://localhost:8080/auth/change-password \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "password123",
    "newPassword": "newpassword456"
  }'
```

### 3. Asignar Rol (Requiere ADMIN)

```bash
# Primero, obtener token de admin
# Luego:
curl -X PUT http://localhost:8080/users/{userId}/roles \
  -H "Authorization: Bearer <admin_access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "roleName": "ADMIN"
  }'
```

---

## Troubleshooting

### Problemas Comunes

#### 1. Error 401 Unauthorized

**Causas**:
- Token expirado
- Token inválido o malformado
- Header Authorization incorrecto
- Refresh token revocado o expirado

**Solución**:
- Verificar formato del header: `Authorization: Bearer <token>`
- Verificar que el token no haya expirado
- Usar `/auth/refresh` para obtener nuevo token
- Verificar que el refresh token no esté revocado

#### 2. Error 403 Forbidden

**Causas**:
- Usuario no tiene el rol requerido
- Endpoint requiere ADMIN pero usuario es USER

**Solución**:
- Verificar roles del usuario con `/auth/me`
- Asignar rol necesario (requiere ADMIN)

#### 3. Error 500 al cargar Swagger

**Causas**:
- Problemas de serialización de DTOs
- Configuración incorrecta de SpringDoc

**Solución**:
- Verificar que todos los DTOs sean serializables
- Verificar configuración en `application.yml`
- Revisar logs de la aplicación

#### 4. Token no se valida correctamente

**Causas**:
- Secreto JWT diferente entre generación y validación
- Token corrupto o modificado
- Problema con el formato del token

**Solución**:
- Verificar que `JWT_SECRET` sea el mismo en toda la aplicación
- Verificar formato del token (debe ser JWT válido)
- Revisar logs del `JwtAuthenticationFilter`

#### 5. Refresh Token no encontrado

**Causas**:
- Token no existe en BD
- Token fue revocado
- Token expirado y eliminado

**Solución**:
- Verificar que el token exista en la tabla `refresh_tokens`
- Verificar que `revoked = false`
- Verificar que `expires_at` no haya pasado

---

## Estructura de Base de Datos

### Tabla: users

```sql
CREATE TABLE users (
    id CHAR(36) PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL
);
```

### Tabla: roles

```sql
CREATE TABLE roles (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    authority VARCHAR(50) UNIQUE NOT NULL
);
```

### Tabla: user_roles (Many-to-Many)

```sql
CREATE TABLE user_roles (
    user_id CHAR(36) NOT NULL,
    role_id CHAR(36) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);
```

### Tabla: refresh_tokens

```sql
CREATE TABLE refresh_tokens (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    token VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## Extensiones Futuras

### Posibles Mejoras

1. **OAuth2 / Keycloak**:
   - Implementar nuevos adaptadores en `infrastructure/security`
   - Mantener los mismos puertos del dominio

2. **Rate Limiting**:
   - Agregar límites de intentos de login
   - Protección contra fuerza bruta

3. **Two-Factor Authentication (2FA)**:
   - Agregar TOTP (Time-based One-Time Password)
   - SMS o Email verification

4. **Token Blacklist**:
   - Lista negra de tokens revocados
   - Útil para logout inmediato de access tokens

5. **Auditoría**:
   - Log de intentos de login
   - Historial de cambios de contraseña
   - Tracking de asignación de roles

---

## Referencias

- [JWT.io](https://jwt.io/) - Documentación oficial de JWT
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/index.html)
- [JJWT Documentation](https://github.com/jwtk/jjwt)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Arquitectura Hexagonal](https://alistair.cockburn.us/hexagonal-architecture/)

---

**Última actualización**: Enero 2026  
**Versión del Sistema**: 1.0.0  
**Spring Boot**: 3.3.0  
**Spring Security**: 6.x
