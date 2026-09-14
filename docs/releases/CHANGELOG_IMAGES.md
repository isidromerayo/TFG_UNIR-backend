# Changelog - Imágenes Docker

## [1.1] - PostgreSQL - 2026-09-14

### 🚀 Publicación

#### Added
- Imagen `isidromerayo/postgres-tfg:1.1` construida con el `Dockerfile-db-postgresql` actual

#### Security
- ✅ Elimina la `POSTGRES_PASSWORD` embebida que contenía la imagen `1.0` (visible en `docker history`)
- La contraseña se pasa exclusivamente en runtime (`docker-compose.yml` / `podman-pod.sh`)

#### Notes
- La imagen no contiene datos: el seed se monta en runtime desde `recursos/db/postgresql/`
- `docker-compose.yml`, `podman-pod.sh` y `DOCKER_IMAGES_GUIDE.md` actualizados a `1.1`

---

## [0.4.0] - Backend - 2026-01-17

### 🚀 Actualización Major (Framework)

#### Changed
- **Spring Boot 3.5.0**: Actualización a la última versión estable del framework.
- **Base Image**: Eclipse Temurin 21 (LTS) mantenida.

#### Security
- Actualizaciones de seguridad heredadas de Spring Boot 3.5.
- Dependencias transitivas actualizadas.

### 📦 Dependencias
- Spring Boot 3.5.0
- Java 21

### 🔗 Compatibilidad
- Compatible con MariaDB v0.1.0+
- **Nota**: Requiere re-despliegue completo si se actualiza desde versiones < 0.3.0 debido a cambios en BBDD anteriores.

---

## [0.3.1] - Backend - 2024-12-09

### 📄 Documentación

#### Added
- **Página de inicio**: Información del proyecto en `/`
  - Versión del backend
  - Información de Git (branch, commit ID)
  - Fecha de compilación en formato castellano
  - Descripción del proyecto
  - Modo oscuro con toggle (🌓)
  - Accesibilidad WCAG 2.1 AA completa

#### Changed
- Configuración de Spring Boot Actuator para exponer información
- Plugin `git-commit-id-maven-plugin` agregado para capturar metadata de Git

#### Accessibility
- ✅ Contraste optimizado para ambos modos (claro/oscuro)
- ✅ Navegación por teclado completa
- ✅ Atributos ARIA para lectores de pantalla
- ✅ Skip link para contenido principal
- ✅ Semántica HTML5 correcta

### 📦 Dependencias
- Spring Boot 3.4.12
- Spring Security con BCrypt
- MariaDB JDBC Driver 3.5.6
- Java 21

### 🔗 Compatibilidad
- Compatible con MariaDB v0.1.0+
- Compatible con frontends Angular, React, Vue

---

## [0.3.0] - Backend - 2024-12-09

### 🔐 Seguridad

#### Added
- **BCrypt Password Hashing**: Implementación completa de BCrypt para hashing de contraseñas
  - `SecurityConfig.java`: Bean de `BCryptPasswordEncoder` con 10 rounds
  - `LoginController.java`: Validación de contraseñas con comparación constant-time
  - Prevención de timing attacks en autenticación

#### Changed
- Endpoint de autenticación actualizado a `/api/auth`
- Validación de estado de usuario (activo/inactivo)
- Respuestas de error mejoradas

#### Security
- ✅ Contraseñas nunca se almacenan en texto plano
- ✅ Comparación constant-time previene timing attacks
- ✅ Salt automático único por contraseña
- ✅ Work factor configurable (10 rounds por defecto)

### 📦 Dependencias
- Spring Boot 3.4.12
- Spring Security con BCrypt
- MariaDB JDBC Driver 3.5.6
- Java 21

### 🔗 Compatibilidad
- Requiere MariaDB con contraseñas hasheadas (v0.1.0+)
- Compatible con frontends Angular, React, Vue

---

## [0.1.0] - MariaDB - 2024-12-09

### 🔐 Seguridad

#### Added
- **Contraseñas BCrypt**: Todos los usuarios de prueba tienen contraseñas hasheadas
  - 14 usuarios con hashes BCrypt (strength 10)
  - Scripts de inicialización actualizados
  - Verificación de hashes implementada

#### Changed
- `dump.mariadb.sql`: Contraseñas actualizadas de texto plano a BCrypt
- `create.mariadb.sql`: Sin cambios (mantiene compatibilidad)

#### Security
- ✅ Eliminadas todas las contraseñas en texto plano
- ✅ Hashes BCrypt con salt automático
- ✅ Compatible con backend v0.3.0+

### 📊 Datos de Prueba

**Usuarios activos (Estado: A):**
- helena@localhost / 1234
- carlos@localhost / 1234
- ines@localhost / 1234
- isabel@localhost / 1234
- carla@localhost / 1234

**Usuarios pendientes (Estado: P):**
- maria@localhost / 1234
- juanantonio@localhost / 1234
- marta@localhost / 1234
- pedro@localhost / 1234
- diego@localhost / 1234
- clara@localhost / 1234
- marta@localhost / 1234
- krista@localhost / 1234
- jevon@localhost / TFG_1234

### 🔗 Compatibilidad
- Requiere backend con BCrypt (v0.3.0+)
- MariaDB 12.1.2+

---

## Versiones Anteriores

### [0.2.2] - Backend - 2024-XX-XX
- Versión anterior sin BCrypt
- ⚠️ Contraseñas en texto plano (INSEGURO)
- ⚠️ Vulnerable a timing attacks

### [0.0.4] - MariaDB - 2024-XX-XX
- Versión anterior sin BCrypt
- ⚠️ Contraseñas en texto plano (INSEGURO)

---

## Migración desde Versiones Anteriores

### De Backend 0.2.2 → 0.3.0

**Cambios requeridos:**
1. Actualizar imagen en `docker-compose.yml`:
   ```yaml
   image: "isidromerayo/spring-backend-tfg:0.3.0"
   ```

2. Actualizar MariaDB a v0.1.0 (con contraseñas BCrypt)

3. Eliminar volúmenes antiguos:
   ```bash
   docker compose down -v
   docker compose up -d
   ```

**Breaking Changes:**
- Las contraseñas en texto plano ya no funcionan
- Requiere contraseñas hasheadas con BCrypt en la base de datos

### De MariaDB 0.0.4 → 0.1.0

**Cambios requeridos:**
1. Actualizar imagen en `docker-compose.yml`:
   ```yaml
   image: "isidromerayo/mariadb-tfg:0.1.0"
   ```

2. Eliminar volumen antiguo (datos en texto plano):
   ```bash
   docker compose down -v
   ```

3. Iniciar con nueva imagen:
   ```bash
   docker compose up -d
   ```

**Breaking Changes:**
- Los datos antiguos (contraseñas en texto plano) no son compatibles
- Requiere backend v0.3.0+ con BCrypt

---

## Uso de las Nuevas Versiones

### Docker Compose

```yaml
version: '3.8'

services:
  maria_db:
    image: "isidromerayo/mariadb-tfg:0.1.0"
    ports:
      - "3306:3306"
    volumes:
      - maria-db-data:/var/lib/mysql

  api_service:
    image: "isidromerayo/spring-backend-tfg:0.3.0"
    ports:
      - "8080:8080"
    depends_on:
      - maria_db
    environment:
      SPRING_DATASOURCE_URL: "jdbc:mariadb://maria_db:3306/tfg_unir"
      SPRING_DATASOURCE_USERNAME: "user_tfg"
      SPRING_DATASOURCE_PASSWORD: "tfg_un1r_PWD"

volumes:
  maria-db-data:
```

### Docker CLI

```bash
# Pull de imágenes
docker pull isidromerayo/mariadb-tfg:0.1.0
docker pull isidromerayo/spring-backend-tfg:0.3.0

# Ejecutar MariaDB
docker run -d --name maria_db \
  -p 3306:3306 \
  isidromerayo/mariadb-tfg:0.1.0

# Ejecutar Backend
docker run -d --name api_service \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mariadb://maria_db:3306/tfg_unir \
  --link maria_db \
  isidromerayo/spring-backend-tfg:0.3.0
```

### Podman

```bash
# Pull de imágenes
podman pull docker.io/isidromerayo/mariadb-tfg:0.1.0
podman pull docker.io/isidromerayo/spring-backend-tfg:0.3.0

# Usar script de Podman Pod
./scripts/podman-pod.sh start
```

---

## Verificación

### Verificar Versiones

```bash
# Verificar imagen de MariaDB
docker inspect isidromerayo/mariadb-tfg:0.1.0 | grep Created

# Verificar imagen de Backend
docker inspect isidromerayo/spring-backend-tfg:0.3.0 | grep Created
```

### Verificar Contraseñas BCrypt

```bash
# Conectar a MariaDB
docker exec -it maria_db mariadb -u user_tfg -ptfg_un1r_PWD tfg_unir

# Verificar hashes
SELECT id, nombre, LEFT(password, 30) FROM usuarios LIMIT 5;
```

Resultado esperado:
```
+----+---------------+--------------------------------+
| id | nombre        | LEFT(password, 30)             |
+----+---------------+--------------------------------+
|  1 | María         | $2b$10$JKheLVrM5.jvtYVvd.tfqOL |
|  2 | Juan Antonio  | $2b$10$JKheLVrM5.jvtYVvd.tfqOL |
+----+---------------+--------------------------------+
```

### Probar Login

```bash
# Con script de test
./scripts/test-login.sh helena@localhost 1234

# Con curl
curl -X POST http://localhost:8080/api/auth \
  -H "Content-Type: application/json" \
  -d '{"email":"helena@localhost","password":"1234"}'
```

---

## Notas de Seguridad

### ⚠️ Solo para Desarrollo

Las contraseñas de prueba (`1234`, `TFG_1234`) son débiles y solo para desarrollo.

**En producción:**
- Usar contraseñas fuertes y únicas
- Implementar políticas de contraseñas
- Considerar autenticación de dos factores
- Rotar credenciales regularmente

### ✅ Mejoras de Seguridad

Estas versiones implementan:
- BCrypt con 10 rounds (2^10 = 1024 iteraciones)
- Salt automático único por contraseña
- Comparación constant-time (previene timing attacks)
- Validación de estado de usuario

---

## Soporte

### Documentación
- [SECURITY_BCRYPT.md](SECURITY_BCRYPT.md) - Guía de seguridad
- [docs/security/](docs/security/) - Documentación completa
- [docs/PODMAN_GUIDE.md](docs/PODMAN_GUIDE.md) - Guía de Podman

### Issues
- Reportar problemas en GitHub Issues
- Incluir versiones de imágenes usadas
- Proporcionar logs relevantes

---

## Licencia

Mismo que el proyecto principal.

---

## Autores

- Isidro Merayo - Implementación de BCrypt y migración
