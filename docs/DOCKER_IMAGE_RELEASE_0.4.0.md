# Docker Image Release 0.4.0

**Fecha:** 2026-01-17  
**Versión:** 0.4.0  
**Spring Boot:** 3.5.9  
**Motivo:** Migración a Spring Boot 3.5

---

## 📦 Cambios en esta Versión

### Actualizaciones Principales

1. **Spring Boot 3.5.9**
   - Actualizado desde 3.4.12 a 3.5.9
   - Incluye Spring Framework 6.2
   - Soporte extendido hasta junio 2032

2. **SpringDoc OpenAPI 2.8.5**
   - Actualizado desde 2.5.0 a 2.8.5
   - Mejor compatibilidad con Spring Boot 3.5

3. **Documentación Actualizada**
   - README.md con badge de Spring Boot 3.5.9
   - AGENTS.md con versión actualizada

---

## 🐳 Imágenes Docker Disponibles

### Backend Application

```bash
# Versión específica
docker pull isidromerayo/spring-backend-tfg:0.4.0

# Latest (apunta a 0.4.0)
docker pull isidromerayo/spring-backend-tfg:latest
```

### MariaDB Database

```bash
# Versión específica (sin cambios)
docker pull isidromerayo/mariadb-tfg:0.0.4

# Latest
docker pull isidromerayo/mariadb-tfg:latest
```

---

## 🚀 Construcción de Imágenes

### Prerequisitos

1. **Compilar el proyecto:**
   ```bash
   cd TFG_UNIR-backend
   ./mvnw clean package -DskipTests
   ```

2. **Verificar que existe el JAR:**
   ```bash
   ls -lh target/*.jar
   ```

### Backend Application Image

```bash
# Construir imagen con versión específica
docker build -t isidromerayo/spring-backend-tfg:0.4.0 .

# Crear tag latest
docker tag isidromerayo/spring-backend-tfg:0.4.0 isidromerayo/spring-backend-tfg:latest

# Publicar a Docker Hub
docker login
docker push isidromerayo/spring-backend-tfg:0.4.0
docker push isidromerayo/spring-backend-tfg:latest
```

### Usando Podman

```bash
# Construir imagen con versión específica
podman build -t isidromerayo/spring-backend-tfg:0.4.0 .

# Crear tag latest
podman tag isidromerayo/spring-backend-tfg:0.4.0 isidromerayo/spring-backend-tfg:latest

# Publicar a Docker Hub
podman login docker.io
podman push isidromerayo/spring-backend-tfg:0.4.0
podman push isidromerayo/spring-backend-tfg:latest
```

---

## 🧪 Verificación de Imágenes

### Probar Localmente

```bash
# Ejecutar contenedor
docker run --name backend-test -p 8080:8080 -d \
  -e SPRING_DATASOURCE_URL=jdbc:mariadb://host.docker.internal:3306/tfg_unir \
  isidromerayo/spring-backend-tfg:0.4.0

# Verificar logs
docker logs -f backend-test

# Probar endpoints
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/v1/cursos

# Limpiar
docker stop backend-test
docker rm backend-test
```

### Con Docker Compose

```bash
cd TFG_UNIR-backend

# Levantar servicios
docker compose up -d

# Verificar logs
docker compose logs -f backend

# Detener servicios
docker compose down
```

---

## 📊 Información de la Imagen

### Backend Application

| Propiedad | Valor |
|-----------|-------|
| **Base Image** | eclipse-temurin:21-jre-alpine |
| **Java Version** | 21 |
| **Spring Boot** | 3.5.9 |
| **Puerto Expuesto** | 8080 |
| **Tamaño Aproximado** | ~250 MB |

### Variables de Entorno

```bash
# Configuración de Base de Datos
SPRING_DATASOURCE_URL=jdbc:mariadb://app_db:3306/tfg_unir
SPRING_DATASOURCE_USERNAME=user_tfg
SPRING_DATASOURCE_PASSWORD=tfg_un1r_PWD

# Configuración de JPA
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false

# Configuración de Actuator
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
```

---

## 🔄 Migración desde 0.3.x

### Cambios de Configuración

No se requieren cambios en la configuración de Docker Compose. La imagen es compatible con la configuración existente.

### Pasos de Migración

1. **Detener servicios actuales:**
   ```bash
   docker compose down
   ```

2. **Actualizar imagen en docker-compose.yml:**
   ```yaml
   services:
     backend:
       image: isidromerayo/spring-backend-tfg:0.4.0
   ```

3. **Levantar servicios con nueva versión:**
   ```bash
   docker compose up -d
   ```

4. **Verificar funcionamiento:**
   ```bash
   docker compose logs -f backend
   curl http://localhost:8080/actuator/health
   ```

---

## 🐛 Troubleshooting

### Problema: Imagen no se descarga

```bash
# Verificar conectividad con Docker Hub
docker login

# Forzar descarga
docker pull isidromerayo/spring-backend-tfg:0.4.0
```

### Problema: Contenedor no arranca

```bash
# Ver logs detallados
docker logs backend-container-name

# Verificar variables de entorno
docker inspect backend-container-name | grep -A 20 Env
```

### Problema: No conecta con MariaDB

```bash
# Verificar que MariaDB está corriendo
docker ps | grep mariadb

# Verificar red Docker
docker network inspect backend_default

# Probar conexión desde contenedor
docker exec -it backend-container-name sh
nc -zv app_db 3306
```

---

## 📚 Referencias

- [Plan de Migración a Spring Boot 3.5](SPRING_BOOT_3.5_MIGRATION_PLAN.md)
- [Spring Boot 3.5 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.5-Release-Notes)
- [Dockerfile](../Dockerfile)
- [docker-compose.yml](../docker-compose.yml)

---

## 📝 Notas de Release

### Compatibilidad

- ✅ Compatible con frontend Angular existente
- ✅ Compatible con MariaDB 10.3+
- ✅ Compatible con Java 21
- ✅ Compatible con configuración Docker Compose existente

### Testing

- ✅ 15/15 tests pasando (11 UT + 4 IT)
- ✅ Cobertura de código: 85%
- ✅ SpotBugs: Sin errores críticos
- ✅ OWASP: Sin vulnerabilidades críticas nuevas
- ✅ SonarCloud: Quality Gate PASSED

### Seguridad

- ✅ Actualizado a Spring Boot 3.5.9 con últimos parches de seguridad
- ✅ Dependencias actualizadas a versiones seguras
- ✅ Análisis OWASP ejecutado sin vulnerabilidades críticas

---

**Última actualización:** 2026-01-17
