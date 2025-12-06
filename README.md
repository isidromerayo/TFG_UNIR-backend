# TFG_UNIR Backend

[![Java CI with Maven](https://github.com/isidromerayo/TFG_UNIR-backend/actions/workflows/maven.yml/badge.svg)](https://github.com/isidromerayo/TFG_UNIR-backend/actions/workflows/maven.yml)
[![Pull Request CI](https://github.com/isidromerayo/TFG_UNIR-backend/actions/workflows/pull-request.yml/badge.svg)](https://github.com/isidromerayo/TFG_UNIR-backend/actions/workflows/pull-request.yml)

### Universidad Internacional de La Rioja

### Escuela Superior de Ingeniería y Tecnología 

#### Grado en Ingeniería Informática

## Frameworks frontend JavaScript: Análisis y estudio práctico

### Backend

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

### 🛠️ Stack tecnológico

- **Java 21**
- **Spring Boot 3.4.12**
- **Spring Data JPA** - Persistencia
- **Spring Security** - Autenticación y autorización
- **MariaDB** - Base de datos producción
- **H2** - Base de datos testing
- **JWT** - Tokens de autenticación
- **Swagger/OpenAPI** - Documentación API
- **Lombok** - Reducción de boilerplate
- **JaCoCo** - Cobertura de código
- **SpotBugs** - Análisis estático
- **Docker/Podman** - Containerización

### 🧪 Tests

```bash
# Tests unitarios
mvn test

# Tests de integración
mvn -DskipUTs -Pfailsafe verify

# Todos los tests
mvn verify -Pfailsafe
```

### 📦 Perfiles de Maven

```bash
# Perfil para tests de integración
mvn verify -Pfailsafe

# Perfil para análisis de dependencias
mvn verify -Pdependency-check
```

### 🔍 Análisis de código

```bash
# Cobertura de código con JaCoCo
mvn jacoco:report

# Análisis estático con SpotBugs
mvn spotbugs:check

# SpotBugs con plugins de seguridad
mvn spotbugs:spotbugs

# OWASP Dependency Check (perfil activado)
mvn -Pdependency-check verify
```

### 🔐 Autenticación

El backend utiliza JWT (JSON Web Tokens) para la autenticación.
- Librería: `jjwt` v0.13.0
- Configuración de seguridad con Spring Security

Es necesaria una versión de Java 21, para utilizar Spring Boot 3.4.x

```
cd backend
./mvnw clean install
```

#### BBDD: MariaDB para producción

```
$ mariadb --version
mariadb  Ver 15.1 Distrib 10.3.38-MariaDB, for debian-linux-gnu (x86_64) using readline 5.2
```

##### Crear base de datos y usuario

```
MariaDB [(none)]> create database tfg_unir; -- create NEW database
MariaDB [(none)]> create user 'user_tfg'@'%' identified by 'tfg_un1r_PWD'; -- create user
MariaDB [(none)]> grant all on tfg_unir.* to 'user_tfg'@'%'; -- give all privileges to the user
```

##### Carga inicial de datos

Los recursos están el proyecto [TFG UNIR](https://github.com/isidromerayo/TFG_UNIR)

```
mariadb -u user_tfg -ptfg_un1r_PWD tfg_unir < recursos/db/dump.mariadb.sql 
```

Backup con mysql de datos

```
mysqldump -u user_tfg -ptfg_un1r_PWD tfg_unir > recursos/db/dump.mariadb.sql 
```

#### 🐳 Containerización con Docker/Podman

> **Nota**: Este proyecto soporta tanto Docker como Podman. Todos los comandos `docker` pueden reemplazarse por `podman`. Ver la [sección de Podman](#-soporte-para-podman) para más detalles.

#### Docker MariaDB

Carga inicial para utilizar por el backend con Spring Boot con datos de pruebas del directorio recursos/db

`docker run --name mariadb-tfg -p 3306:3306 -d isidromerayo/mariadb-tfg`

Para publicar en docker hub `docker push isidromerayo/mariadb-tfg:X.Y.Z`

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

Construir imagen de aplicación con el jar generado del backend (con el `spring.datasource.url=jdbc:mariadb://app_db:3306/tfg_unir` en el application.properties) hay que ejecutar un maven para generar


```
cd backend
./mvnw clean install
docker build -t isidromerayo/spring-backend-tfg:VERSION-X.Y.Z .
```

https://spring.io/guides/topicals/spring-boot-docker/
https://javatodev.com/docker-compose-for-spring-boot-with-mariadb/

#### docker compose

Con docker compose se montará un contendor con MariaDB (datos precargados) y otro con la aplicación de Spring Boot 3 con el API 

> **⚠️ Prerequisitos**:
> 1. Los archivos SQL deben existir en `../recursos/db/`:
>    - `create.mariadb.sql` - Script de creación de esquema
>    - `dump.mariadb.sql` - Datos iniciales
> 2. El `application.properties` debe apuntar al alias de docker 'app_db':
>    ```properties
>    spring.datasource.url=jdbc:mariadb://app_db:3306/tfg_unir
>    ```

```
cd backend
docker compose up
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
 ✔ Container backend-api_service-1  Stopped     0.3s 
 ✔ Container backend-maria_db-1     Stopped     0.5s 
```

##### Publicar imagen en docker

Deberemos estar logeados en nuestra cuenta de docker

`docker push isidromerayo/spring-backend-tfg:X.Y.Z`

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

##### Ejecutar MariaDB con Podman

```bash
# Equivalente a: docker run --name mariadb-tfg -p 3306:3306 -d isidromerayo/mariadb-tfg
podman run --name mariadb-tfg -p 3306:3306 -d isidromerayo/mariadb-tfg
```

##### Construir imagen del backend con Podman

> **⚠️ Importante**: El `Dockerfile` usa `docker.io/eclipse-temurin:21-jdk` (registry completo) para asegurar compatibilidad con Podman y Docker. Podman requiere especificar el registry explícitamente.

```bash
cd backend
./mvnw clean install
podman build -t isidromerayo/spring-backend-tfg:VERSION-X.Y.Z .
```

##### Usar Podman Compose

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
podman logs mariadb-tfg
podman logs -f api_service  # Seguir logs en tiempo real

# Detener y eliminar contenedores
podman stop mariadb-tfg
podman rm mariadb-tfg

# Listar imágenes
podman images

# Eliminar imagen
podman rmi isidromerayo/spring-backend-tfg:X.Y.Z

# Limpiar recursos no usados
podman system prune -a

# Generar YAML de Kubernetes desde contenedor
podman generate kube mariadb-tfg > mariadb-k8s.yaml
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
podman pull docker.io/isidromerayo/mariadb-tfg:latest
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

`mvn release:prepare`

modo batch (no pregunta)

`mvn release:prepare -B`

Este comando realiza varias acciones:

* Verifica que no haya cambios sin commitear.
* Actualiza la versión en el pom.xml (por ejemplo, de 1.0-SNAPSHOT a 1.0).
* Hace commit de los cambios.
* Crea un tag en el sistema de control de versiones (Git, SVN, etc.).
* Actualiza el pom.xml a la siguiente versión de desarrollo (por ejemplo, 1.1-SNAPSHOT).

Hacer el release (deploy)

`mvn release:perform -Dmaven.javadoc.skip=true`

Este comando:

* Clona el proyecto desde el tag creado.
* Compila y despliega el artefacto al repositorio definido en <distributionManagement>.

# Badges

[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=isidromerayo_TFG_UNIR-backend)](https://sonarcloud.io/summary/new_code?id=isidromerayo_TFG_UNIR-backend)

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/381fca2f4da04e269a7dbd6a983519e3)](https://app.codacy.com/gh/isidromerayo/TFG_UNIR-backend/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
