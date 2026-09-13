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

Migración de riesgo **bajo** — sin breaking changes en código de producción ni de tests.
La única corrección necesaria fue en scripts y documentación (`-DskipTests` → `-Dmaven.test.skip=true`).

Resultado final: **51 tests unitarios + 22 tests de integración en verde**, SpotBugs limpio,
sin cambios en ningún archivo Java de producción o test.

---

## ✅ Cambios realizados

### 1. Actualización del Parent (`pom.xml`)

```xml
<!-- ANTES -->
<parent>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.8</version>
</parent>

<!-- DESPUÉS -->
<parent>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.1.1</version>
</parent>
```

### 2. Overrides de seguridad mantenidos

Boot 4.1.1 **no actualiza** Tomcat ni Jackson respecto a Boot 4.0.8:

| Override | Boot 4.1.1 gestiona | Override mantenido | Motivo |
|---|---|---|---|
| `tomcat.version=11.0.25` | 11.0.24 | ✅ | CVE-2026-73180/68763/68569 (fix en 11.0.25) |
| `jackson-bom.version=3.1.6` | 3.1.5 | ✅ | CVE-2026-19032/83557 (fix en 3.1.6) |
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

---

## 🔧 Cambios descubiertos durante la ejecución

### Ninguno

A diferencia de la migración 3.5 → 4.0 (que requirió adaptar springdoc, test slices modulares,
`TestRestTemplate`, rest-assured BOM y Jackson 2→3), la migración 4.0.8 → 4.1.1 fue completamente
transparente para el código Java:

- Spring Security 7.0 → 7.1: **sin breaking changes** para este proyecto
  (la configuración `WebSecurityConfig` con `SecurityFilterChain` + `@EnableMethodSecurity`
  es compatible sin modificaciones)
- Hibernate 7.2.x → 7.4.5.Final: **sin breaking changes** (solo warning pre-existente
  sobre `@Temporal` deprecated — no nuevo, no requiere acción)
- rest-assured 6.0.1 + TestRestTemplate: **sin cambios necesarios**

---

## 🧪 Verificación

| Comando | Resultado |
|---|---|
| `./mvnw clean compile` | ✅ BUILD SUCCESS |
| `./mvnw clean package -Dmaven.test.skip=true` | ✅ `target/backend.jar` 64MB |
| `./mvnw test` | ✅ 51/51 |
| `./mvnw clean verify -Pintegration-tests` | ✅ 51 unit + 22 IT |
| `./mvnw compile spotbugs:check` | ✅ limpio (SpotBugs 4.10.4 + FindSecBugs 1.14.0) |

Versiones finales del classpath: Tomcat **11.0.25** (override), Spring Framework **7.0.9**,
Spring Security **7.1.1**, Hibernate **7.4.5.Final**, Jackson 3 (**3.1.6**, override),
springdoc **3.1.1**, rest-assured **6.0.1**.

---

## 📅 Seguimiento pendiente

- [ ] OWASP dependency-check scan post-migración con `NVD_API_KEY`
- [ ] Actualizar `AGENTS.md` Known Vulnerabilities con resultados del scan y nuevos falsos positivos
- [ ] Crear `docs/security/informe-vulnerabilidades-<fecha>.md` con el informe del nuevo scan
