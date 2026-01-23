# Guía para Agentes AI

Este documento proporciona directrices para agentes de inteligencia artificial que trabajen en este repositorio.

## Descripción del Proyecto

Este es un proyecto backend desarrollado en Java con el framework Spring Boot. Utiliza Maven para la gestión de dependencias y el ciclo de vida de la compilación.

### Tecnologías Clave
- **Lenguaje:** Java 17
- **Framework:** Spring Boot 4.0.2
- **Spring Framework:** 7.0.3
- **Hibernate:** 7.2.1.Final
- **Gestor de dependencias:** Maven
- **Base de datos:** H2 (tests), MariaDB (producción)
- **Seguridad:** Spring Security 7.0.2 + JWT (JSON Web Tokens)
- **Testing:** JUnit 5, Mockito, REST Assured (integración)
- **Calidad:** SpotBugs, JaCoCo (cobertura), SonarCloud
- **Herramientas:** vfox (gestión versiones Java)

## Flujo de Trabajo para Contribuciones

Para asegurar la calidad y estabilidad del código, sigue estos pasos al realizar cambios:

1.  **Análisis y Desarrollo:**
    *   Comprende la tarea y el código existente antes de modificarlo.
    *   Aplica los cambios siguiendo las convenciones de código y patrones de diseño ya establecidos en el proyecto.
    *   Crear una rama nueva cuando se vaya a realizar una nueva funcionalidad o modificación.
    *   **Ejecutar los tests antes de realizar cambios significativos** para asegurarse de que no se rompe nada.

2.  **Ejecución de Tests:**
    *   **EXCEPCIÓN**: Si ÚNICAMENTE se modifican archivos de documentación (*.md, *.txt, comentarios, archivos en `/docs/`, steering files), se puede omitir la ejecución de tests.
    *   Para **cambios de código**, es **mandatorio** ejecutar la suite completa de tests (unitarios e integración). Esto asegura que tus cambios no han roto ninguna funcionalidad existente (regresiones).
    *   **Tests completos (unitarios + integración):**
        ```bash
        ./mvnw -Pintegration-tests verify
        ```
    *   **Solo tests unitarios (para desarrollo rápido):**
        ```bash
        ./mvnw test
        ```
    *   **Solo tests de integración:**
        ```bash
        ./mvnw -DskipUTs -Pintegration-tests verify
        ```
    *   Cuando fallen los test y se este arreglando, centrarse primero en lanzar solo los que fallan en lugar de lanzar todos siempre:
        - Tests unitarios específicos: `./mvnw test -Dtest=NombreDelTest`
        - Tests de integración específicos: `./mvnw -Pintegration-tests verify -Dit.test=NombreDelTestIT`
    *   Aplicar TDD a la hora de implementar
    *   Preferiblemente preparar los datos como carga inicial en BBDD en lugar de crear en los test

3.  **Verificación de Calidad del Código con SpotBugs:**
    *   **Solo para cambios de código** (omitir si solo se modifica documentación).
    *   Antes de considerar tu trabajo finalizado y listo para un commit, ejecuta un análisis estático de código con SpotBugs para detectar bugs potenciales y malas prácticas.
    *   Utiliza el siguiente comando para lanzar el análisis:
        ```bash
        ./mvnw compile spotbugs:check
        ```

4.  **Creación de Commits:**
    *   Asegúrate de que todos los tests se ejecutan correctamente y que el análisis de SpotBugs no reporta problemas críticos.
    *   Escribe un mensaje de commit claro y descriptivo.

5.  **Subir cambios al repositorio remoto (git push):**
    *   **Para cambios de código**: Asegúrate de que todos los tests se ejecutan correctamente con el comando:
        ```bash
        ./mvnw clean verify -Pintegration-tests
        ```
    *   **Para cambios solo de documentación**: Se puede hacer push directamente después del commit.
    *   Para mantener la calidad, revisar que se siguen las guías de SonarQube

El cumplimiento de estos pasos es fundamental para mantener la integridad del proyecto.

## Convenciones de Commits

Utiliza [Conventional Commits](https://www.conventionalcommits.org/) para mensajes claros:

- `feat:` - Nueva funcionalidad
- `fix:` - Corrección de bugs
- `docs:` - Solo cambios en documentación
- `test:` - Añadir o modificar tests
- `refactor:` - Refactorización sin cambiar funcionalidad
- `chore:` - Tareas de mantenimiento (dependencias, configuración)
- `perf:` - Mejoras de rendimiento

**Ejemplos:**
```bash
git commit -m "feat: add JWT token refresh endpoint"
git commit -m "fix: resolve null pointer in LoginController"
git commit -m "docs: update SECURITY.md with vulnerability policy"
git commit -m "test: add integration tests for authentication flow"
```

## Cobertura de Código

El proyecto mantiene una cobertura de código del **85%** (objetivo: ≥80%).

### Generar reportes de cobertura

```bash
# Generar todos los reportes (UT + IT + Merged)
./mvnw clean verify -Pintegration-tests

# Ver reportes en navegador
open target/site/jacoco/index.html      # Reporte combinado (principal)
open target/site/jacoco-ut/index.html   # Solo tests unitarios
open target/site/jacoco-it/index.html   # Solo tests de integración
```

### Exclusiones de cobertura

Los siguientes archivos están excluidos del análisis de cobertura:
- `**/model/**` - Entidades JPA (POJOs con Lombok)
- `**/controller/User.java` - DTO simple
- `**/controller/FormUser.java` - DTO simple

Ver más detalles en [COVERAGE_ANALYSIS.md](COVERAGE_ANALYSIS.md) y [JACOCO_CONFIGURATION.md](JACOCO_CONFIGURATION.md).

## Análisis de Seguridad

### OWASP Dependency Check

Ejecutar análisis de vulnerabilidades en dependencias:

```bash
# Sin API key (más lento)
./mvnw -Pdependency-check verify

# Con API key del NVD (recomendado)
./mvnw -Pdependency-check verify -Dnvd.api.key=${NVD_API_KEY}
```

### Reportar vulnerabilidades

Si encuentras vulnerabilidades de seguridad, sigue la [Security Policy](SECURITY.md).

## Gestión de Versiones con Maven Release Plugin

El proyecto utiliza el **Maven Release Plugin** para gestionar versiones de forma estándar y automatizada.

### Proceso de Release

El proceso de release consta de dos comandos principales:

#### 1. `mvn release:prepare`

Prepara la release automáticamente:
- Verifica que no hay cambios sin commitear
- Cambia la versión de `X.Y.Z-SNAPSHOT` a `X.Y.Z`
- Crea un commit con la versión de release
- Crea un tag Git `vX.Y.Z`
- Cambia la versión a `X.Y.(Z+1)-SNAPSHOT` para el siguiente desarrollo
- Hace push de commits y tags a GitHub

```bash
./mvnw release:prepare -DskipTests -Darguments="-DskipTests"
```

**Interactivo:** El comando preguntará:
- Versión de release (ej: `0.3.1`)
- Tag de SCM (ej: `v0.3.1`)
- Nueva versión de desarrollo (ej: `0.3.2-SNAPSHOT`)

#### 2. `mvn release:perform`

Ejecuta la release:
- Hace checkout del tag creado
- Compila el proyecto desde el tag
- Ejecuta tests (si no se especifica `-DskipTests`)
- Despliega el artefacto al repositorio configurado

```bash
./mvnw release:perform -DskipTests -Darguments="-DskipTests"
```

### Ejemplo Completo

```bash
# 1. Asegurarse de que todo está commiteado
git status

# 2. Preparar la release
./mvnw release:prepare -DskipTests -Darguments="-DskipTests"
# Responder a las preguntas interactivas

# 3. Ejecutar la release
./mvnw release:perform -DskipTests -Darguments="-DskipTests"

# 4. Verificar el artefacto desplegado
ls -lh ~/.m2/repository-local/eu/estilolibre/tfgunir/backend/0.3.1/

# 5. Verificar tags en GitHub
git tag
git ls-remote --tags origin
```

### Versionado Semántico

El proyecto sigue [Semantic Versioning 2.0.0](https://semver.org/):

- **MAJOR** (X.0.0): Cambios incompatibles en la API
- **MINOR** (0.X.0): Nueva funcionalidad compatible hacia atrás
- **PATCH** (0.0.X): Correcciones de bugs compatibles hacia atrás

**Ejemplos:**
- `0.3.0` → `0.3.1`: Página de inicio agregada (patch)
- `0.3.0` → `0.4.0`: Nueva API REST endpoint (minor)
- `1.0.0` → `2.0.0`: Cambio en estructura de respuestas API (major)

### Construcción de Imágenes Docker

Después de la release, construir y publicar imágenes Docker:

```bash
# 1. Checkout del tag de release
git checkout v0.3.1

# 2. Construir imagen con Podman/Docker
podman build -f Dockerfile \
  -t isidromerayo/spring-backend-tfg:0.3.1 \
  -t isidromerayo/spring-backend-tfg:latest .

# 3. Publicar a Docker Hub (requiere login)
podman login docker.io
podman push isidromerayo/spring-backend-tfg:0.3.1
podman push isidromerayo/spring-backend-tfg:latest

# 4. Volver a la rama de desarrollo
git checkout fix/snyk-timing-attack-password
```

### Configuración del Plugin

El plugin está configurado en `pom.xml`:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-release-plugin</artifactId>
    <version>3.1.1</version>
    <configuration>
        <tagNameFormat>v@{project.version}</tagNameFormat>
    </configuration>
</plugin>
```

### Troubleshooting

**Problema:** Error "No SCM URL was provided"
```bash
# Solución: Verificar que existe release.properties
ls -la release.properties
# Si no existe, ejecutar release:prepare primero
```

**Problema:** Cambios sin commitear
```bash
# Solución: Commitear o descartar cambios
git status
git add .
git commit -m "chore: prepare for release"
```

**Problema:** Tag ya existe
```bash
# Solución: Eliminar tag local y remoto
git tag -d v0.3.1
git push origin :refs/tags/v0.3.1
```

## Recursos Adicionales

- **[README.md](README.md)** - Documentación principal del proyecto
- **[DOCS_INDEX.md](DOCS_INDEX.md)** - Índice completo de documentación
- **[COVERAGE_ANALYSIS.md](COVERAGE_ANALYSIS.md)** - Análisis de cobertura detallado
- **[SONARQUBE_POM_CONFIG.md](SONARQUBE_POM_CONFIG.md)** - Configuración de SonarQube
- **[SECURITY.md](SECURITY.md)** - Política de seguridad
- **[CHANGELOG_IMAGES.md](CHANGELOG_IMAGES.md)** - Historial de versiones de imágenes Docker

---

**Última actualización:** 2025-12-09
