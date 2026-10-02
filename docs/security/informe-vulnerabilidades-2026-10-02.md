# Informe de Revisión de Vulnerabilidades — 02/10/2026

Análisis de vulnerabilidades de las librerías del backend y plan de remediación.

## 1. Alcance y metodología

| Campo | Valor |
|-------|-------|
| Fecha | 02/10/2026 |
| Herramienta | OWASP dependency-check 13.0.0 (BD NVD completa, API key local) |
| Proyecto | `eu.estilolibre.tfgunir:backend` 0.7.2-SNAPSHOT |
| Versión base | Spring Boot 4.1.1 (Java 21) |
| Reporte técnico | `target/dependency-check-report.html` (regenerable, no versionado) |

### Fuentes consultadas

| Fuente | Estado en la revisión |
|--------|-----------------------|
| OWASP Dependency-Check (NVD local) | Ejecutado, 4 hallazgos (todos falsos positivos) |
| Apache Tomcat Security Advisories | 10 CVEs nuevos del 23/09/2026 afectan a 11.0.25 |
| Spring Security Advisories | CVE-2026-41707 corregido en 7.1.1 (versión en uso) |
| Spring HATEOAS Advisories | CVE-2026-41006/41007 corregidos en 3.1 GA (versión en uso) |
| Spring Data REST Advisories | CVE-2026-47849/47850 corregidos en 5.1.1 (versión en uso) |
| AGENTS.md (falsos positivos) | 2 entradas previas, 1 nueva |

## 2. Vulnerabilidades reales detectadas

El rendimiento del escáner NVD depende de que el CVE esté *enriquecido* con CPEs en NVD.
Los CVEs de Tomcat publicados el 23/09/2026 estaban (a fecha de este informe) **"Awaiting
Enrichment"**, por lo que dependency-check **no los detectó**. Se localizaron por triaje
manual contra los avisos de Apache y Spring.

### Tomcat 11.0.25 (override de Spring Boot 4.1.1)

| CVE | Descripción | Severidad |
|-----|-------------|-----------|
| CVE-2026-86248 | CLIENT_CERT auth no falla con soft-fail desactivado (FFM); fix incompleto de CVE-2026-34500 | Critical 9.1 |
| CVE-2026-76183 | Bypass de constraints de seguridad en endpoints WebSocket | Critical 9.8 |
| CVE-2026-86350 | Regresión del fix de CVE-2026-41293 → request header mix-up | Important |
| CVE-2026-78383 | AJP DoS (hilo de procesamiento bloqueado sin request body) | High 7.5 |
| CVE-2026-79677 | DoS por timeouts perdidos en escrituras WebSocket asíncronas | High 7.5 |
| CVE-2026-87022 | WebSocket message smuggling con per-message-deflate | High 7.5 |
| CVE-2026-75973 | Improper Authentication | High 7.3 |
| CVE-2026-78437 | HTTP/2 DoS por request malformado (cleanup incompleto) | Low 3.7 |
| CVE-2026-73581 | CRL ignorada cuando el certificado usa keystore (OpenSSL/FFM) | Medium 6.5 |
| CVE-2026-77756 | Improper handling of length parameter inconsistency | Low 3.7 |

**Remediación:** `tomcat.version` 11.0.25 → **11.0.26** (`pom.xml`). Fixes publicados por
Apache el 15/09/2026.

## 3. Falsos positivos (confirmados y documentados)

| Hallazgo | Motivo del descarte |
|----------|---------------------|
| `spring-boot-data-rest` CVE-2026-47849, CVE-2026-47850 | El CPE matcher asocia el módulo `spring-boot-data-rest` 4.1.1 con "Spring Data REST 4.0.0–4.4.15". La librería real (`spring-data-rest-webmvc/core` **5.1.1**, gestionada por Boot 4.1.1) es la **versión corregida** de ambos CVEs (fix OSS en 5.1.1). |
| `spring-boot-devtools` CVE-2022-31691 | El CVE afecta a las extensiones de IDE (Spring Tools 4 / VSCode), no a devtools. Además es dev-only y se excluye del jar empaquetado. |
| `swagger-ui` 5.32.14 → `swagger-ui-bundle.js`/`swagger-ui-es-bundle.js` (DOMPurify 3.4.13) GHSA-p98j-92pf-mc4p | Severidad **Low** (CVSS 2.3). Requiere `DOMPurify.sanitize(node, { IN_PLACE: true })` **más** un hook `afterSanitize*` que elimine nodos; Swagger UI no usa `IN_PLACE` ni ese patrón. Librería JS del navegador (documentación de API), nunca procesada por el backend. |

## 4. CVEs verificados como no aplicables (versión ya parcheada)

Estos avisos de agosto/septiembre de 2026 **no afectan** a las versiones en uso:

| Dependencia | Versión en uso | CVE | Estado |
|-------------|----------------|-----|--------|
| `spring-security-core` | 7.1.1 | CVE-2026-41707 (DPoP proof replay) | Corregido en 7.1.1 (afecta 7.1.0) |
| `spring-hateoas` | 3.1.2 | CVE-2026-41006, CVE-2026-41007 | Corregido en 3.1 GA / 3.0.4 (afecta ≤ 3.0.3) |
| `spring-data-rest-core/webmvc` | 5.1.1 | CVE-2026-47849, CVE-2026-47850 | Corregido en 5.1.1 (afecta 5.1.0) |
| `spring-core` / `spring-webmvc` | 7.0.9 | CVE-2026-47883/47884/47888/47890/47891 (sept.) | Afectan 7.0.0–7.0.8; 7.0.9 está fuera |
| `tools.jackson.core:jackson-databind` | 3.1.6 | CVE-2026-68497, CVE-2026-83557, CVE-2026-19032 | Corregidos en 3.1.6 |
| `org.apache.commons:commons-lang3` | 3.20.0 | — | Sin vulnerabilidades directas (Snyk) |
| `org.postgresql:postgresql` | 42.7.13 | — | Sin hallazgos |

## 5. Resultado del re-scan tras la remediación

Ejecutado el 02/10/2026 con la misma herramienta sobre Spring Boot 4.1.1 (Tomcat **11.0.26**):

| Dependencia | Antes | Después | CVEs reales |
|-------------|-------|---------|-------------|
| `tomcat-embed-core/el/websocket` | 11.0.25 (10 CVEs del 23/09) | **11.0.26** | 0 |

**Total: 0 vulnerabilidades reales.** Los 4 hallazgos del reporte son los falsos positivos
documentados en §3.

> **Limitación metodológica:** dependency-check solo detecta CVEs con CPEs publicados en NVD.
> Los avisos recientes pueden tardar semanas en aparecer. Por eso cada revisión requiere
> triaje manual contra los avisos de Apache/Spring (así se detectaron los CVEs de Tomcat).

## 6. Cambios documentales

- `pom.xml`: `tomcat.version` 11.0.25 → 11.0.26 y comentario actualizado.
- `AGENTS.md`: Stack actualizado a Tomcat 11.0.26; §Known Vulnerabilities y falsos positivos actualizados.
- `docs/security/SECURITY.md`: versiones y enlaces actualizados (Boot 4.1.1, dep-check 13.0.0, 0.7.x).
- `docs/security/README.md`: estado de `SNYK_SECURITY_ISSUE.md` (resuelto) e índice.
- `docs/security/informe-vulnerabilidades-2026-10-02.md`: este informe.

## 7. Seguimiento posterior

- [x] Re-escanear tras el bump de Tomcat (hecho: §5)
- [x] Triaje de los CVEs publicados desde el informe del 08/09/2026
- [ ] Bump a Spring Boot 4.1.2 cuando se publique (si gestiona Tomcat ≥ 11.0.26, retirar override)
- [ ] Revisar el gap de seguridad documentado: JWT emitido pero no validado (ver `docs/security/README.md`)
- [ ] Repetir triaje manual ante cada nuevo aviso de Apache Tomcat / Spring (no esperar al escáner NVD)
