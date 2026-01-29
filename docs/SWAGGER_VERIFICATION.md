# Verificación de Configuración de Swagger/OpenAPI

## ✅ Checklist de Configuración

### 1. Dependencia en pom.xml
- ✅ `springdoc-openapi-starter-webmvc-ui` versión 2.3.0

### 2. Configuración en application.yml
- ✅ `springdoc.api-docs.path: /v3/api-docs`
- ✅ `springdoc.api-docs.enabled: true`
- ✅ `springdoc.swagger-ui.path: /swagger-ui.html`
- ✅ `springdoc.swagger-ui.enabled: true`

### 3. Configuración de Seguridad
- ✅ Rutas públicas en SecurityConfig:
  - `/swagger-ui/**`
  - `/swagger-ui.html`
  - `/v3/api-docs/**`
  - `/v3/api-docs.yaml`
  - `/v3/api-docs.yml`
  - `/swagger-resources/**`
  - `/webjars/**`
  - `/configuration/**`

### 4. Configuración OpenAPI
- ✅ Clase `OpenApiConfig` con `@Configuration`
- ✅ Bean `OpenAPI` configurado
- ✅ Esquema de seguridad JWT Bearer Token

### 5. Anotaciones en Controladores
- ✅ `@Tag` en controladores
- ✅ `@Operation` en métodos
- ✅ `@ApiResponses` en métodos
- ✅ `@SecurityRequirement` en endpoints protegidos

## 🔍 URLs para Verificar

Una vez que la aplicación esté ejecutándose:

1. **Swagger UI**: `http://localhost:8080/swagger-ui.html`
   - O también: `http://localhost:8080/swagger-ui/index.html`

2. **API Docs JSON**: `http://localhost:8080/v3/api-docs`

3. **API Docs YAML**: `http://localhost:8080/v3/api-docs.yaml`

## 🐛 Troubleshooting

### Si Swagger UI no carga:

1. **Verificar que la aplicación esté ejecutándose**:
   ```bash
   mvn spring-boot:run
   ```

2. **Verificar logs de inicio**:
   Buscar mensajes como:
   - "Mapped "{[/v3/api-docs]}" 
   - "Mapped "{[/swagger-ui/**]}"

3. **Verificar dependencias**:
   ```bash
   mvn dependency:tree | grep springdoc
   ```

4. **Verificar configuración de seguridad**:
   Asegurarse de que las rutas de Swagger estén en `permitAll()`

5. **Limpiar y recompilar**:
   ```bash
   mvn clean compile
   ```

### Si aparece página en blanco:

- Verificar la consola del navegador para errores JavaScript
- Verificar que el puerto sea correcto (por defecto 8080)
- Intentar acceder directamente a `/v3/api-docs` para verificar que la API está disponible

### Si los endpoints no aparecen:

- Verificar que los controladores tengan `@RestController`
- Verificar que los métodos tengan `@GetMapping`, `@PostMapping`, etc.
- Verificar que no haya errores de compilación

## 📝 Notas

- En **producción**, Swagger está deshabilitado por defecto en `application-prod.yml`
- Para habilitar en producción, cambiar `springdoc.swagger-ui.enabled: true` en `application-prod.yml`
- La configuración usa SpringDoc OpenAPI 2.3.0, compatible con Spring Boot 3.3.0
