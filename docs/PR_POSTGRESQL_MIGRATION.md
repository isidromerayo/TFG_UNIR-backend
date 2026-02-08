# PR: Migración a PostgreSQL y Release 0.5.0

## Descripción
Esta Pull Request completa la migración de la base de datos de MariaDB a **PostgreSQL** y formaliza el release de la versión **0.5.0** del backend. Se han actualizado todas las dependencias, configuraciones de orquestación y la documentación del proyecto.

## Cambios Principales

### 🐘 Base de Datos y Persistencia
- **Migración a PostgreSQL**: Sustitución del driver MariaDB por el de PostgreSQL en Maven.
- **Scripts SQL**: Nuevos scripts de inicialización específicos para PostgreSQL en `recursos/db/postgresql/`.
- **Configuración**: Actualización de `application.properties` para soportar el nuevo stack y puerto (5432).
- **Soporte Legacy**: Creación de una guía específica para seguir usando MariaDB/MySQL si fuera necesario (`docs/MARIADB_MYSQL_GUIDE.md`).

### 📦 Release 0.5.0
- **Maven Release**: Ejecutado `mvn release:prepare` y `release:perform`.
- **Versionado**: Salto a la versión estable `0.5.0` y preparación de la siguiente iteración `0.5.1-SNAPSHOT`.
- **Versiones de Plugins**: Actualización de plugins de Maven para compatibilidad con Spring Boot 3.5.x.

### 🐳 Infraestructura (Docker/Podman)
- **Nueva Imagen DB**: `isidromerayo/postgres-tfg:1.0` (PostgreSQL 18.1 con datos precargados).
- **Nueva Imagen Backend**: `isidromerayo/spring-backend-tfg:0.5.0`.
- **Orquestación**: `docker-compose.yml` y `podman-pod.sh` actualizados para usar las nuevas imágenes y el volumen `/var/lib/postgresql`.

### 📚 Documentación
- **README.md**: Actualizado con el nuevo stack, versiones y guías de ejecución.
- **AGENTS.md**: Directrices internas actualizadas para PostgreSQL.
- **Índice**: `DOCS_INDEX.md` actualizado con las nuevas guías.

## Verificación Realizada
- [x] **Compilación**: `./mvnw clean package` exitoso.
- [x] **Ejecución Local**: Backend conectando correctamente a PostgreSQL vía Docker.
- [x] **API Testing**: Endpoints `/api/cursos` verificados con datos reales.
- [x] **Health Check**: Actuator reportando estado `UP`.
- [x] **Publicación**: Imágenes subidas y verificadas en Docker Hub.

## Artefactos
- **Docker Backend**: [isidromerayo/spring-backend-tfg:0.5.0](https://hub.docker.com/r/isidromerayo/spring-backend-tfg/tags)
- **Docker DB**: [isidromerayo/postgres-tfg:1.0](https://hub.docker.com/r/isidromerayo/postgres-tfg/tags)
- **Tag Git**: `v0.5.0`
