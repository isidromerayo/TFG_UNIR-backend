# Migración a Spring Boot 4.1.1

**Fecha:** 2026-09-13
**Versión Origen:** Spring Boot 4.0.8
**Versión Destino:** Spring Boot 4.1.1
**Estado:** ✅ Completada
**Rama:** `chore/spring-boot-4.1.1`
**Motivación:** Fin de soporte OSS de la línea 4.0 (31/12/2026) — bump preventivo a 4.1.x
(plan completo en `docs/migration/plan/SPRING_BOOT_4.1_MIGRATION_PLAN.md`)

---

## 📊 Resumen Ejecutivo

Este documento registra el proceso de migración de Spring Boot 4.0.8 a 4.1.1.
Los cambios se dividen en dos grupos: los **contemplados en el plan inicial** y
los **descubiertos durante la ejecución** (problema → causa raíz → solución).

Resultado final: **51 tests unitarios + 22 tests de integración en verde**,
SpotBugs limpio y OWASP scan con **0 vulnerabilidades reales**.

---

## ✅ Cambios contemplados en el plan inicial

### 1. Actualización del Parent

```xml
<!-- ANTES -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.8</version>
</parent>

<!-- DESPUÉS -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.1.1</version>
</parent>
```

### 2. Overrides de seguridad mantenidos

Boot 4.1.1 **no actualiza** Tomcat ni Jackson respecto a Boot 4.0.8:

| Override | Boot 4.1.1 gestiona | Override mantenido | Motivo |
|---|---|---|---|
| `tomcat.version=11.0.25` | 11.0.24 | ✅ | CVE-2026-73180 (Low), CVE-2026-68763 (Important DoS), CVE-2026-68569 (Important auth bypass) |
| `jackson-bom.version=3.1.6` | 3.1.5 | ✅ | CVE-2026-19032 (CVSS 6.9), CVE-2026-83557 (CVSS 6.3) |
| `git-commit-id-plugin.version=10.0.1` | 9.2.0 | ✅ | Ya tenemos versión más nueva |

Comentarios de los overrides actualizados con los nuevos CVEs de Tomcat.

### 3. Breaking change: `-DskipTests` → `-Dmaven.test.skip=true`

Boot 4.1 breaking change: el `spring-boot-maven-plugin` ya no reacciona a `-DskipTests`
para omitir AOT processing — solo `maven.test.skip` es reconocido por consistencia con otros
plugins core de Maven.

Archivos actualizados (8 ficheros):
- `AGENTS.md` — sección Release Flow
- `scripts/README.md`
- `docs/docker/DOCKER_WORKFLOW.md`
- `docs/docker/DOCKER_IMAGES_GUIDE.md`
- `docs/security/SECURITY_BCRYPT.md`
- `docs/security/LESSONS_LEARNED.md`
- `docs/migration/MIGRATION_EXECUTION_GUIDE.md`
- `README.md`

### 4. Documentación actualizada

- `AGENTS.md`: Stack section → `Spring Boot 4.1.1`, `Spring Security 7.1.1`, `Hibernate 7.4.5.Final`
- `AGENTS.md`: Known Vulnerabilities → actualizado con OWASP scan post-migración
- `README.md`: versión de Spring Boot y Hibernate actualizadas
- `docs/migration/SPRING_BOOT_LIFECYCLE.md`: 4.1 como versión actual, 4.0 como migrado
- `docs/migration/SPRING_BOOT_4.1_MIGRATION.md`: registro de ejecución
- `docs/migration/plan/SPRING_BOOT_4.1_MIGRATION_PLAN.md`: plan detallado

### 5. OWASP dependency-check scan

`./mvnw -Pdependency-check dependency-check:check -Dnvd.api.key=$NVD_API_KEY`:
**0 vulnerabilidades reales** en el classpath.

---

## 🔧 Cambios descubiertos durante la ejecución

### 1. `@Temporal` deprecated en Hibernate 7.4.5.Final

- **Problema:** warings de Hibernate sobre `@Temporal` deprecated (3 modelos: `Curso`, `Instructor`, `Valoracion`)
- **Causa:** Hibernate 7.x (JPA 3.1) infiere correctamente el tipo de fecha sin `@Temporal`
- **Solución:** eliminar `@Temporal` y la importación `jakarta.persistence.Temporal` y `TemporalType` de los 3 modelos

### 2. `CascadeType` eliminado por error en `Curso.java`

- **Problema:** compilación rota — `CascadeType` no encontrado en `@OneToMany`
- **Causa:** en el primer intento de limpieza de imports, eliminé `CascadeType` que se usaba en el modelo `Curso`
- **Solución:** recuperar `CascadeType` en los imports

---

## 🧪 Verificación

| Comando | Resultado |
|---|---|
| `./mvnw clean compile` | ✅ BUILD SUCCESS |
| `./mvnw clean package -Dmaven.test.skip=true` | ✅ `target/backend.jar` 64MB |
| `./mvnw test` | ✅ 51/51 |
| `./mvnw clean verify -Pintegration-tests` | ✅ 51 unit + 22 IT |
| `./mvnw compile spotbugs:check` | ✅ limpio (SpotBugs 4.10.4 + FindSecBugs 1.14.0) |
| `./mvnw -Pdependency-check dependency-check:check` | ✅ 0 reales (2 FPs documentados) |

Versiones finales del classpath: Tomcat **11.0.25** (override), Spring Framework **7.0.9**,
Spring Security **7.1.1**, Hibernate **7.4.5.Final**, Jackson 3 (**3.1.6**, override),
springdoc **3.1.1**, rest-assured **6.0.1**.

---

## 📅 Seguimiento pendiente

- [ ] Configurar el secret `NVD_API_KEY` en GitHub (maintainer)
- [ ] Actualizar workflow CI para ejecutar OWASP scan con `NVD_API_KEY`