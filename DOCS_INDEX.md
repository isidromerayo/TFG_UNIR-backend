# Índice de Documentación - TFG UNIR Backend

**Última actualización**: 2025-12-08

---

## 📖 Documentación Principal

### [README.md](README.md)
Documentación principal del proyecto con información general, stack tecnológico, comandos básicos y métricas de calidad.

---

## 🔧 Desarrollo

### [AGENTS.md](AGENTS.md)
**Guía para agentes AI y desarrolladores**
- Flujo de trabajo para contribuciones
- Ejecución de tests (unitarios e integración)
- Verificación de calidad con SpotBugs
- Creación de commits
- Proceso de push

**Cuándo usar**: Antes de realizar cualquier cambio en el código.

---

## 📊 Calidad de Código

### [COVERAGE_ANALYSIS.md](COVERAGE_ANALYSIS.md)
**Análisis detallado de cobertura de código**
- Resumen general: 85% de cobertura
- Cobertura por paquete
- Áreas que requieren tests
- Plan de mejora para alcanzar objetivos
- Exclusiones configuradas (DTOs y entidades)

**Cuándo usar**: Para entender el estado actual de cobertura y planificar mejoras.

### [JACOCO_CONFIGURATION.md](JACOCO_CONFIGURATION.md)
**Configuración de JaCoCo para tests unitarios e integración**
- Confirmación de reportes separados (UT, IT, Merged)
- Archivos generados (.exec y HTML)
- Ejemplo de cobertura: LoginController
- Comandos para generar reportes
- Integración con SonarQube

**Cuándo usar**: Para entender cómo funciona la cobertura de código o modificar la configuración.

### [SONARQUBE_POM_CONFIG.md](SONARQUBE_POM_CONFIG.md)
**Configuración de SonarQube en pom.xml**
- Propiedades de SonarQube
- Exclusiones de cobertura
- Cómo ejecutar análisis
- Verificación de configuración
- Impacto esperado en métricas

**Cuándo usar**: Para configurar o modificar el análisis de SonarQube.

### [SONARQUBE_ISSUES.md](SONARQUBE_ISSUES.md)
**Análisis de issues detectados por SonarQube**
- Resumen por severidad (BLOCKER, CRITICAL, MAJOR, MINOR, INFO)
- Descripción detallada de cada issue
- Soluciones implementadas
- Plan de acción

**Cuándo usar**: Para revisar y resolver issues de calidad de código.

---

## 🔄 Workflows y CI/CD

### [MANUAL_WORKFLOW_SETUP.md](MANUAL_WORKFLOW_SETUP.md)
**Configuración manual de workflows de GitHub Actions**
- Setup de workflows CI/CD
- Configuración de secretos
- Integración con SonarQube

**Cuándo usar**: Al configurar o modificar workflows de GitHub Actions.

---

## 🏗️ Monorepo

### [MONOREPO_WORKFLOW_DISTRIBUTION.md](MONOREPO_WORKFLOW_DISTRIBUTION.md)
**Distribución de workflows en monorepo**
- Estructura de workflows
- Sincronización entre repositorios

**Cuándo usar**: Al trabajar con la estructura de monorepo.

### [SETUP_MONOREPO_SYNC.md](SETUP_MONOREPO_SYNC.md)
**Sincronización con monorepo**
- Configuración de subtrees
- Comandos de sincronización

**Cuándo usar**: Al sincronizar cambios con el monorepo principal.

---

## 🚀 Guías Rápidas

### Ejecutar Tests

```bash
# Tests unitarios
./mvnw test

# Tests de integración
./mvnw -DskipUTs -Pfailsafe verify

# Todos los tests con cobertura
./mvnw clean verify -Pfailsafe
```

### Ver Reportes de Cobertura

```bash
# Generar reportes
./mvnw clean verify -Pfailsafe

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
| **Cobertura de código** | 85% | ≥ 80% | ✅ |
| **Tests unitarios** | 11 | - | ✅ |
| **Tests integración** | 4 | - | ✅ |
| **Reliability Rating** | A | A | ✅ |
| **Security Rating** | A | A | ✅ |
| **Quality Gate** | Passed | Passed | ✅ |

---

## 🔗 Enlaces Externos

- **SonarCloud**: https://sonarcloud.io/project/overview?id=isidromerayo_TFG_UNIR-backend
- **GitHub Actions**: https://github.com/isidromerayo/TFG_UNIR-backend/actions
- **Repositorio**: https://github.com/isidromerayo/TFG_UNIR-backend

---

## 📝 Notas

### Exclusiones de Cobertura

Los siguientes archivos están excluidos del análisis de cobertura:
- `**/model/**` - Entidades JPA (POJOs con Lombok)
- `**/controller/User.java` - DTO simple
- `**/controller/FormUser.java` - DTO simple

**Justificación**: Son clases sin lógica de negocio, validadas implícitamente en tests de integración.

### Configuración de JaCoCo

- **Tests unitarios**: `jacoco-ut.exec` → `target/site/jacoco-ut/`
- **Tests integración**: `jacoco-it.exec` → `target/site/jacoco-it/`
- **Reporte combinado**: `jacoco-merged.exec` → `target/site/jacoco/` ⭐

El reporte combinado toma el **máximo de cobertura** de ambos tipos de tests.

---

## 🆘 Ayuda

Si tienes dudas sobre:
- **Desarrollo**: Ver [AGENTS.md](AGENTS.md)
- **Cobertura**: Ver [COVERAGE_ANALYSIS.md](COVERAGE_ANALYSIS.md)
- **SonarQube**: Ver [SONARQUBE_POM_CONFIG.md](SONARQUBE_POM_CONFIG.md)
- **Tests**: Ver sección "Tests" en [README.md](README.md)
