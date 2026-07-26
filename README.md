# TFG_UNIR Backend

<div align="center">

## 🛠️ Tecnologías y Herramientas

### 🏗️ Build & CI/CD
[![Java CI](https://img.shields.io/github/actions/workflow/status/isidromerayo/TFG_UNIR-backend/maven.yml?label=Build&logo=github-actions&logoColor=white)](https://github.com/isidromerayo/TFG_UNIR-backend/actions/workflows/maven.yml)
[![Pull Request](https://img.shields.io/github/actions/workflow/status/isidromerayo/TFG_UNIR-backend/pull-request.yml?label=PR%20Checks&logo=github-actions&logoColor=white)](https://github.com/isidromerayo/TFG_UNIR-backend/actions/workflows/pull-request.yml)

### 📊 Calidad de Código - SonarQube
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=isidromerayo_TFG_UNIR-backend&metric=alert_status&style=flat-square)](https://sonarcloud.io/summary/new_code?id=isidromerayo_TFG_UNIR-backend)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=isidromerayo_TFG_UNIR-backend&metric=coverage&style=flat-square)](https://sonarcloud.io/summary/new_code?id=isidromerayo_TFG_UNIR-backend)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=isidromerayo_TFG_UNIR-backend&metric=sqale_rating&style=flat-square)](https://sonarcloud.io/summary/new_code?id=isidromerayo_TFG_UNIR-backend)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=isidromerayo_TFG_UNIR-backend&metric=bugs&style=flat-square)](https://sonarcloud.io/summary/new_code?id=isidromerayo_TFG_UNIR-backend)

### 📦 Dependencias
[![Dependabot](https://img.shields.io/badge/dependabot-enabled-0366d6.svg?logo=dependabot&logoColor=white)](https://github.com/isidromerayo/TFG_UNIR-backend/security/dependabot)
[![Known Vulnerabilities](https://snyk.io/test/github/isidromerayo/TFG_UNIR-backend/badge.svg)](https://snyk.io/test/github/isidromerayo/TFG_UNIR-backend)

### 📄 Licencia
[![License](https://img.shields.io/github/license/isidromerayo/TFG_UNIR-backend?color=blue&style=flat-square)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-007396?logo=java&logoColor=white)](https://www.oracle.com/java/technologies/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.16-6DB33F?logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)

</div>

# Universidad Internacional de La Rioja

## Escuela Superior de Ingeniería y Tecnología 

### Grado en Ingeniería Informática

#### TFG: Frameworks frontend JavaScript: Análisis y estudio práctico

##### Backend

## 🚀 Desarrollo y Contribución

Este proyecto sigue un flujo de trabajo basado en Pull Requests para mantener la calidad del código.

### 📋 Flujo de trabajo recomendado

```bash
# 1. Crear nueva rama para tu feature/fix
git checkout main
git pull origin main
git checkout -b feature/nombre-descriptivo

# 2. Realizar cambios y commits
git add .
git commit -m "feat: descripción del cambio"

# 3. Push y crear Pull Request
git push -u origin feature/nombre-descriptivo
# Crear PR desde GitHub web

# 4. Después del merge, limpiar
git checkout main
git pull origin main
git branch -d feature/nombre-descriptivo
```

### 🤖 Skills para agentes de IA (estandarización del equipo)

Este repositorio versiona *skills* (guías y patrones en Markdown) para que los agentes de IA trabajen alineados con el stack y las convenciones del proyecto.

- **Reglas de contribución y flujo de trabajo**: [AGENTS.md](AGENTS.md)
- **Gestión de skills (estructura, alta/actualización)**: [docs/skills/USING_SKILLS.md](docs/skills/USING_SKILLS.md)

### 🛠️ Stack tecnológico

- **Java 21**
- **Spring Boot 3.5.16**
- **Spring Framework 6.2.19**
- **Hibernate 6.6.53.Final**
- **Spring Data JPA** - Persistencia
- **Spring Security 6.5.11** - Autenticación y autorización
- **PostgreSQL 15+** - Base de datos producción
- **H2** - Base de datos testing
- **JWT** - Tokens de autenticación
- **Swagger/OpenAPI 2.8.17** - Documentación API
- **Lombok** - Reducción de boilerplate
- **JaCoCo** - Cobertura de código
- **SpotBugs** - Análisis estático
- **Docker/Podman** - Containerización

### 🧪 Tests

```bash
# Tests unitarios
./mvnw test

# Tests de integración
./mvnw -DskipUTs -Pintegration-tests verify

# Todos los tests (unitarios + integración)
./mvnw clean verify -Pintegration-tests

# Tests con cobertura de código
./mvnw clean verify -Pintegration-tests
# Reporte en: target/site/jacoco/index.html
```

**Cobertura actual**: 85% (objetivo: ≥80%)
- Tests unitarios: 11 tests
- Tests de integración: 4 tests
- Total: 15 tests

### 📦 Perfiles de Maven

```bash
# Perfil para tests de integración
mvn verify -Pintegration-tests

# Perfil para análisis de dependencias
mvn verify -Pdependency-check
```

### 🔍 Análisis de código

#### Cobertura de código (JaCoCo)

```bash
# Generar reporte de cobertura (unitarios + integración)
./mvnw clean verify -Pintegration-tests

# Ver reportes
open target/site/jacoco/index.html      # Reporte combinado (principal)
open target/site/jacoco-ut/index.html   # Solo tests unitarios
open target/site/jacoco-it/index.html   # Solo tests de integración
```

**Configuración**:
- Reportes separados para UT e IT
- Reporte combinado (merge automático)
- Exclusiones: DTOs y entidades JPA
- Ver: `docs/quality/JACOCO_CONFIGURATION.md` y `docs/quality/COVERAGE_ANALYSIS.md`

#### Análisis estático (SpotBugs)

```bash
# Análisis con SpotBugs
./mvnw compile spotbugs:check

# SpotBugs con plugins de seguridad
./mvnw spotbugs:spotbugs
```

#### Análisis de calidad (SonarQube)

```bash
# Análisis local (requiere SONAR_TOKEN)
./mvnw sonar:sonar -Dsonar.token=${SONAR_TOKEN}

# Ver resultados en:
# https://sonarcloud.io/project/overview?id=isidromerayo_TFG_UNIR-backend
```

**Configuración**: Las propiedades de SonarQube están en el `pom.xml`
- Ver: `docs/quality/SONARQUBE_POM_CONFIG.md`

#### Análisis de dependencias (OWASP)

```bash
# OWASP Dependency Check (perfil activado)
./mvnw -Pdependency-check verify

# Con API Key del NVD
./mvnw -Pdependency-check verify -Dnvd.api.key=${NVD_API_KEY}
```

### 🔐 Autenticación

El backend utiliza JWT (JSON Web Tokens) para la autenticación.
- Librería: `jjwt` v0.13.0
- Configuración de seguridad con Spring Security

#### 👥 Usuarios de Prueba

La base de datos incluye usuarios de prueba precargados. Ejemplos:

**Usuarios Activos** (pueden iniciar sesión):
- `c@example.com` / `1234`
- `ines@localhost` / `1234`

**Usuarios Pendientes** (para testing de estados):
- `maria@localhost` / `1234`

> 📋 **Lista completa de usuarios**: Ver el [README del monorepo](https://github.com/isidromerayo/TFG_UNIR-monorepo#-usuarios-de-prueba) para la lista completa de usuarios activos y pendientes.

Es necesaria una versión de Java 21, para utilizar Spring Boot 3.5.x

```
cd backend
./mvnw clean install
```

#### BBDD: PostgreSQL para producción

```bash
$ psql --version
psql (PostgreSQL) 15.x
```

> ℹ️ **Base de datos**: PostgreSQL es la única base de datos soportada.

##### Crear base de datos y usuario

```sql
-- Crear base de datos
CREATE DATABASE tfg_unir;

-- Crear usuario
CREATE USER user_tfg WITH PASSWORD 'tfg_un1r_PWD';

-- Dar privilegios
GRANT ALL PRIVILEGES ON DATABASE tfg_unir TO user_tfg;
```

##### Carga inicial de datos

Los scripts para PostgreSQL están organizados en `recursos/db/postgresql/`:

```bash
psql -h localhost -U user_tfg -d tfg_unir -f recursos/db/postgresql/01-create.sql
psql -h localhost -U user_tfg -d tfg_unir -f recursos/db/postgresql/02-create.sql
psql -h localhost -U user_tfg -d tfg_unir -f recursos/db/postgresql/03-create.sql
```

Backup de datos:

```bash
pg_dump -h localhost -U user_tfg -d tfg_unir > recursos/db/postgresql/dump.postgresql.sql
```

#### 🐳 Containerización con Docker/Podman

> **Nota**: Este proyecto soporta tanto Docker como Podman. Todos los comandos `docker` pueden reemplazarse por `podman`. Ver la [sección de Podman](#-soporte-para-podman) para más detalles.

#### 🔧 Uso con Podman Pod

El script `scripts/podman-pod.sh` ha sido actualizado para soportar variables de entorno:

```bash
# Copiar archivo de entorno (si no existe)
cp .env.example .env

# Iniciar el backend con Podman Pod
./scripts/podman-pod.sh start

# Verificar estado
./scripts/podman-pod.sh status

# Ver logs del API
./scripts/podman-pod.sh logs

# Ver logs de la base de datos
./scripts/podman-pod.sh logs db

# Detener el backend
./scripts/podman-pod.sh stop
```

El script cargará las variables de entorno desde el archivo `.env` y las pasará a los contenedores, con valores por defecto si no están definidas.

#### � Mejoras de Configuración Docker

Recientemente se han implementado las siguientes optimizaciones:

1. **Dockerfile mejorado** - Uso de JRE en lugar de JDK, usuario no root, optimizaciones JVM
2. **.dockerignore** - Excluye archivos innecesarios del contexto de build
3. **Variables de entorno** - Soporte para configuración flexible
4. **Limitaciones de recursos** - Control de CPU y memoria
5. **Red personalizada** - Mejor aislamiento entre contenedores

#### 🔧 Configuración Inicial

1. **Copiar archivo de entorno**:
   ```bash
   cp .env.example .env
   ```

2. **Editar configuración**:
   Abre `.env` y personaliza las credenciales si es necesario. La configuración por defecto es:
   ```
   POSTGRES_PASSWORD=mypass
   POSTGRES_DB=tfg_unir
   POSTGRES_USER=user_tfg
   ```

#### 🚀 Levantar Servicios

Antes de levantar los servicios, asegúrate de que tienes el JAR de la aplicación compilado (requiere Java 21):

```bash
# Compilar la aplicación (requiere Java 21)
./mvnw clean package -DskipTests

# Levantar servicios con docker compose
docker compose up -d --build
```

#### 📊 Verificar Estado

```bash
# Ver containers en ejecución
docker compose ps

# Ver logs
docker compose logs -f

# Verificar healthchecks
docker compose exec api_service wget -qO- http://localhost:8080/actuator/health
```

#### 🔍 Troubleshooting

**Problema**: El contenedor de la API no se inicia.  
**Solución**: Verifica que el archivo `target/backend.jar` existe y que las credenciales de la base de datos son correctas.

**Problema**: La API no se conecta a la base de datos.  
**Solución**: Verifica que el contenedor de PostgreSQL está saludable (`docker compose ps`).

#### 📈 Optimización de Recursos

El `docker-compose.yml` incluye limitaciones de recursos para prevenir que los contenedores consuman toda la memoria:

```yaml
deploy:
  resources:
    limits:
      cpus: '1.0'
      memory: 1G
    reservations:
      cpus: '0.5'
      memory: 512M
```

#### 🗄️ Docker PostgreSQL

##### Usar imagen oficial

Para usar una instancia limpia de PostgreSQL:

```bash
docker run --name postgres-tfg -e POSTGRES_PASSWORD=mypass -e POSTGRES_DB=tfg_unir -e POSTGRES_USER=user_tfg -p 5432:5432 -d postgres:17
```

##### Construir imagen personalizada de PostgreSQL

```bash
cd backend

# Construir imagen (requiere POSTGRES_PASSWORD)
POSTGRES_PASSWORD=mi_password docker build -f Dockerfile-db-postgresql \
    --build-arg POSTGRES_PASSWORD=mi_password \
    -t isidromerayo/postgres-tfg:1.0 .
```

##### Publicar imágenes en Docker Hub

```bash
# Backend (extrae versión del pom.xml, rechaza SNAPSHOT)
./scripts/publish-images.sh

# PostgreSQL (requiere POSTGRES_PASSWORD)
POSTGRES_PASSWORD=mi_password ./scripts/publish-db-image.sh 1.0
```

Ver guía completa: [docs/docker/DOCKER_IMAGES_GUIDE.md](docs/docker/DOCKER_IMAGES_GUIDE.md)

#### BBDD: H2 para test


#### Lanzar aplicación con Spring Boot 3

Es necesario disponer de BBDD

Lanzar aplicación desde consola

```
cd backend
./mvnw spring-boot:run
```

Visualizar API expuesta

http://localhost:8080/api


Swagger

http://localhost:8080/swagger-ui.html

#### Docker Spring Boot 

Construir imagen de aplicación con el jar generado del backend (con el `spring.datasource.url=jdbc:postgresql://app_db:5432/tfg_unir` en el application.properties) hay que ejecutar un maven para generar


```
cd backend
./mvnw clean install
docker build -t isidromerayo/spring-backend-tfg:VERSION-X.Y.Z .
```

https://spring.io/guides/topicals/spring-boot-docker/

#### 🐳 docker compose

Con docker compose se montará un contenedor con PostgreSQL (datos precargados) y otro con la aplicación de Spring Boot con el API.

##### Prerequisitos

1. **Archivos SQL**: Deben existir en `../recursos/db/postgresql/`:
   - `01-create.sql` - Creación de esquema
   - `02-create.sql` - Tablas
   - `03-create.sql` - Datos iniciales

2. **Configuración del backend**: El `application.properties` debe soportar variables de entorno:
   ```properties
   spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/tfg_unir}
   ```

3. **Imagen del backend actualizada**: Si modificas el código, necesitas:
   ```bash
   # Compilar (requiere Java 21)
   ./mvnw clean package -DskipTests
   
   # Reconstruir imagen
   docker build -t isidromerayo/spring-backend-tfg:VERSION .
   
   # Actualizar versión en docker-compose.yml
   ```

##### Levantar los servicios

```bash
cd backend

# Levantar en primer plano (ver logs)
docker compose up

# O en segundo plano
docker compose up -d
```

MariaDB correra en el puerto por defecto *3306* y Spring Boot 3 en el *8080*, así no tendremos montado lo necesario para tener el backend y probar la aplicación con los diferentes frameworks.

Con `docker compose up -d` corre en segundo plano y liberamos la terminal

```
[+] Running 2/2
 ✔ Container backend-maria_db-1     Started     0.4s 
 ✔ Container backend-api_service-1  Started     0.6s 

```

Para detener las instancias de los contenedores `docker compose stop`.

```
[+] Stopping 2/2
 ✔ Container backend-api_service-1    Stopped     0.3s 
 ✔ Container backend-postgres_db-1   Stopped     0.5s 
```

#### 📤 Publicar imágenes en Docker Hub

##### Usar scripts automatizados

```bash
# Publicar backend (extrae versión del pom.xml)
./scripts/publish-images.sh

# Publicar PostgreSQL (requiere variable de entorno)
POSTGRES_PASSWORD=mi_password ./scripts/publish-db-image.sh 1.0
```

##### Flujo completo de release

Ver [AGENTS.md - Release Flow](AGENTS.md) para el proceso completo.

##### Guía de versionado

Seguimos [Semantic Versioning](https://semver.org/):

- **MAJOR.MINOR.PATCH** (ejemplo: 1.2.3)
  - **MAJOR**: Cambios incompatibles en la API
  - **MINOR**: Nueva funcionalidad compatible con versiones anteriores
  - **PATCH**: Correcciones de bugs compatibles

**Ejemplos:**
```bash
# El script detecta la versión del pom.xml automáticamente
./scripts/publish-images.sh

# O especificar manualmente
./scripts/publish-images.sh --version 1.0.0
```

##### Script de publicación

Usa los scripts incluidos en el proyecto:

```bash
# Backend
./scripts/publish-images.sh

# PostgreSQL
POSTGRES_PASSWORD=mi_password ./scripts/publish-db-image.sh 1.0
```

##### Troubleshooting

**Error: "denied: requested access to the resource is denied"**
```bash
# Asegúrate de estar logeado
docker login
# o
podman login docker.io
```

**Error: "unauthorized: authentication required"**
```bash
# Tu sesión expiró, vuelve a hacer login
docker logout
docker login
```

**Error: "tag does not exist"**
```bash
# Verifica que la imagen existe localmente
docker images | grep spring-backend-tfg
# Si no existe, construye la imagen primero
docker build -t isidromerayo/spring-backend-tfg:VERSION .
```

---

### 🐙 Soporte para Podman

Este proyecto es **totalmente compatible con Podman** como alternativa a Docker. Podman es una herramienta de contenedores sin daemon, más segura y que puede ejecutarse sin privilegios de root.

#### ¿Por qué Podman?

- 🔒 **Más seguro**: Sin daemon, ejecución rootless por defecto
- 🚀 **Compatible con Docker**: Misma sintaxis de comandos
- 📦 **Incluido en RHEL/Fedora**: No requiere instalación adicional
- 🎯 **Mejor aislamiento**: Cada contenedor es un proceso independiente
- ✅ **Compatible con Kubernetes**: Genera YAML de Kubernetes directamente

#### Instalación de Podman

**Linux (Debian/Ubuntu):**
```bash
sudo apt-get update
sudo apt-get install podman
```

**Linux (Fedora/RHEL):**
```bash
sudo dnf install podman
```

**macOS:**
```bash
brew install podman
podman machine init
podman machine start
```

**Verificar instalación:**
```bash
podman --version
```

#### Uso con Podman

Todos los comandos Docker funcionan con Podman reemplazando `docker` por `podman`:

##### Ejecutar PostgreSQL con Podman

```bash
# Equivalente a: docker run --name postgres-tfg -p 5432:5432 -d postgres:17
podman run --name postgres-tfg -p 5432:5432 -d postgres:17
```

##### Construir imagen del backend con Podman

> **⚠️ Importante**: El `Dockerfile` usa `docker.io/eclipse-temurin:21-jdk` (registry completo) para asegurar compatibilidad con Podman y Docker. Podman requiere especificar el registry explícitamente.

```bash
cd backend
./mvnw clean install
podman build -t isidromerayo/spring-backend-tfg:VERSION-X.Y.Z .
```

##### Usar Podman Pod (Recomendado)

> **✅ Solución recomendada**: Podman Pod es la forma nativa de Podman para agrupar contenedores (similar a un pod de Kubernetes). Los contenedores en un pod comparten el mismo namespace de red, por lo que pueden comunicarse usando `localhost`.

**Opción 1: Usar el script de ayuda (Recomendado)**

El proyecto incluye un script `scripts/podman-pod.sh` que simplifica la gestión del backend:

```bash
cd backend

# Iniciar el backend
./scripts/podman-pod.sh start

# Ver el estado
./scripts/podman-pod.sh status

# Ver logs del API
./scripts/podman-pod.sh logs

# Ver logs de PostgreSQL
./scripts/podman-pod.sh logs db

# Detener el backend
./scripts/podman-pod.sh stop

# Reiniciar el backend
./scripts/podman-pod.sh restart
```

El script automáticamente:
- Crea el pod con los puertos necesarios
- Inicia PostgreSQL con los datos precargados
- Inicia el backend API
- Verifica que los archivos SQL existen

**Opción 2: Comandos manuales**

**1. Crear el pod**
```bash
cd backend
podman pod create --name backend-pod -p 8080:8080 -p 5432:5432
```

**2. Ejecutar PostgreSQL en el pod**
```bash
podman run -d --pod backend-pod --name postgres_db \
  -v tfg_unir-backend_pg_data:/var/lib/postgresql/data \
  -v $(pwd)/../recursos/db/postgresql/01-create.sql:/docker-entrypoint-initdb.d/01-create.sql \
  -v $(pwd)/../recursos/db/postgresql/02-create.sql:/docker-entrypoint-initdb.d/02-create.sql \
  -v $(pwd)/../recursos/db/postgresql/03-create.sql:/docker-entrypoint-initdb.d/03-create.sql \
  -e POSTGRES_PASSWORD=mypass \
  -e POSTGRES_DB=tfg_unir \
  -e POSTGRES_USER=user_tfg \
  postgres:17
```

**3. Ejecutar el backend en el pod**
```bash
# Nota: Usamos localhost porque están en el mismo pod
podman run -d --pod backend-pod --name api_service \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/tfg_unir \
  isidromerayo/spring-backend-tfg:latest
```

**4. Verificar el estado**
```bash
# Ver el pod y sus contenedores
podman pod ps
podman ps --pod

# Ver logs
podman logs api_service
podman logs maria_db

# Probar el API
curl http://localhost:8080/api
```

**5. Detener y eliminar**
```bash
# Detener el pod (detiene todos los contenedores)
podman pod stop backend-pod

# Eliminar el pod (elimina todos los contenedores)
podman pod rm backend-pod
```

##### Usar Podman Compose

> **⚠️ Limitación conocida**: Podman con docker-compose tiene problemas con la resolución DNS entre contenedores. Se recomienda usar Podman Pod (ver arriba) o Docker para docker-compose.

> **⚠️ Prerequisito**: Asegúrate de que los archivos SQL existen en `../recursos/db/` (ver sección [docker compose](#docker-compose) para detalles).

**Opción 1: Podman Compose (requiere instalación)**
```bash
# Instalar podman-compose
pip3 install podman-compose

# Usar igual que docker-compose
cd backend
podman-compose up
podman-compose up -d  # En segundo plano
podman-compose stop
```

**Opción 2: Podman 4.0+ (soporte nativo)**
```bash
# Podman 4.0+ incluye soporte nativo para compose
cd backend
podman compose up
podman compose up -d
podman compose stop
```

> **💡 Nota**: Si encuentras problemas de resolución DNS con docker-compose, usa Podman Pod en su lugar.

##### Publicar imagen con Podman

```bash
# Login en Docker Hub
podman login docker.io

# Push de la imagen
podman push isidromerayo/spring-backend-tfg:X.Y.Z
```

#### Alias para Compatibilidad

Si prefieres usar los comandos de Docker pero con Podman:

```bash
# Añadir a ~/.bashrc o ~/.zshrc
alias docker=podman
alias docker-compose=podman-compose

# Recargar configuración
source ~/.bashrc  # o source ~/.zshrc
```

Después de esto, todos los comandos `docker` usarán Podman automáticamente.

#### Diferencias Importantes

| Aspecto | Docker | Podman |
|---------|--------|--------|
| **Daemon** | Requiere daemon corriendo | Sin daemon (daemonless) |
| **Root** | Requiere root o grupo docker | Puede ejecutarse sin root |
| **Arquitectura** | Cliente-servidor | Proceso directo |
| **Compatibilidad** | Estándar de facto | Compatible con Docker |
| **Seguridad** | Buena | Mejor (rootless) |
| **Kubernetes** | Requiere conversión | Genera YAML nativamente |

#### Comandos Útiles de Podman

```bash
# Listar contenedores
podman ps
podman ps -a  # Incluir detenidos

# Ver logs
podman logs postgres-tfg
podman logs -f api_service  # Seguir logs en tiempo real

# Detener y eliminar contenedores
podman stop postgres-tfg
podman rm postgres-tfg

# Listar imágenes
podman images

# Eliminar imagen
podman rmi isidromerayo/spring-backend-tfg:X.Y.Z

# Limpiar recursos no usados
podman system prune -a

# Generar YAML de Kubernetes desde contenedor
podman generate kube postgres-tfg > postgres-k8s.yaml
```

#### Troubleshooting Podman

**Problema: "permission denied" al acceder a puertos < 1024**
```bash
# Solución: Usar puertos > 1024 o configurar
echo "net.ipv4.ip_unprivileged_port_start=80" | sudo tee /etc/sysctl.d/podman-ports.conf
sudo sysctl --system
```

**Problema: "cannot find image locally"**
```bash
# Especificar el registry completo
podman pull docker.io/isidromerayo/postgres-tfg:latest
```

**Problema: Podman compose no funciona**
```bash
# Verificar versión de Podman
podman --version  # Debe ser 4.0+ para soporte nativo

# O instalar podman-compose
pip3 install podman-compose
```

#### Recursos Adicionales

- 📚 [Documentación oficial de Podman](https://docs.podman.io/)
- 🔄 [Guía de migración Docker → Podman](https://podman.io/getting-started/migration)
- 🎓 [Tutorial de Podman](https://github.com/containers/podman/blob/main/docs/tutorials/podman_tutorial.md)
- 🐙 [Podman Desktop](https://podman-desktop.io/) - GUI para Podman

---

#### OWASP Dependency Check
`mvn org.owasp:dependency-check-maven:check`

Si tenemos un API KEY del servicio
 
`mvn org.owasp:dependency-check-maven:check -Dnvd.api.key=XXXX`

### 📦 Distribución

El proyecto está configurado para desplegar en un repositorio local Maven:
- Ubicación: `~/.m2/repository-local`
- Para cambiar el repositorio, modificar `<distributionManagement>` en el pom.xml

#### Maven release

Preparar el release

`./mvnw release:prepare`

modo batch (no pregunta)

`./mvnw release:prepare -B`

Este comando realiza varias acciones:

* Verifica que no haya cambios sin commitear.
* Actualiza la versión en el pom.xml (por ejemplo, de 1.0-SNAPSHOT a 1.0).
* Hace commit de los cambios.
* Crea un tag en el sistema de control de versiones (Git, SVN, etc.).
* Actualiza el pom.xml a la siguiente versión de desarrollo (por ejemplo, 1.1-SNAPSHOT).

Hacer el release (deploy)

`./mvnw release:perform -Dmaven.javadoc.skip=true`

Este comando:

* Clona el proyecto desde el tag creado.
* Compila y despliega el artefacto al repositorio definido en <distributionManagement>.

---

## 📚 Documentación

### Guías de desarrollo

- **[AGENTS.md](AGENTS.md)** - Guía para agentes AI y flujo de trabajo
- **[MANUAL_WORKFLOW_SETUP.md](docs/workflows/github/MANUAL_WORKFLOW_SETUP.md)** - Configuración manual de workflows

### Calidad de código

- **[COVERAGE_ANALYSIS.md](docs/quality/COVERAGE_ANALYSIS.md)** - Análisis detallado de cobertura de código
- **[JACOCO_CONFIGURATION.md](docs/quality/JACOCO_CONFIGURATION.md)** - Configuración de JaCoCo (UT + IT + Merge)
- **[SONARQUBE_POM_CONFIG.md](docs/quality/SONARQUBE_POM_CONFIG.md)** - Configuración de SonarQube en pom.xml
- **[SONARQUBE_ISSUES.md](docs/quality/SONARQUBE_ISSUES.md)** - Análisis de issues detectados por SonarQube

### Monorepo

- **[MONOREPO_WORKFLOW_DISTRIBUTION.md](docs/workflows/github/MONOREPO_WORKFLOW_DISTRIBUTION.md)** - Distribución de workflows
- **[SETUP_MONOREPO_SYNC.md](docs/workflows/github/SETUP_MONOREPO_SYNC.md)** - Sincronización con monorepo

---

## 🎯 Métricas de Calidad

| Métrica | Valor | Objetivo | Estado |
|---------|-------|----------|--------|
| **Cobertura** | 85% | ≥ 80% | ✅ |
| **Tests** | 15 (11 UT + 4 IT) | - | ✅ |
| **Reliability Rating** | A | A | ✅ |
| **Security Rating** | A | A | ✅ |
| **Quality Gate** | Passed | Passed | ✅ |

**Última actualización**: 2026-06-29 (Release v0.6.2)

Ver más detalles en [SonarCloud](https://sonarcloud.io/project/overview?id=isidromerayo_TFG_UNIR-backend)

---

# Badges

[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=isidromerayo_TFG_UNIR-backend)](https://sonarcloud.io/summary/new_code?id=isidromerayo_TFG_UNIR-backend)

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/381fca2f4da04e269a7dbd6a983519e3)](https://app.codacy.com/gh/isidromerayo/TFG_UNIR-backend/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
