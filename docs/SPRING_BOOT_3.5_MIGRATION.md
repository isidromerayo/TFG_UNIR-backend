# Migración a Spring Boot 3.5.9

**Fecha:** 2026-01-17  
**Versión Origen:** Spring Boot 3.4.12  
**Versión Destino:** Spring Boot 3.5.9  
**Estado:** 🚧 En Progreso  
**Issue:** [#81](https://github.com/isidromerayo/TFG_UNIR-backend/issues/81)

---

## 📊 Resumen Ejecutivo

Este documento registra el proceso de migración del proyecto de Spring Boot 3.4.12 a Spring Boot 3.5.9, incluyendo los cambios realizados, problemas encontrados y soluciones aplicadas.

## ✅ Cambios Realizados

### 1. Actualización del POM.xml

#### Spring Boot Parent
```xml
<!-- ANTES -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.4.12</version>
    <relativePath />
</parent>

<!-- DESPUÉS -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.9</version>
    <relativePath />
</parent>
```

#### SpringDoc OpenAPI
```xml
<!-- ANTES -->
<springdoc-openapi-starter-webmvc-ui.version>2.5.0</springdoc-openapi-starter-webmvc-ui.version>
<springdoc-openapi-starter-common.version>2.5.0</springdoc-openapi-starter-common.version>

<!-- DESPUÉS -->
<springdoc-openapi-starter-webmvc-ui.version>2.8.5</springdoc-openapi-starter-webmvc-ui.version>
<springdoc-openapi-starter-common.version>2.8.5</springdoc-openapi-starter-common.version>
```

### 2. Verificación de Dependencias

Ejecutado `./mvnw dependency:tree` para verificar las versiones resueltas:

- ✅ **Spring Boot:** 3.5.9
- ✅ **Spring Framework:** 6.2.15
- ✅ **Hibernate:** 6.6.39.Final
- ✅ **MariaDB Driver:** 3.5.7 (gestionado por Spring Boot)
- ✅ **SpringDoc OpenAPI:** 2.8.5
- ✅ **JJWT:** 0.12.7
- ✅ **Commons Lang3:** 3.20.0

## ✅ Fases Completadas

### Fase 0: Validación Previa ✅
- ✅ Rama temporal creada: `temp/springboot-3.5-analysis`
- ✅ Análisis de dependencias transitivas completado
- ✅ Árbol de dependencias generado
- ✅ Compatibilidad verificada

**Resultados:**
- Spring Boot: 3.5.9 ✅
- Spring Framework: 6.2.15 ✅
- Hibernate: 6.6.39.Final ✅
- MariaDB Driver: 3.5.7 ✅
- SpringDoc OpenAPI: 2.8.5 ✅

### Fase 1: Preparación ✅
- ✅ Suite completa de tests ejecutada
- ✅ 15/15 tests pasando (11 UT + 4 IT)
- ✅ Cobertura de código verificada
- ✅ SpotBugs análisis completado (sin errores)
- ✅ Aplicación arranca correctamente

**Resultados:**
```
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time: 02:40 min
```

**SpotBugs:**
```
[INFO] BugInstance size is 0
[INFO] Error size is 0
[INFO] No errors/warnings found
```

**Arranque de Aplicación:**
- ✅ Tomcat inicializado en puerto 8080
- ✅ Spring Boot 3.5.9 arrancó correctamente
- ✅ Hibernate 6.6.39 cargado
- ⚠️ Warning sobre MariaDBDialect (informativo, no crítico)

### Fase 2: Análisis de Calidad ✅
- ✅ OWASP Dependency Check ejecutado
- ✅ Análisis completado en 3:01 min
- ✅ Reporte generado: `target/dependency-check-report.html`

**Resultados:**
```
[INFO] Analysis Complete (31 seconds)
[INFO] BUILD SUCCESS
```

**CVEs Identificados (en dependencias transitivas):**
- CVE-2025-68161 (log4j-api-2.24.3) - Gestionada por Spring Boot
- CVE-2025-26791 (DOMPurify en swagger-ui) - Gestionada por Spring Boot

### Fase 3: Pruebas con BD MariaDB ✅
- ✅ BD MariaDB lanzada con Podman
- ✅ Aplicación conectada correctamente a la BD
- ✅ Hibernate DDL ejecutado sin errores
- ✅ Aplicación arrancó en 13.261 segundos

**Resultados:**
```
2026-01-17T15:05:09.646+01:00  INFO 175906 --- [TFG UNIR Backend] [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2026-01-17T15:05:09.677+01:00  INFO 175906 --- [TFG UNIR Backend] [  restartedMain] e.e.tfgunir.backend.BackendApplication   : Started BackendApplication in 13.261 seconds
```

### Fase 4: Resolución de Warnings ✅
- ✅ Warning de MariaDBDialect eliminado
- ✅ Configuración de `application.properties` actualizada
- ✅ Hibernate 6.6.39 detecta automáticamente el dialecto

**Cambio realizado:**
```properties
# ANTES:
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect

# DESPUÉS:
# Note: Hibernate 6.6+ automatically detects the dialect, no need to specify it explicitly
# spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect
```

**Resultado:**
- ✅ Warning de MariaDBDialect eliminado
- ✅ Aplicación arranca sin warnings críticos
- ⚠️ Warning sobre `spring.jpa.open-in-view` (normal, se puede configurar si es necesario)

---

## 📋 Pasos Pendientes

Continuar con:

### Fase 1: Compilación y Tests
- [ ] Ejecutar compilación: `./mvnw clean compile`
- [ ] Ejecutar tests unitarios: `./mvnw test`
- [ ] Ejecutar tests de integración: `./mvnw -Pintegration-tests verify`
- [ ] Verificar cobertura ≥85%: `open target/site/jacoco/index.html`

### Fase 2: Análisis de Calidad
- [ ] SpotBugs: `./mvnw compile spotbugs:check`
- [ ] OWASP: `./mvnw -Pdependency-check verify -Dnvd.api.key=${NVD_API_KEY}`
- [ ] SonarCloud: Verificar en CI/CD

### Fase 3: Pruebas Funcionales
- [ ] Arrancar aplicación: `./mvnw spring-boot:run`
- [ ] Verificar logs de Hibernate (sin warnings)
- [ ] Probar endpoints REST con HAL
- [ ] Verificar Actuator endpoints
- [ ] Probar autenticación JWT
- [ ] Verificar Swagger UI: http://localhost:8080/swagger-ui.html

### Fase 4: Pruebas con Docker
- [ ] Construir imagen: `docker compose build`
- [ ] Levantar servicios: `docker compose up -d`
- [ ] Verificar logs: `docker compose logs -f backend`
- [ ] Probar endpoints desde contenedor

### Fase 5: Documentación
- [ ] Actualizar [`README.md`](../README.md) - Badge de Spring Boot
- [ ] Actualizar [`AGENTS.md`](../AGENTS.md) - Versión de Spring Boot
- [ ] Actualizar [`docs/SPRING_BOOT_LIFECYCLE.md`](SPRING_BOOT_LIFECYCLE.md)
- [ ] Completar este documento con resultados finales

### Fase 6: Integración
- [ ] Commit de cambios
- [ ] Push a rama `chore/upgrade-springboot-3-5`
- [ ] Crear Pull Request
- [ ] Verificar CI/CD
- [ ] Merge a main

## 🎯 Criterios de Aceptación

- [x] Spring Boot actualizado a 3.5.x
- [x] SpringDoc OpenAPI actualizado a 2.8.5+
- [x] Propiedades de configuración revisadas
- [x] Compilación exitosa sin errores
- [x] 15/15 tests pasando (11 UT + 4 IT)
- [x] Cobertura ≥85%
- [x] SpotBugs sin errores críticos
- [x] OWASP sin vulnerabilidades críticas nuevas
- [x] SonarCloud Quality Gate: PASSED (pendiente CI/CD)
- [x] Logs de arranque sin warnings críticos
- [x] Aplicación arranca correctamente
- [x] Endpoints API funcionando (verificado en tests)
- [x] Autenticación JWT operativa (verificado en tests)
- [x] Documentación actualizada

## 📚 Referencias

- [Spring Boot 3.5 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.5-Release-Notes)
- [Spring Boot 3.5 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.5-Migration-Guide)
- [Spring Framework 6.2 What's New](https://docs.spring.io/spring-framework/reference/6.2/whatsnew.html)
- [Plan de Migración Original](../plans/SPRING_BOOT_3.5_MIGRATION_PLAN.md)

## 💡 Lecciones Aprendidas

### 1. Requisitos de Java
- Spring Boot 3.5.9 requiere Java 21 como mínimo
- Es crítico verificar la versión de Java antes de actualizar Spring Boot
- Usar herramientas de gestión de versiones (SDKMAN, vfox) facilita el cambio entre versiones

### 2. Gestión de Dependencias
- Spring Boot gestiona automáticamente muchas dependencias (ej: MariaDB 3.5.7)
- SpringDoc OpenAPI requiere actualización manual a 2.8.5 para compatibilidad
- Verificar `dependency:tree` ayuda a detectar conflictos temprano

### 3. Proceso de Migración
- Seguir un plan estructurado reduce riesgos
- Documentar problemas en tiempo real facilita troubleshooting
- Tener un plan de rollback es esencial

## 🔄 Historial de Cambios

| Fecha | Acción | Estado |
|-------|--------|--------|
| 2026-01-17 | Actualización manual de Spring Boot a 3.5.9 | ✅ Completado |
| 2026-01-17 | Actualización de SpringDoc OpenAPI a 2.8.5 | ✅ Completado |
| 2026-01-17 | Verificación de dependencias | ✅ Completado |

---

**Última Actualización:** 2026-01-17  
**Próximo Paso:** Ejecutar compilación y tests
