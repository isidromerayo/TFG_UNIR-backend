# Scripts del Backend

Scripts de utilidad para el backend TFG UNIR.

## Scripts Disponibles

### 🐳 Publicación de Imágenes Docker

#### `publish-images.sh`
Script para construir y publicar la imagen del backend en Docker Hub. Solo publica versiones que **no** sean SNAPSHOT.

**Uso:**
```bash
# Publicar con versión extraída del pom.xml
./scripts/publish-images.sh

# Solo mostrar qué haría (sin ejecutar)
./scripts/publish-images.sh --dry-run

# Especificar versión manualmente
./scripts/publish-images.sh --version 0.6.2

# Omitir login en Docker Hub
./scripts/publish-images.sh --skip-login
```

**Flags:**
| Flag | Descripción |
|------|-------------|
| `--version X.Y.Z` | Sobrescribe la versión detectada del pom.xml |
| `--dry-run` | Muestra los comandos sin ejecutarlos |
| `--skip-login` | Omite la verificación de login en Docker Hub |

**Qué hace:**
1. Extrae la versión del `pom.xml` (valida formato semver)
2. Rechaza versiones SNAPSHOT
3. Construye la imagen del backend con `--build-arg VERSION`
4. Etiqueta con `:version` y `:latest`
5. Publica en Docker Hub

**Requisitos:**
- Backend compilado (`./mvnw clean package`)
- Autenticado en Docker Hub (`docker login` o `podman login`)

#### `publish-db-image.sh`
Script para construir y publicar la imagen de PostgreSQL en Docker Hub.

**Uso:**
```bash
# Publicar con versión por defecto (1.0)
POSTGRES_PASSWORD=<tu_password> ./scripts/publish-db-image.sh

# Versión específica
POSTGRES_PASSWORD=<tu_password> ./scripts/publish-db-image.sh 1.1

# Solo mostrar qué haría
POSTGRES_PASSWORD=<tu_password> ./scripts/publish-db-image.sh --dry-run
```

**Variables de entorno requeridas:**
| Variable | Descripción | Default |
|----------|-------------|---------|
| `POSTGRES_PASSWORD` | Contraseña de PostgreSQL (**obligatoria**) | - |
| `POSTGRES_DB` | Nombre de la base de datos | `tfg_unir` |
| `POSTGRES_USER` | Usuario de PostgreSQL | `user_tfg` |

**Qué hace:**
1. Valida que `POSTGRES_PASSWORD` esté definido
2. Construye la imagen de PostgreSQL con credenciales como `--build-arg`
3. Etiqueta con `:version` y `:latest`
4. Publica en Docker Hub

> **⚠️ Seguridad**: Las credenciales **nunca** se hardcodean en el Dockerfile. Se pasan como variables de entorno y `--build-arg` en tiempo de build.

#### `build-and-test-bcrypt.sh`
Script automatizado completo para construir y probar la migración a BCrypt.

**Uso:**
```bash
./scripts/build-and-test-bcrypt.sh
```

**Qué hace:**
1. Verifica contraseñas BCrypt en dump.mariadb.sql
2. Construye nueva imagen Docker (isidromerayo/mariadb-tfg:0.0.5-bcrypt)
3. Reinicia contenedores con volúmenes limpios
4. Verifica contraseñas hasheadas en la base de datos
5. Espera a que el backend esté listo
6. Prueba autenticación con múltiples usuarios
7. Valida rechazo de credenciales incorrectas
8. Muestra resumen de resultados

**Requisitos:**
- Docker o Podman
- curl
- jq (opcional, para formato JSON)

#### `test-login.sh`
Script simple para probar el login con diferentes usuarios.

**Uso:**
```bash
# Probar todos los usuarios de prueba
./scripts/test-login.sh

# Probar un usuario específico
./scripts/test-login.sh maria@localhost 1234
```

**Usuarios de prueba:**
- maria@localhost / 1234
- helena@localhost / 1234
- carlos@localhost / 1234
- ines@localhost / 1234
- Alva_Streich@example.net / TFG_1234

### 🐳 Contenedores

#### `podman-pod.sh`
Script para gestionar contenedores con Podman Pod (alternativa a docker-compose).

⚠️ **IMPORTANTE**: Si usas Podman, usa este script en lugar de `podman-compose` debido a problemas de DNS.

**Uso:**
```bash
# Iniciar pod
./scripts/podman-pod.sh start

# Detener pod
./scripts/podman-pod.sh stop

# Reiniciar pod
./scripts/podman-pod.sh restart

# Ver estado
./scripts/podman-pod.sh status

# Ver logs del backend
./scripts/podman-pod.sh logs
./scripts/podman-pod.sh logs api

# Ver logs de MariaDB
./scripts/podman-pod.sh logs db
```

**Ventajas sobre podman-compose:**
- ✅ Mejor resolución DNS entre contenedores (usan localhost)
- ✅ Más estable en sistemas Linux
- ✅ Similar a Pods de Kubernetes
- ✅ No requiere daemon

**Documentación completa**: Ver [docs/PODMAN_GUIDE.md](../docs/PODMAN_GUIDE.md)

## Estructura de Directorios

```
scripts/
├── README.md                    # Este archivo
├── publish-images.sh            # Publicar imagen del backend
├── publish-db-image.sh          # Publicar imagen de PostgreSQL
├── build-and-test-bcrypt.sh     # Build y test BCrypt
├── test-login.sh                # Pruebas de login
└── podman-pod.sh                # Gestión Podman Pod
```

## Quick Start

### Publicar imágenes del backend

```bash
# 1. Compilar
./mvnw clean package -DskipTests

# 2. Verificar qué se publicaría
./scripts/publish-images.sh --dry-run

# 3. Publicar
./scripts/publish-images.sh
```

### Publicar imagen de PostgreSQL

```bash
# Requiere POSTGRES_PASSWORD como variable de entorno
POSTGRES_PASSWORD=mi_password_secreto ./scripts/publish-db-image.sh 1.0
```

### Probar login

```bash
./scripts/test-login.sh maria@localhost 1234
```

## Documentación

Para más información, consulta:

- **Release Flow**: Ver `AGENTS.md` sección "Release Flow (Docker images)"
- **Guía Docker**: `docs/docker/DOCKER_IMAGES_GUIDE.md`
- **Quick Start BCrypt**: `docs/security/QUICK_START_BCRYPT.md`

## Troubleshooting

### publish-db-image.sh falla con "POSTGRES_PASSWORD no está definido"

Define la variable de entorno antes de ejecutar:
```bash
POSTGRES_PASSWORD=tu_password ./scripts/publish-db-image.sh
```

### publish-images.sh falla con "No se publican imágenes SNAPSHOT"

La versión detectada del `pom.xml` contiene `-SNAPSHOT`. Ejecuta primero:
```bash
mvn release:prepare
```

### Script falla con "Backend no compilado"

Compila el backend primero:
```bash
./mvnw clean package -DskipTests
```

### Usando Podman en lugar de Docker

Si usas Podman y tienes problemas con DNS (`UnknownHostException`):

```bash
# Usa el script de Podman Pod en lugar de podman-compose
./scripts/podman-pod.sh start
```

## Contribuir

Al agregar nuevos scripts:

1. Hazlos ejecutables: `chmod +x scripts/nuevo-script.sh`
2. Agrega documentación en este README
3. Incluye comentarios en el script
4. Maneja errores apropiadamente (`set -e`)
5. Usa colores para output (ver ejemplos existentes)
