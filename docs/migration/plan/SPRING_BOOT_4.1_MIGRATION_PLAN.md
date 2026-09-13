# Plan de Migración a Spring Boot 4.1.1

**Proyecto:** TFG_UNIR-backend
**Versión Actual:** Spring Boot 4.0.8
**Versión Objetivo:** Spring Boot 4.1.1
**Fecha del Plan:** 2026-09-13
**Estado:** 📋 Planificación
**Rama:** `chore/spring-boot-4.1.1`
**Motivación:** Fin de soporte OSS de la línea 4.0 (31/12/2026) — bump preventivo a 4.1.x

---

## 📊 Resumen Ejecutivo

### Motivación

Spring Boot 4.0.x finaliza su soporte OSS el 31/12/2026. Spring Boot 4.1.1 es la última GA
de la línea 4.1.x (publicada 2026-08-20, 0 vulnerabilidades conocidas en su BOM).

- ✅ Continuar recibiendo parches de seguridad y correcciones
- ✅ Acceder a Spring Security 7.1.1 y Hibernate 7.4.5.Final
- ✅ Soporte OSS activo durante 2027

### Impacto Estimado

- **Riesgo:** 🟢 Bajo (migración menor dentro de la línea 4.x)
- **Complejidad:** 🟢 Baja
- **Cambios Breaking:** Mínimos — principalmente el flag `-DskipTests` en scripts
- **Tiempo de Implementación:** < 1 sprint

---

## 🔍 Análisis del BOM de Boot 4.1.1

Verificado desde `spring-boot-dependencies-4.1.1.pom` (Maven Central).

| Componente | Boot 4.0.8 gestionaba | Boot 4.1.1 gestiona | Acción |
|---|---|---|---|
| `tomcat.version` | 11.0.24 | **11.0.24** (sin cambio) | ⚠️ Mantener override en 11.0.25 |
| `jackson-bom.version` | 3.1.5 | **3.1.5** (sin cambio) | ⚠️ Mantener override en 3.1.6 |
| `spring-framework.version` | 7.0.9 | 7.0.9 | Sin cambio |
| `spring-security.version` | 7.0.7 | **7.1.1** | Verificar con tests |
| `hibernate.version` | 7.2.x | **7.4.5.Final** | Verificar con tests |
| `git-commit-id-maven-plugin` | gestionaba 9.x | **9.2.0** | Mantener override en 10.0.1 |
| `commons-lang3.version` | 3.20.0 | 3.20.0 | Sin cambio |

### Por qué se mantiene el override de Tomcat

Boot 4.1.1 sigue gestionando Tomcat 11.0.24, que tiene tres CVEs corregidos en 11.0.25:
- **CVE-2026-73180** (Low): WebSocket session no se cierra al expirar HTTP session
- **CVE-2026-68763** (Important): DoS via allocation leak en HTTP/2 backlog tracking
- **CVE-2026-68569** (Important): Principal lookup falla abierto en algunos métodos de autenticación

### Por qué se mantiene el override de Jackson

Boot 4.1.1 sigue gestionando Jackson 3.1.5, con los mismos CVEs que motivaron el override original:
- **CVE-2026-19032** (CVSS 6.9): Unsafe Reflection en `jackson-databind`
- **CVE-2026-83557** (CVSS 6.3): Deserialization of Untrusted Data

---

## ⚠️ Breaking Changes Relevantes (Boot 4.1.0)

### 1. `-DskipTests` ya no omite AOT processing

El plugin de Maven de Spring Boot 4.1 solo reacciona a `maven.test.skip` para omitir tests,
por consistencia con otros plugins core de Maven. El flag `-DskipTests` ya no tiene efecto
para saltar el procesamiento AOT de tests.

**Impacto en este proyecto:**
- `scripts/publish-images.sh` usa `./mvnw clean package -DskipTests` → cambiar a `-Dmaven.test.skip=true`
- `AGENTS.md` sección Release Flow documenta ese comando → actualizar

### 2. Spring Security 7.0 → 7.1 (incremental)

La configuración actual (`WebSecurityConfig` con `SecurityFilterChain`, `HttpSecurity`,
`AbstractHttpConfigurer`, `@EnableMethodSecurity`) es compatible con la API de Spring Security 7.x.
No se anticipan breaking changes, pero la suite de tests de integración lo confirmará.

### 3. Deprecaciones de Boot 4.0 eliminadas

Cualquier API marcada `@Deprecated` en Boot 4.0 habrá desaparecido. Los tests de compilación
y la suite completa lo detectarán.

---

## 📋 Plan de Tareas

### Task 1: Crear rama y actualizar el parent en `pom.xml`

- Crear rama `chore/spring-boot-4.1.1`
- Cambiar `<version>4.0.8</version>` → `<version>4.1.1</version>` en el bloque `<parent>`
- Mantener override `<tomcat.version>11.0.25</tomcat.version>` (actualizar comentario con CVEs nuevos)
- Mantener override `<jackson-bom.version>3.1.6</jackson-bom.version>`
- Mantener override `<git-commit-id-plugin.version>10.0.1</git-commit-id-plugin.version>`
- **Verificar:** `./mvnw clean compile` → `BUILD SUCCESS`

### Task 2: Corregir breaking change `-DskipTests` en scripts y docs

- `scripts/publish-images.sh`: reemplazar `-DskipTests` por `-Dmaven.test.skip=true`
- `AGENTS.md` sección Release Flow: actualizar el comando documentado
- **Verificar:** `./mvnw clean package -Dmaven.test.skip=true` genera `target/backend.jar`

### Task 3: Suite de tests unitarios

- Ejecutar `./mvnw test`
- Analizar y corregir cualquier regresión (probables: APIs de Security 7.0 eliminadas, cambios Hibernate 7.4)
- Ejecutar `./mvnw compile spotbugs:check`
- **Verificar:** `Tests run: 51, Failures: 0, Errors: 0` + SpotBugs limpio

### Task 4: Suite de tests de integración

- Ejecutar `./mvnw clean verify -Pintegration-tests`
- Analizar y corregir regresiones (`TestRestTemplate`, security filters, serialización)
- **Verificar:** `Tests run: 73, Failures: 0, Errors: 0`

### Task 5: Actualizar documentación

- `AGENTS.md`: Stack section → `Spring Boot 4.1.1`, `Spring Security 7.1.1`, `Hibernate 7.4.5.Final`
- `AGENTS.md`: Known Vulnerabilities → actualizar estado post-migración; eliminar pendiente de línea 4.0; añadir nuevos falsos positivos de Tomcat si aplica
- Crear `docs/migration/SPRING_BOOT_4.1_MIGRATION.md` con el registro de ejecución (tras completar las tasks anteriores)
- `README.md`: actualizar versión del stack si aparece referenciada
- Verificar: `grep -r "4.0.8" docs/ README.md AGENTS.md` no devuelve referencias incorrectas
- **Verificar:** pre-commit checklist `./mvnw clean compile && ./mvnw test && ./mvnw compile spotbugs:check`

### Task 6: OWASP dependency-check scan post-migración

- Ejecutar `./mvnw -Pdependency-check dependency-check:check -Dnvd.api.key=$NVD_API_KEY`
- Revisar reporte en `target/dependency-check-report.html`
- Documentar falsos positivos en `docs/security/` y `AGENTS.md`
- **Verificar:** 0 vulnerabilidades reales; `AGENTS.md` Known Vulnerabilities actualizado con fecha del scan

---

## 🧪 Verificación Final Esperada

| Comando | Resultado esperado |
|---|---|
| `./mvnw test` | ✅ 51/51 |
| `./mvnw clean verify -Pintegration-tests` | ✅ 51 unit + 22 IT |
| `./mvnw compile spotbugs:check` | ✅ limpio |
| `./mvnw -Pdependency-check dependency-check:check` | ✅ 0 vulnerabilidades reales |

---

## 🔗 Referencias

- [Spring Boot 4.1.0 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.1-Release-Notes)
- [BOM spring-boot-dependencies 4.1.1](https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-dependencies/4.1.1/spring-boot-dependencies-4.1.1.pom)
- [Apache Tomcat 11 Security](https://tomcat.apache.org/security-11.html)
- [Plan migración anterior (4.0)](../SPRING_BOOT_4_MIGRATION.md)
- [Informe vulnerabilidades 2026-09-08](../../security/informe-vulnerabilidades-2026-09-08.md)
