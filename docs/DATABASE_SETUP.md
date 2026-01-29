# Configuración de Base de Datos MySQL

## Requisitos Previos

- MySQL 8.0 o superior instalado y ejecutándose
- Usuario de MySQL con permisos para crear bases de datos

## Configuración por Perfiles

### Desarrollo (application-dev.yml)

Por defecto, el proyecto usa el perfil `dev`. Configura las siguientes variables de entorno o edita `application-dev.yml`:

```bash
export DB_URL=jdbc:mysql://localhost:3306/security_app_dev?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
export DB_USERNAME=root
export DB_PASSWORD=tu_password
```

O ejecuta con parámetros:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.datasource.url=jdbc:mysql://localhost:3306/security_app_dev --spring.datasource.username=root --spring.datasource.password=tu_password"
```

### Producción (application-prod.yml)

Para producción, configura las variables de entorno:

```bash
export SPRING_PROFILES_ACTIVE=prod
export DB_URL=jdbc:mysql://tu-servidor:3306/security_app_prod?useSSL=true&requireSSL=true&serverTimezone=UTC
export DB_USERNAME=usuario_prod
export DB_PASSWORD=password_seguro_prod
export JWT_SECRET=tu-secreto-super-seguro-aqui
export SERVER_PORT=8080
```

## Crear Base de Datos Manualmente

Si prefieres crear la base de datos manualmente:

```sql
CREATE DATABASE security_app_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE security_app_prod CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear usuario (opcional)
CREATE USER 'security_user'@'localhost' IDENTIFIED BY 'password_seguro';
GRANT ALL PRIVILEGES ON security_app_dev.* TO 'security_user'@'localhost';
GRANT ALL PRIVILEGES ON security_app_prod.* TO 'security_user'@'localhost';
FLUSH PRIVILEGES;
```

## Ejecutar con Perfil Específico

### Desarrollo
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Producción
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

O usando variables de entorno:
```bash
SPRING_PROFILES_ACTIVE=prod mvn spring-boot:run
```

## Migraciones de Base de Datos

En desarrollo, Hibernate crea/actualiza automáticamente el esquema (`ddl-auto: update`).

En producción, se recomienda usar herramientas como:
- **Flyway** (recomendado)
- **Liquibase**
- Scripts SQL manuales

**IMPORTANTE**: En producción, `ddl-auto` debe ser `validate` o `none` para evitar pérdida de datos.

## Troubleshooting

### Error de conexión
- Verifica que MySQL esté ejecutándose: `mysqladmin -u root -p status`
- Verifica las credenciales en las variables de entorno
- Verifica que el puerto 3306 esté accesible

### Error de timezone
- Asegúrate de incluir `serverTimezone=UTC` en la URL de conexión
- O configura el timezone de MySQL: `SET GLOBAL time_zone = '+00:00';`

### Error de SSL
- En desarrollo: usa `useSSL=false` en la URL
- En producción: configura certificados SSL correctamente
