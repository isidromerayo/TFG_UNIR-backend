# Guía de Imágenes Docker

## Introducción

Este proyecto proporciona imágenes Docker para el backend y la base de datos PostgreSQL:

1. **Descargar desde Docker Hub** (recomendado) - Imágenes pre-construidas
2. **Construir localmente** - Para desarrollo o personalización

### Release Flow

```bash
# 1. Preparar release (elimina -SNAPSHOT, crea tag vX.Y.Z)
mvn release:prepare

# 2. Compilar desde el tag
git checkout vX.Y.Z && ./mvnw clean package -DskipTests

# 3. Publicar backend (valida que NO sea SNAPSHOT)
./scripts/publish-images.sh

# 4. Verificar lo que se publicaría sin ejecutar
./scripts/publish-images.sh --dry-run

# 5. (Opcional) Publicar BD si hay cambios estructurales
POSTGRES_PASSWORD=<password> ./scripts/publish-db-image.sh 1.1

# 6. Volver a main y subir tags
git checkout main && git push origin main --tags
```

---

## Opción 1: Usar Imágenes de Docker Hub (Recomendado)

### Ventajas
- ✅ Rápido y fácil
- ✅ No requiere compilación
- ✅ Imágenes probadas y verificadas
- ✅ Actualizaciones automáticas con `:latest`

### Descargar Imágenes

```bash
# Con Docker
docker pull isidromerayo/postgres-tfg:1.0
docker pull isidromerayo/spring-backend-tfg:0.6.2

# Con Podman
podman pull docker.io/isidromerayo/postgres-tfg:1.0
podman pull docker.io/isidromerayo/spring-backend-tfg:0.6.2
```

### Usar con Docker Compose

El archivo `docker-compose.yml` ya está configurado para usar estas imágenes:

```yaml
services:
  postgres_db:
    image: "isidromerayo/postgres-tfg:1.0"
    # ...

  api_service:
    image: "isidromerayo/spring-backend-tfg:0.6.2"
    # ...
```

Simplemente ejecuta:

```bash
docker compose up -d
```

### Usar con Podman Pod

```bash
# El script ya está configurado para usar imágenes de Docker Hub
./scripts/podman-pod.sh start
```

---

## Opción 2: Construir Imágenes Localmente

### Ventajas
- ✅ Control total sobre el código
- ✅ Personalización posible
- ✅ No requiere cuenta de Docker Hub
- ✅ Ideal para desarrollo

### Requisitos

- Docker o Podman instalado
- Maven 3.6+
- Java 21+

### Construcción Paso a Paso

#### 1. Compilar el Backend

```bash
cd TFG_UNIR-backend
./mvnw clean package -DskipTests
```

Esto genera `target/backend.jar` (~60MB)

#### 2. Construir Imagen de PostgreSQL

La imagen no embebe `POSTGRES_PASSWORD` — se pasa en runtime vía `docker-compose.yml` o `podman-pod.sh`.

**Con Docker:**
```bash
docker build -f Dockerfile-db-postgresql \
    -t isidromerayo/postgres-tfg:1.0 .
docker tag isidromerayo/postgres-tfg:1.0 isidromerayo/postgres-tfg:latest
```

**Con Podman:**
```bash
podman build -f Dockerfile-db-postgresql \
    -t localhost/isidromerayo/postgres-tfg:1.0 .
podman tag localhost/isidromerayo/postgres-tfg:1.0 localhost/isidromerayo/postgres-tfg:latest
```

#### 3. Construir Imagen del Backend

**Con Docker:**
```bash
docker build --build-arg VERSION=0.6.2 -t isidromerayo/spring-backend-tfg:0.6.2 .
docker tag isidromerayo/spring-backend-tfg:0.6.2 isidromerayo/spring-backend-tfg:latest
```

**Con Podman:**
```bash
podman build --build-arg VERSION=0.6.2 -t localhost/isidromerayo/spring-backend-tfg:0.6.2 .
podman tag localhost/isidromerayo/spring-backend-tfg:0.6.2 localhost/isidromerayo/spring-backend-tfg:latest
```

#### 4. Verificar Imágenes Construidas

**Con Docker:**
```bash
docker images | grep isidromerayo
```

**Con Podman:**
```bash
podman images | grep isidromerayo
```

Deberías ver:
```
isidromerayo/postgres-tfg          1.0      ...    ...    ...
isidromerayo/postgres-tfg          latest   ...    ...    ...
isidromerayo/spring-backend-tfg   0.6.2    ...    ...    ...
isidromerayo/spring-backend-tfg   latest   ...    ...    ...
```

### Usar Imágenes Locales

#### Con Docker Compose

Si construiste con Docker, el `docker-compose.yml` funcionará directamente:

```bash
docker compose up -d
```

#### Con Podman Pod

Actualiza `scripts/podman-pod.sh`:

```bash
API_SERVICE_IMAGE="localhost/isidromerayo/spring-backend-tfg:0.6.2"
```

Luego ejecuta:

```bash
./scripts/podman-pod.sh start
```

---

## Publicar Imágenes en Docker Hub

### Requisitos

- Cuenta en [Docker Hub](https://hub.docker.com/)
- Autenticación configurada (`docker login` o `podman login`)
- Backend compilado (`./mvnw clean package -DskipTests`)

### Publicar imagen del backend

```bash
# Verificar qué se publicaría (sin ejecutar)
./scripts/publish-images.sh --dry-run

# Publicar
./scripts/publish-images.sh
```

El script:
- Extrae la versión del `pom.xml`
- Rechaza versiones SNAPSHOT
- Construye con `--build-arg VERSION`
- Publica con tags `:version` y `:latest`

### Publicar imagen de PostgreSQL

```bash
# La imagen no embebe POSTGRES_PASSWORD — se pasa en runtime
./scripts/publish-db-image.sh 1.0
```

### Publicación manual

Si prefieres hacerlo manualmente:

```bash
# Backend
docker build --build-arg VERSION=0.6.2 -t isidromerayo/spring-backend-tfg:0.6.2 .
docker push isidromerayo/spring-backend-tfg:0.6.2
docker push isidromerayo/spring-backend-tfg:latest

# PostgreSQL (POSTGRES_PASSWORD se pasa en runtime, no en el build)
docker build -f Dockerfile-db-postgresql \
    -t isidromerayo/postgres-tfg:1.0 .
docker push isidromerayo/postgres-tfg:1.0
docker push isidromerayo/postgres-tfg:latest
```

---

## Comparación de Opciones

| Aspecto | Docker Hub | Local |
|---------|------------|-------|
| **Velocidad** | ⚡ Rápido (solo descarga) | 🐌 Lento (compilar + construir) |
| **Requisitos** | Docker/Podman | Docker/Podman + Maven + Java |
| **Espacio** | ~200MB descarga | ~200MB + ~500MB build |
| **Personalización** | ❌ No | ✅ Sí |
| **Cuenta Docker Hub** | ❌ No necesaria | ❌ No necesaria |
| **Ideal para** | Producción, testing | Desarrollo, personalización |

---

## Versiones Disponibles

### PostgreSQL

| Tag | Descripción |
|-----|-------------|
| `1.0` | PostgreSQL 17, imagen base para el proyecto |
| `latest` | Referencia a la última versión |

### Backend

| Tag | Descripción |
|-----|-------------|
| `0.6.2` | **Última versión** (Spring Boot 3.5.16) |
| `latest` | Referencia a la última versión |

---

## Verificación de Imágenes

### Verificar Versión del Backend

```bash
# Inspeccionar imagen
docker inspect isidromerayo/spring-backend-tfg:0.6.2 | grep -A 5 "Env"

# Ejecutar y verificar logs
docker run --rm isidromerayo/spring-backend-tfg:0.6.2 | head -20
```

---

## Troubleshooting

### Error: "manifest unknown"

**Problema:** La imagen no existe en Docker Hub

**Solución:** Construir localmente o esperar a que se publique

```bash
# Construir localmente
./mvnw clean package -DskipTests
docker build -f Dockerfile-db-postgresql -t isidromerayo/postgres-tfg:1.0 .
docker build --build-arg VERSION=0.6.2 -t isidromerayo/spring-backend-tfg:0.6.2 .
```

### Error: "unauthorized: incorrect username or password"

**Problema:** No estás autenticado en Docker Hub

**Solución:** Autenticarse

```bash
docker login
# o
podman login docker.io
```

### Error: "no space left on device"

**Problema:** No hay espacio en disco

**Solución:** Limpiar imágenes antiguas

```bash
# Ver espacio usado
docker system df

# Limpiar imágenes no usadas
docker image prune -a

# Limpiar todo (cuidado!)
docker system prune -a --volumes
```

### Imágenes Locales vs Docker Hub

**Problema:** Confusión entre imágenes locales y de Docker Hub

**Solución:** Usar prefijos claros

- `localhost/nombre:tag` - Imagen local (Podman)
- `nombre:tag` - Imagen local (Docker)
- `docker.io/nombre:tag` - Imagen de Docker Hub

---

## Actualización de Imágenes

### Actualizar desde Docker Hub

```bash
# Detener servicios
docker compose down

# Actualizar imágenes
docker compose pull

# Reiniciar
docker compose up -d
```

### Reconstruir Localmente

```bash
# Detener servicios
docker compose down

# Recompilar backend
./mvnw clean package -DskipTests

# Reconstruir imágenes
docker build -f Dockerfile-db -t isidromerayo/mariadb-tfg:0.1.0 .
docker build -t isidromerayo/spring-backend-tfg:0.4.0 .

# Reiniciar
docker compose up -d
```

---

## Mejores Prácticas

### Para Desarrollo

1. ✅ Construir imágenes localmente
2. ✅ Usar tags específicos (no `:latest`)
3. ✅ Reconstruir después de cambios en código
4. ✅ Limpiar imágenes antiguas regularmente

### Para Producción

1. ✅ Usar imágenes de Docker Hub
2. ✅ Usar tags de versión específicos
3. ✅ Verificar checksums de imágenes
4. ✅ Implementar escaneo de vulnerabilidades

### Para CI/CD

1. ✅ Construir en pipeline
2. ✅ Ejecutar tests antes de publicar
3. ✅ Usar tags semánticos (major.minor.patch)
4. ✅ Mantener changelog actualizado

---

## Referencias

- [Dockerfile-db-postgresql](../../Dockerfile-db-postgresql) - Dockerfile de PostgreSQL
- [Dockerfile](../../Dockerfile) - Dockerfile del Backend
- [docker-compose.yml](../../docker-compose.yml) - Configuración Docker Compose
- [scripts/publish-images.sh](../../scripts/publish-images.sh) - Publicar imagen del backend
- [scripts/publish-db-image.sh](../../scripts/publish-db-image.sh) - Publicar imagen de PostgreSQL
- [AGENTS.md](../../AGENTS.md) - Release Flow

---

## Soporte

### Documentación
- [AGENTS.md](../../AGENTS.md) - Release Flow y reglas del proyecto
- [scripts/README.md](../../scripts/README.md) - Documentación de scripts
- [docs/security/](../security/) - Documentación de seguridad

### Comunidad
- GitHub Issues - Reportar problemas
- Pull Requests - Contribuir mejoras
