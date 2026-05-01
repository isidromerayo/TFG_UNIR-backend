# Documentación del Proyecto TFG UNIR Backend

**Última actualización**: 2026-05-01

---

## 📖 Documentación Principal

### [../README.md](../README.md)
Documentación principal del proyecto con información general, stack tecnológico, comandos básicos y métricas de calidad.

### [../AGENTS.md](../AGENTS.md)
Guía para agentes AI y desarrolladores con flujo de trabajo, comandos de testing, y convenciones de código.

---

## 📁 Estructura de Documentación

### `/quality/`
Documentación relacionada con calidad de código, cobertura y análisis estático.

- **[COVERAGE_ANALYSIS.md](quality/COVERAGE_ANALYSIS.md)** - Análisis detallado de cobertura de código
- **[JACOCO_CONFIGURATION.md](quality/JACOCO_CONFIGURATION.md)** - Configuración de JaCoCo para tests
- **[SONARQUBE_ISSUES.md](quality/SONARQUBE_ISSUES.md)** - Issues detectados por SonarQube
- **[SONARQUBE_POM_CONFIG.md](quality/SONARQUBE_POM_CONFIG.md)** - Configuración de SonarQube en pom.xml
- **[SONARQUBE_SETUP.md](quality/SONARQUBE_SETUP.md)** - Configuración inicial de SonarQube
- **[CODACY_ISSUES.md](quality/CODACY_ISSUES.md)** - Análisis de issues de Codacy

### `/security/`
Documentación de seguridad, autenticación y buenas prácticas.

- **[SECURITY.md](security/SECURITY.md)** - Guía de seguridad del proyecto
- **[SECURITY_BCRYPT.md](security/SECURITY_BCRYPT.md)** - Migración a BCrypt
- **[README.md](security/README.md)** - Índice de documentación de seguridad
- **[LESSONS_LEARNED.md](security/LESSONS_LEARNED.md)** - Lecciones aprendidas en migración
- **[QUICK_START_BCRYPT.md](security/QUICK_START_BCRYPT.md)** - Inicio rápido con BCrypt
- **[BUILD_AND_TEST_BCRYPT.md](security/BUILD_AND_TEST_BCRYPT.md)** - Scripts de testing BCrypt
- **[BCRYPT_MIGRATION_SUMMARY.md](security/BCRYPT_MIGRATION_SUMMARY.md)** - Resumen de migración
- **[SNYK_SECURITY_ISSUE.md](security/SNYK_SECURITY_ISSUE.md)** - Issue de seguridad Snyk
- **[PR_SNYK_TIMING_ATTACK.md](security/PR_SNYK_TIMING_ATTACK.md)** - PR sobre timing attacks

### `/workflows/`
Configuración de CI/CD, GitHub Actions y workflows.

- **[MANUAL_WORKFLOW_SETUP.md](workflows/MANUAL_WORKFLOW_SETUP.md)** - Configuración manual de workflows
- **[MONOREPO_WORKFLOW_DISTRIBUTION.md](workflows/MONOREPO_WORKFLOW_DISTRIBUTION.md)** - Distribución en monorepo
- **[SETUP_MONOREPO_SYNC.md](workflows/SETUP_MONOREPO_SYNC.md)** - Sincronización con monorepo
- **[/github/](workflows/github/)** - Plantillas de workflows de GitHub Actions

### `/docker/`
Guías de Docker, Podman y contenedores.

- **[DOCKER_WORKFLOW.md](docker/DOCKER_WORKFLOW.md)** - Workflow con Docker
- **[DOCKER_IMAGES_GUIDE.md](docker/DOCKER_IMAGES_GUIDE.md)** - Guía de imágenes Docker
- **[DOCKER_IMAGE_RELEASE_0.4.0.md](docker/DOCKER_IMAGE_RELEASE_0.4.0.md)** - Release 0.4.0
- **[PODMAN_GUIDE.md](docker/PODMAN_GUIDE.md)** - Guía de Podman

### `/database/`
Documentación sobre bases de datos y migraciones.

- **[MARIADB_MYSQL_GUIDE.md](database/MARIADB_MYSQL_GUIDE.md)** - Guía de MariaDB/MySQL
- **[PR_POSTGRESQL_MIGRATION.md](database/PR_POSTGRESQL_MIGRATION.md)** - Migración a PostgreSQL

### `/migration/`
Documentación sobre migraciones de versión y reorganizaciones.

- **[REORGANIZATION_SUMMARY.md](migration/REORGANIZATION_SUMMARY.md)** - Resumen de reorganización
- **[SPRING_BOOT_3.5_MIGRATION.md](migration/SPRING_BOOT_3.5_MIGRATION.md)** - Migración a Spring Boot 3.5
- **[MIGRATION_EXECUTION_GUIDE.md](migration/MIGRATION_EXECUTION_GUIDE.md)** - Guía de ejecución
- **[SPRING_BOOT_LIFECYCLE.md](migration/SPRING_BOOT_LIFECYCLE.md)** - Ciclo de vida Spring Boot
- **[/plan/](migration/plan/)** - Planes de migración detallados

### `/releases/`
Documentación de versiones y changelogs.

- **[CHANGELOG_IMAGES.md](releases/CHANGELOG_IMAGES.md)** - Changelog de imágenes Docker

### `/skills/`
Documentación sobre skills para agentes AI.

- **[USING_SKILLS.md](skills/USING_SKILLS.md)** - Guía de uso de skills
- **[SKILLS_ANALYSIS_REPORT.md](skills/SKILLS_ANALYSIS_REPORT.md)** - Análisis de skills

---

## 🚀 Guías Rápidas

### Ejecutar Tests
```bash
# Tests unitarios
./mvnw test

# Tests de integración
./mvnw -Pintegration-tests verify

# Todos los tests con cobertura
./mvnw clean verify -Pintegration-tests
```

### Ver Reportes de Cobertura
```bash
# Generar reportes
./mvnw clean verify -Pintegration-tests

# Abrir reportes
open target/site/jacoco/index.html      # Combinado (principal)
open target/site/jacoco-ut/index.html   # Solo unitarios
open target/site/jacoco-it/index.html   # Solo integración
```

### Análisis de Calidad
```bash
# SpotBugs
./mvnw compile spotbugs:check

# SonarQube (requiere token)
./mvnw sonar:sonar -Dsonar.token=${SONAR_TOKEN}
```

---

## 📈 Métricas Actuales

| Métrica | Valor | Objetivo | Estado |
|---------|-------|----------|--------|
| **Cobertura de código** | 100%* | ≥ 80% | ✅ |
| **Tests unitarios** | 11 | - | ✅ |
| **Tests integración** | 4 | - | ✅ |
| **Spring Boot** | 3.5.11 | Latest | ✅ |
| **Java** | 21 | LTS | ✅ |
| **Quality Gate** | Passed | Passed | ✅ |

*En paquetes incluidos (excluye model/, dto/, config/)

---

## 🔗 Enlaces Externos

- **SonarCloud**: https://sonarcloud.io/project/overview?id=isidromerayo_TFG_UNIR-backend
- **GitHub Actions**: https://github.com/isidromerayo/TFG_UNIR-backend/actions
- **Repositorio**: https://github.com/isidromerayo/TFG_UNIR-backend

---

## 🆘 Ayuda

Si tienes dudas sobre:
- **Desarrollo**: Ver [../AGENTS.md](../AGENTS.md)
- **Cobertura**: Ver [quality/COVERAGE_ANALYSIS.md](quality/COVERAGE_ANALYSIS.md)
- **SonarQube**: Ver [quality/SONARQUBE_POM_CONFIG.md](quality/SONARQUBE_POM_CONFIG.md)
- **Seguridad**: Ver [security/README.md](security/README.md)
- **Tests**: Ver sección "Tests" en [../README.md](../README.md)
