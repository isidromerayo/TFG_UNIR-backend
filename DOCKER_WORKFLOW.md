# Flujo de Trabajo con Docker/Podman

Este documento describe el flujo de trabajo completo para desarrollo, construcción y publicación de imágenes Docker/Podman del backend.

## 📋 Flujo de Trabajo Recomendado

### 1. Desarrollo y Testing Local

```bash
# Ejecutar backend localmente con Spring Boot
./mvnw spring-boot:run

# O con Java directamente
./mvnw clean package -DskipTests
java -jar target/backend.jar
```

### 2. Construcción de la Imagen

#### Opción A: Con Podman (Recomendado)

```bash
# 1. Compilar el proyecto
bash -c 'eval "$(vfox activate bash)" && vfox use -g java@21 && ./mvnw clean package -DskipTests'

# 2. Construir la imagen con la nueva versión
VERSION="0.2.2"  # Actualizar según corresponda
podman build -t isidromerayo/spring-backend-tfg:${VERSION} .

# 3. Crear tag latest (opcional, solo para versiones estables)
podman tag isidromerayo/spring-backend-tfg:${VERSION} isidromerayo/spring-backend-tfg:latest
```

#### Opción B: Con Docker

```bash
# 1. Compilar el proyecto
./mvnw clean package -DskipTests

# 2. Construir la imagen
VERSION="0.2.2"
docker build -t isidromerayo/spring-backend-tfg:${VERSION} .

# 3. Crear tag latest
docker tag isidromerayo/spring-backend-tfg:${VERSION} isidromerayo/spring-backend-tfg:latest
```

### 3. Testing de la Imagen Localmente

#### Con Podman Pod (Recomendado)

```bash
# 1. Actualizar la versión en el script
# Editar scripts/podman-pod.sh y cambiar API_SERVICE_IMAGE

# 2. Iniciar el backend
./scripts/podman-pod.sh start

# 3. Verificar que funciona
curl http://localhost:8080/api

# 4. Verificar CORS (importante para frontends)
curl -I -X OPTIONS http://localhost:8080/api/cursos \
  -H "Origin: http://localhost:4200" \
  -H "Access-Control-Request-Method: GET"

# 5. Probar con el frontend
# Abrir Angular/React/Vue y verificar que no hay errores CORS

# 6. Ver logs si hay problemas
./scripts/podman-pod.sh logs

# 7. Detener cuando termines de probar
./scripts/podman-pod.sh stop
```

#### Con Docker Compose

```bash
# 1. Actualizar docker-compose.yml con la nueva versión
# Cambiar: image: "isidromerayo/spring-backend-tfg:0.2.2"

# 2. Levantar servicios
docker compose up -d

# 3. Verificar
curl http://localhost:8080/api

# 4. Detener
docker compose down
```

### 4. Publicación en Docker Hub

**⚠️ IMPORTANTE**: Solo publicar después de verificar que la imagen funciona correctamente localmente.

#### Con Podman

```bash
# 1. Login en Docker Hub (solo la primera vez)
podman login docker.io
# Username: isidromerayo
# Password: [Personal Access Token]

# 2. Push de la versión específica
VERSION="0.2.2"
podman push isidromerayo/spring-backend-tfg:${VERSION}

# 3. Push del tag latest (solo para versiones estables)
podman push isidromerayo/spring-backend-tfg:latest

# 4. Verificar en Docker Hub
# https://hub.docker.com/r/isidromerayo/spring-backend-tfg/tags
```

#### Con Docker

```bash
# 1. Login en Docker Hub
docker login
# Username: isidromerayo
# Password: [Personal Access Token]

# 2. Push de la versión específica
VERSION="0.2.2"
docker push isidromerayo/spring-backend-tfg:${VERSION}

# 3. Push del tag latest
docker push isidromerayo/spring-backend-tfg:latest
```

### 5. Actualización de Documentación

```bash
# 1. Actualizar README.md si hay cambios importantes
# 2. Actualizar docker-compose.yml con la nueva versión
# 3. Actualizar scripts/podman-pod.sh con la nueva versión

# 4. Commit de los cambios
git add README.md docker-compose.yml scripts/podman-pod.sh
git commit -m "chore: update backend image to version ${VERSION}"
git push
```

## 🔄 Versionado Semántico

Seguimos [Semantic Versioning](https://semver.org/):

- **MAJOR.MINOR.PATCH** (ejemplo: 1.2.3)
  - **MAJOR**: Cambios incompatibles en la API
  - **MINOR**: Nueva funcionalidad compatible con versiones anteriores
  - **PATCH**: Correcciones de bugs compatibles

**Ejemplos de cuándo incrementar cada número:**

```bash
# PATCH: Corrección de bug, actualización de dependencias
0.2.1 -> 0.2.2

# MINOR: Nueva funcionalidad, mejora de CORS, nuevo endpoint
0.2.2 -> 0.3.0

# MAJOR: Cambio breaking, nueva versión de Spring Boot, cambio en API
0.3.0 -> 1.0.0
```

## 📝 Checklist Antes de Publicar

- [ ] Código compilado sin errores: `./mvnw clean package`
- [ ] Tests pasando (si aplica): `./mvnw test`
- [ ] Imagen construida correctamente
- [ ] Imagen probada localmente con Podman Pod o Docker Compose
- [ ] Backend responde en `http://localhost:8080/api`
- [ ] CORS verificado con `curl` o frontend
- [ ] Logs del contenedor sin errores críticos
- [ ] Versión actualizada en:
  - [ ] `pom.xml` (si cambió)
  - [ ] `docker-compose.yml`
  - [ ] `scripts/podman-pod.sh`
- [ ] Documentación actualizada si hay cambios importantes
- [ ] Commit realizado con mensaje descriptivo
- [ ] Imagen publicada en Docker Hub
- [ ] Tag latest actualizado (solo para versiones estables)

## 🐛 Troubleshooting

### La imagen no se construye

```bash
# Verificar que el JAR existe
ls -lh target/backend.jar

# Limpiar y recompilar
./mvnw clean package -DskipTests
```

### El contenedor no arranca

```bash
# Ver logs del contenedor
podman logs api_service
# o
docker logs api_service

# Verificar que MariaDB está corriendo
podman ps | grep maria_db
```

### Errores CORS en el frontend

```bash
# Verificar configuración CORS
curl -I -X OPTIONS http://localhost:8080/api/cursos \
  -H "Origin: http://localhost:4200" \
  -H "Access-Control-Request-Method: GET" | grep -i access-control

# Debe mostrar:
# Access-Control-Allow-Origin: http://localhost:4200
# Access-Control-Allow-Methods: GET,POST,OPTIONS,DELETE,PUT,PATCH
```

### La imagen no se puede pushear

```bash
# Verificar login
podman login docker.io
# o
docker login

# Verificar que la imagen existe localmente
podman images | grep spring-backend-tfg
```

## 🔗 Referencias

- [Docker Hub - spring-backend-tfg](https://hub.docker.com/r/isidromerayo/spring-backend-tfg)
- [Semantic Versioning](https://semver.org/)
- [Podman Documentation](https://docs.podman.io/)
- [Docker Documentation](https://docs.docker.com/)
