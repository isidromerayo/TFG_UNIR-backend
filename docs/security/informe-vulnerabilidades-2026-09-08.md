# Informe de Revisión de Vulnerabilidades — 08/09/2026

Análisis de vulnerabilidades de las librerías del backend y plan de remediación.

## 1. Alcance y metodología

| Campo | Valor |
|-------|-------|
| Fecha | 08/09/2026 |
| Herramienta | OWASP dependency-check 13.0.0 (BD NVD completa, API key local) |
| Proyecto | `eu.estilolibre.tfgunir:backend` 0.6.3-SNAPSHOT |
| Versión base | Spring Boot 3.5.16 (Java 21) |
| Reporte técnico | `target/dependency-check-report.html` (regenerable, no versionado) |

### Fuentes consultadas

| Fuente | Estado en la revisión |
|--------|----------------------|
| Dependabot alerts (GitHub) | 0 alertas abiertas |
| Snyk / Mend (checks en PRs) | Pasa |
| OWASP Dependency-Check en CI | **Roto silenciosamente** (ver §5) |
| SonarQube | Pasa |
| AGENTS.md (falsos positivos) | 2 entradas vigentes |

## 2. Vulnerabilidades reales

Versiones en uso dentro del rango vulnerable publicado. Severidad según NVD.

| Dependencia | Versión | CVEs | Severidad máxima | Fix publicado |
|-------------|---------|------|------------------|---------------|
| `tomcat-embed-core` | 10.1.55 | 19 | 6 CRITICAL, 6 HIGH | Tomcat 10.1.58 |
| `spring-core` | 6.2.19 | 10 | 4 CRITICAL (bypass SpEL, corrupción de flujos SSE, bypass de cabeceras CORS pre-flight) | Spring Framework 6.2.20 |
| `spring-security-core` | 6.5.11 | 4 | 1 CRITICAL (bypass de autenticación) | 6.5.12 |
| `postgresql` | 42.7.11 | 1 | HIGH (downgrade de channel binding SCRAM) | 42.7.12+ |
| `jackson-databind` | 2.21.4 | 1 | MEDIUM | Nueva 2.21.x |
| `spring-data-jpa` | 3.5.13 | 1 | MEDIUM (CVE-2026-47834) | Solo en Spring Data JPA 4.1.0 |
| `log4j-api` | 2.24.3 | 1 nuevo (CVE-2026-49844) | MEDIUM (rango vulnerable < 2.25.5) | 2.25.5 — pendiente de triaje (ver §3) |

### CVEs críticos más relevantes

- **Tomcat** (`tomcat-embed-core` 10.1.55): CVE-2026-65637, CVE-2026-65905, CVE-2026-65182, CVE-2026-68525, CVE-2026-59083, CVE-2026-59084. Rangos vulnerables con corte en 10.1.55–10.1.57; fix en 10.1.56/57/58.
- **Spring Framework** (`spring-core` 6.2.19): CVE-2026-47890/47891/47892 (CRITICAL, fix 6.2.20) y CVE-2026-59280–59283 (SpEL, path traversal, DoS, XSS).
- **Spring Security** (`spring-security-core` 6.5.11): CVE-2026-59270 (CRITICAL, fix 6.5.12) y CVE-2026-47841 (HIGH, bypass de verificación de usuario).

## 3. Falsos positivos (confirmados y documentados)

| Hallazgo | Motivo del descarte |
|----------|---------------------|
| `log4j-api` CVE-2026-34477, CVE-2026-34479 | Requieren `log4j-core`, no presente en el proyecto (solo `log4j-api` + `log4j-to-slf4j`) |
| `swagger-ui` 5.32.2 (22 CVEs DOMPurify) | Dev-only, ejecución en navegador; el backend nunca procesa HTML con DOMPurify |

> **Nota:** CVE-2026-49844 sobre `log4j-api` es **nuevo** y no estaba en la lista de falsos positivos. Queda pendiente de triaje; se resolverá de todas formas con el bump de versión incluido en la migración (≥ 2.25.5).

## 4. Conclusiones

1. **Spring Boot 3.5.16 está EOL OSS desde el 30/06/2026.** Las versiones de corrección (Tomcat 10.1.58, Spring Framework 6.2.20, Spring Security 6.5.12) solo se publicarán en las líneas Spring Boot 4.x (4.0.8 hasta el 31/12/2026; 4.1.x hasta el 31/07/2027). No habrá 3.5.17.
2. La nota de AGENTS.md *"No Tomcat CVEs — tomcat-embed-core-10.1.55 (todos los CVEs anteriores corregidos)"* quedó obsoleta: los 19 CVEs detectados son posteriores a su última actualización (26/07/2026).
3. **La remediación de las ~37 CVEs reales pasa por migrar a Spring Boot 4.x.** El PR #141 (Dependabot, bump 3.5.16 → 4.1.1) cerrado como inválido apuntaba en la dirección correcta; solo fallaba en el detalle de `rest-assured` (Spring Boot 4 dejó de gestionar su versión) y en la falta de adaptación del resto del proyecto.
4. Estrategia elegida: **migración en dos pasos** — primero a la 4.0.8 (salto menor, OSS hasta el 31/12/2026) y el bump a 4.1.x como tarea posterior antes de que la línea 4.0 alcance su fin de soporte.

## 5. Estado del escaneo en CI

El workflow `owasp-dependency-check-maven.yml` presenta dos defectos que anulan su utilidad:

1. `NVD_API_KEY` no está configurada en los secrets del repositorio → la BD NVD no se descarga (`NvdApiException: Invalid API Key, length of 0`).
2. `continue-on-error: true` en el paso de escaneo → el job nunca falla, GitHub lo marca como success y no se genera el reporte (el artefacto queda vacío).

En la práctica, el escaneo CI solo cubre CISA KEV con caché web parcial. Dependabot y Snyk cubren parcialmente el hueco, pero el workflow da una falsa sensación de seguridad.

**Remediación (rama `chore/owasp-ci-nvd`):** eliminar `continue-on-error`. **Acción del mantenedor:** configurar el secret `NVD_API_KEY` en Settings → Secrets → Actions.

## 6. Plan de remediación ejecutado

| Paso | Rama | Contenido |
|------|------|-----------|
| 1 | `security/migrate-spring-boot-4` | Informe (este documento), migración a Spring Boot 4.0.8, fix `rest-assured` (BOM propio), adaptación de código/config, actualización de AGENTS.md y README |
| 2 | `chore/owasp-ci-nvd` | Workflow OWASP bloqueante |

## 7. Resultado del re-scan tras la migración

Ejecutado el 08/09/2026 con la misma herramienta sobre Spring Boot 4.0.8 (Tomcat fijado a 11.0.25):

| Dependencia | Antes | Después | CVEs después |
|-------------|-------|---------|--------------|
| `tomcat-embed-core` | 10.1.55 (19 CVEs) | **11.0.25** | 0 |
| `spring-core` | 6.2.19 (10 CVEs) | **7.0.9** | 0 |
| `spring-security-core` | 6.5.11 (4 CVEs) | **7.0.7** | 0 |
| `spring-data-jpa` | 3.5.13 (1 CVE) | **4.0.7** | 0 |
| `postgresql` | 42.7.11 (1 CVE) | **42.7.13** | 0 |
| `log4j-api` | 2.24.3 (3 CVEs) | **2.25.5** | 0 |
| `jackson-databind` | 2.21.4 (1 CVE) | **2.21.5** (+ Jackson 3 `tools.jackson` 3.1.5) | 0 |
| `swagger-ui` | 5.32.2 | **5.32.14** | FP (ya documentado) |

**Total: de ~70 hallazgos (9 dependencias) a 3 hallazgos (2 dependencias), todos falsos positivos:**

| Hallazgo post-migración | Motivo del descarte |
|--------------------------|---------------------|
| `spring-boot-data-rest` CVE-2026-47849, CVE-2026-47850 | El CPE matcher asocia el módulo `spring-boot-data-rest` 4.0.8 con los rangos de "Spring Data REST 4.0.0–4.4.15". La librería real (`spring-data-rest-webmvc/core` **5.0.7**, gestionada por Boot 4.0.8) está fuera de los rangos vulnerables (5.0.0–5.0.6), ya parcheada. |
| `spring-boot-devtools` CVE-2022-31691 | El CVE afecta a las extensiones de IDE (Spring Tools 4 para Eclipse/VSCode), no a `spring-boot-devtools`. Además devtools es dev-only y se excluye del jar empaquetado. |

## 8. Seguimiento posterior

- [x] Re-escanear tras la migración para confirmar la remediación (hecho: ver §7)
- [x] Triaje de CVE-2026-49844 sobre `log4j-api` (resuelto: la migración lleva 2.25.5, versión corregida)
- [x] Supresión/documentación de los 3 falsos positivos post-migración
- [ ] Configurar secret `NVD_API_KEY` en GitHub (mantenedor)
- [ ] Tras merge: verificar que el workflow OWASP ejecuta el escaneo NVD completo y genera el reporte
- [ ] **Bump a Spring Boot 4.1.x antes del 31/12/2026** (fin de soporte OSS de la línea 4.0)
