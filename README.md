# SecurityApp

Aplicación de autenticación y autorización construida con Spring Boot 3.3.0 y Spring Security 6, siguiendo **Arquitectura Hexagonal** y **Domain-Driven Design (DDD)**.

## 🏗️ Arquitectura

El proyecto está estructurado siguiendo los principios de Arquitectura Hexagonal:

```
src/main/java/com/bkseducate/securityapp/
├── domain/                    # Capa de dominio (sin dependencias externas)
│   ├── model/                # Entidades de dominio (User, Role, RefreshToken)
│   ├── ports/                # Interfaces (puertos) del dominio
│   └── exceptions/           # Excepciones de dominio
├── application/              # Capa de aplicación
│   ├── usecase/             # Casos de uso
│   ├── dto/                  # Data Transfer Objects
│   └── mapper/               # Mappers MapStruct
├── infrastructure/          # Capa de infraestructura
│   ├── security/            # JWT, Password, SecurityConfig
│   ├── persistence/         # Entidades JPA, Repositorios
│   └── exception/           # Manejo global de excepciones
└── adapters/                # Adaptadores
    ├── in/                  # Controladores REST (adaptadores de entrada)
    └── out/                 # Adaptadores de persistencia (adaptadores de salida)
```

## 🚀 Características

- ✅ Autenticación con JWT (Access Token + Refresh Token)
- ✅ Registro de usuarios con asignación automática de ROL_USER
- ✅ Refresh Token persistido en base de datos
- ✅ Cambio de contraseña
- ✅ Asignación de roles (requiere ADMIN)
- ✅ Logout con invalidación de refresh token
- ✅ Manejo global de excepciones
- ✅ Validación de entrada con Jakarta Validation
- ✅ MapStruct para mapeos DTO ↔ Dominio
- ✅ Arquitectura Hexagonal pura (dominio sin dependencias de Spring)
- ✅ Documentación API con Swagger/OpenAPI (SpringDoc)

## 📋 Requisitos

- Java 17+
- Maven 3.6+
- MySQL 8.0+ (o superior)

## 🔧 Configuración

### Perfiles de Spring Boot

El proyecto usa perfiles de Spring Boot para diferentes entornos:

- **dev** (por defecto): Desarrollo con MySQL local
- **prod**: Producción con MySQL configurado

### Variables de entorno para Desarrollo

```bash
export SPRING_PROFILES_ACTIVE=dev
export DB_URL=jdbc:mysql://localhost:3306/security_app_dev?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
export DB_USERNAME=root
export DB_PASSWORD=tu_password
export JWT_SECRET=tu-secreto-super-seguro-aqui
```

### Variables de entorno para Producción

```bash
export SPRING_PROFILES_ACTIVE=prod
export DB_URL=jdbc:mysql://tu-servidor:3306/security_app_prod?useSSL=true&requireSSL=true&serverTimezone=UTC
export DB_USERNAME=usuario_prod
export DB_PASSWORD=password_seguro_prod
export JWT_SECRET=tu-secreto-super-seguro-aqui  # OBLIGATORIO en producción
export SERVER_PORT=8080
```

### Archivos de configuración

- `application.yml`: Configuración base común
- `application-dev.yml`: Configuración para desarrollo
- `application-prod.yml`: Configuración para producción
- `application-local.yml.example`: Plantilla para configuración local (copia y renombra)

### Propiedades principales

- `jwt.secret`: Secreto para firmar tokens JWT (obligatorio en producción)
- `jwt.access-token-expiration`: Expiración del access token (900000ms = 15 min)
- `jwt.refresh-token-expiration`: Expiración del refresh token (604800000ms = 7 días)
- `spring.datasource.*`: Configuración de conexión a MySQL

Ver `docs/DATABASE_SETUP.md` para más detalles sobre la configuración de la base de datos.

## 🏃 Ejecución

```bash
# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

## 📖 Documentación de la API

### Swagger UI

Una vez que la aplicación esté ejecutándose, accede a la documentación interactiva:

```
http://localhost:8080/swagger-ui.html
```

Desde Swagger UI puedes:
- Ver todos los endpoints disponibles
- Probar los endpoints directamente desde el navegador
- Ver los modelos de datos (DTOs)
- Autenticarte con JWT para probar endpoints protegidos

### Especificación OpenAPI

La especificación OpenAPI está disponible en:
- JSON: `http://localhost:8080/v3/api-docs`
- YAML: `http://localhost:8080/v3/api-docs.yaml`

Para más detalles sobre la configuración y uso de Swagger, consulta [`docs/SWAGGER_OPENAPI_SETUP.md`](docs/SWAGGER_OPENAPI_SETUP.md).

## 📡 Endpoints

### Públicos (sin autenticación)

- `POST /auth/register` - Registro de usuario
- `POST /auth/login` - Login (retorna access token y refresh token)
- `POST /auth/refresh` - Refrescar access token

### Protegidos (requieren autenticación)

- `GET /auth/me` - Obtener usuario autenticado
- `PUT /auth/change-password` - Cambiar contraseña
- `POST /auth/logout` - Cerrar sesión
- `PUT /users/{userId}/roles` - Asignar rol (requiere ADMIN)

### Autenticación

Incluir el token en el header:
```
Authorization: Bearer <access_token>
```

## 📝 Ejemplos de uso

### 1. Registrar usuario

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### 2. Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

Respuesta:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

### 3. Obtener usuario autenticado

```bash
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer <access_token>"
```

### 4. Refrescar token

```bash
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

### 5. Cambiar contraseña

```bash
curl -X PUT http://localhost:8080/auth/change-password \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "password123",
    "newPassword": "newpassword456"
  }'
```

### 6. Asignar rol (requiere ADMIN)

```bash
curl -X PUT http://localhost:8080/users/{userId}/roles \
  -H "Authorization: Bearer <admin_access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "role": "ADMIN"
  }'
```

## 🔐 Roles

- `ROLE_USER` - Asignado por defecto al crear usuario
- `ROLE_ADMIN` - Requerido para asignar roles
- `ROLE_MODERATOR` - Rol adicional disponible

## 🧪 Testing

```bash
mvn test
```

## 📦 Build

```bash
mvn clean package
```

El JAR ejecutable se generará en `target/security-app-1.0.0-SNAPSHOT.jar`

Ejecutar JAR:
```bash
java -jar target/security-app-1.0.0-SNAPSHOT.jar
```

## 🗄️ Base de datos

El proyecto usa **MySQL** como base de datos principal.

### Configuración rápida

1. Asegúrate de tener MySQL ejecutándose
2. Configura las variables de entorno (ver sección Configuración)
3. La aplicación creará automáticamente la base de datos si no existe (en desarrollo)

### Crear base de datos manualmente

```sql
CREATE DATABASE security_app_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE security_app_prod CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Para más detalles, consulta `docs/DATABASE_SETUP.md`.

## 🔄 Extensibilidad

El proyecto está diseñado para ser fácilmente extensible:

- **OAuth2/Keycloak**: Implementar nuevos adaptadores en `infrastructure/security`
- **Otras bases de datos**: Cambiar solo los adaptadores en `adapters/out/persistence`
- **Nuevos casos de uso**: Agregar en `application/usecase` sin modificar el dominio
- **Nuevos endpoints**: Agregar controladores en `adapters/in/rest`

## 📚 Principios aplicados

- **Arquitectura Hexagonal**: Separación clara entre dominio, aplicación e infraestructura
- **DDD**: Modelo de dominio rico con entidades, value objects y excepciones
- **SOLID**: Principios aplicados en toda la arquitectura
- **Clean Code**: Código limpio, documentado y mantenible
- **Security Best Practices**: JWT seguro, contraseñas hasheadas, tokens revocables

## 👤 Autor

Desarrollado siguiendo las mejores prácticas de Spring Boot y arquitectura de software.

## 📄 Licencia

Este proyecto es de uso educativo y demostrativo.
