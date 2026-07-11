# Guía de Imágenes Docker

## Introducción

Este proyecto proporciona dos formas de obtener las imágenes Docker necesarias:

1. **Descargar desde Docker Hub** (recomendado) - Imágenes pre-construidas
2. **Construir localmente** - Para desarrollo o personalización

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
docker pull isidromerayo/mariadb-tfg:0.1.0
docker pull isidromerayo/spring-backend-tfg:0.4.0

# Con Podman
podman pull docker.io/isidromerayo/mariadb-tfg:0.1.0
podman pull docker.io/isidromerayo/spring-backend-tfg:0.4.0
```

### Usar con Docker Compose

El archivo `docker-compose.yml` ya está configurado para usar estas imágenes:

```yaml
services:
  maria_db:
    image: "isidromerayo/mariadb-tfg:0.1.0"
    # ...

  api_service:
    image: "isidromerayo/spring-backend-tfg:0.4.0"
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

#### 2. Construir Imagen de MariaDB

**Con Docker:**
```bash
docker build -f Dockerfile-db -t isidromerayo/mariadb-tfg:0.1.0 .
docker tag isidromerayo/mariadb-tfg:0.1.0 isidromerayo/mariadb-tfg:latest
```

**Con Podman:**
```bash
podman build -f Dockerfile-db -t localhost/isidromerayo/mariadb-tfg:0.1.0 .
podman tag localhost/isidromerayo/mariadb-tfg:0.1.0 localhost/isidromerayo/mariadb-tfg:latest
```

#### 3. Construir Imagen del Backend

**Con Docker:**
```bash
docker build -t isidromerayo/spring-backend-tfg:0.4.0 .
docker tag isidromerayo/spring-backend-tfg:0.4.0 isidromerayo/spring-backend-tfg:latest
```

**Con Podman:**
```bash
podman build -t localhost/isidromerayo/spring-backend-tfg:0.4.0 .
podman tag localhost/isidromerayo/spring-backend-tfg:0.4.0 localhost/isidromerayo/spring-backend-tfg:latest
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
isidromerayo/mariadb-tfg          0.1.0    ...    ...    ...
isidromerayo/mariadb-tfg          latest   ...    ...    ...
isidromerayo/spring-backend-tfg   0.4.0    ...    ...    ...
isidromerayo/spring-backend-tfg   latest   ...    ...    ...
```

### Usar Imágenes Locales

#### Con Docker Compose

Si construiste con Docker, el `docker-compose.yml` funcionará directamente:

```bash
docker compose up -d
```

Si construiste con Podman, actualiza `docker-compose.yml`:

```yaml
services:
  maria_db:
    image: "localhost/isidromerayo/mariadb-tfg:0.1.0"
    # ...

  api_service:
    image: "localhost/isidromerayo/spring-backend-tfg:0.4.0"
    # ...
```

#### Con Podman Pod

Actualiza `scripts/podman-pod.sh`:

```bash
MARIA_DB_IMAGE="localhost/isidromerayo/mariadb-tfg:0.1.0"
API_SERVICE_IMAGE="localhost/isidromerayo/spring-backend-tfg:0.4.0"
```

Luego ejecuta:

```bash
./scripts/podman-pod.sh start
```

---

## Publicar Imágenes en Docker Hub (Opcional)

Si quieres publicar tus propias versiones en Docker Hub:

### Requisitos

- Cuenta en [Docker Hub](https://hub.docker.com/)
- Autenticación configurada

### Pasos

#### 1. Autenticarse

**Con Docker:**
```bash
docker login
```

**Con Podman:**
```bash
podman login docker.io
```

Te pedirá:
- **Username**: tu usuario de Docker Hub
- **Password**: tu contraseña o token de acceso

#### 2. Construir y Publicar

Usa el script automatizado:

```bash
./scripts/publish-images.sh
```

Este script:
1. Verifica que el backend esté compilado
2. Construye ambas imágenes
3. Las etiqueta con versión y `:latest`
4. Verifica autenticación
5. Publica en Docker Hub

#### 3. Publicación Manual

Si prefieres hacerlo manualmente:

```bash
# Construir imágenes (ver sección anterior)

# Publicar MariaDB
docker push isidromerayo/mariadb-tfg:0.1.0
docker push isidromerayo/mariadb-tfg:latest

# Publicar Backend
docker push isidromerayo/spring-backend-tfg:0.4.0
docker push isidromerayo/spring-backend-tfg:latest
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

### MariaDB

| Tag | Descripción | Tamaño |
|-----|-------------|--------|
| `0.1.0` | Versión estable con BCrypt | ~400MB |
| `latest` | Última versión (actualmente 0.1.0) | ~400MB |
| `0.0.4` | ⚠️ Versión antigua sin BCrypt | ~400MB |

### Backend

| Tag | Descripción | Tamaño |
|-----|-------------|--------|
| `0.4.0` | **Última versión** (Spring Boot 3.5) | ~450MB |
| `latest` | Referencia a la última versión (`0.4.0`) | ~450MB |
| `0.3.0` | Versión estable anterior (Spring Boot 3.4) | ~450MB |
| `0.2.2` | ⚠️ Versión antigua sin BCrypt | ~450MB |

---

## Verificación de Imágenes

### Verificar Versión de MariaDB

```bash
# Inspeccionar imagen
docker inspect isidromerayo/mariadb-tfg:0.1.0 | grep -A 5 "Env"

# Ejecutar y verificar
docker run --rm isidromerayo/mariadb-tfg:0.1.0 mariadbd --version
```

### Verificar Versión del Backend

```bash
# Inspeccionar imagen
docker inspect isidromerayo/spring-backend-tfg:0.4.0 | grep -A 5 "Env"

# Ejecutar y verificar logs
docker run --rm isidromerayo/spring-backend-tfg:0.4.0 | head -20
```

### Verificar Contraseñas BCrypt

```bash
# Iniciar MariaDB
docker run -d --name test-maria isidromerayo/mariadb-tfg:0.1.0

# Esperar a que inicie
sleep 10

# Verificar contraseñas
docker exec test-maria mariadb -u user_tfg -ptfg_un1r_PWD tfg_unir \
  -e "SELECT id, nombre, LEFT(password, 30) FROM usuarios LIMIT 3"

# Limpiar
docker rm -f test-maria
```

Resultado esperado:
```
+----+---------------+--------------------------------+
| id | nombre        | LEFT(password, 30)             |
+----+---------------+--------------------------------+
|  1 | María         | $2b$10$JKheLVrM5.jvtYVvd.tfqOL |
|  2 | Juan Antonio  | $2b$10$JKheLVrM5.jvtYVvd.tfqOL |
|  3 | Marta         | $2b$10$JKheLVrM5.jvtYVvd.tfqOL |
+----+---------------+--------------------------------+
```

---

## Troubleshooting

### Error: "manifest unknown"

**Problema:** La imagen no existe en Docker Hub

**Solución:** Construir localmente o esperar a que se publique

```bash
# Construir localmente
./mvnw clean package -DskipTests
docker build -f Dockerfile-db -t isidromerayo/mariadb-tfg:0.1.0 .
docker build -t isidromerayo/spring-backend-tfg:0.4.0 .
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

- [Dockerfile-db](../Dockerfile-db) - Dockerfile de MariaDB
- [Dockerfile](../Dockerfile) - Dockerfile del Backend
- [docker-compose.yml](../docker-compose.yml) - Configuración Docker Compose
- [CHANGELOG_IMAGES.md](../CHANGELOG_IMAGES.md) - Changelog de versiones
- [scripts/publish-images.sh](../scripts/publish-images.sh) - Script de publicación

---

## Soporte

### Documentación
- [SECURITY_BCRYPT.md](../SECURITY_BCRYPT.md) - Guía de seguridad
- [docs/PODMAN_GUIDE.md](PODMAN_GUIDE.md) - Guía de Podman
- [docs/security/](security/) - Documentación de seguridad

### Comunidad
- GitHub Issues - Reportar problemas
- Pull Requests - Contribuir mejoras
