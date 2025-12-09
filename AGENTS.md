# Guía para Agentes AI

Este documento proporciona directrices para agentes de inteligencia artificial que trabajen en este repositorio.

## Descripción del Proyecto

Este es un proyecto backend desarrollado en Java con el framework Spring Boot. Utiliza Maven para la gestión de dependencias y el ciclo de vida de la compilación.

### Tecnologías Clave
- **Lenguaje:** Java 21
- **Framework:** Spring Boot 3.4.12
- **Gestor de dependencias:** Maven
- **Base de datos:** H2 (tests), MariaDB (producción)
- **Seguridad:** Spring Security + JWT (JSON Web Tokens)
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
        ./mvnw -Pfailsafe verify
        ```
    *   **Solo tests unitarios (para desarrollo rápido):**
        ```bash
        ./mvnw test
        ```
    *   **Solo tests de integración:**
        ```bash
        ./mvnw -DskipUTs -Pfailsafe verify
        ```
    *   Cuando fallen los test y se este arreglando, centrarse primero en lanzar solo los que fallan en lugar de lanzar todos siempre:
        - Tests unitarios específicos: `./mvnw test -Dtest=NombreDelTest`
        - Tests de integración específicos: `./mvnw -Pfailsafe verify -Dit.test=NombreDelTestIT`
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
        ./mvnw clean verify -Pfailsafe
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
./mvnw clean verify -Pfailsafe

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

## Recursos Adicionales

- **[README.md](README.md)** - Documentación principal del proyecto
- **[DOCS_INDEX.md](DOCS_INDEX.md)** - Índice completo de documentación
- **[COVERAGE_ANALYSIS.md](COVERAGE_ANALYSIS.md)** - Análisis de cobertura detallado
- **[SONARQUBE_POM_CONFIG.md](SONARQUBE_POM_CONFIG.md)** - Configuración de SonarQube
- **[SECURITY.md](SECURITY.md)** - Política de seguridad

---

**Última actualización:** 2025-12-09
