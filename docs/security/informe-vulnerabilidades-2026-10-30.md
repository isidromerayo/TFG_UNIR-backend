# Informe de Revisión de Vulnerabilidades — 30/10/2026

Triaje de los CVEs detectados por OWASP Dependency-Check el 30/10/2026 y su remediación,
junto con la actualización de springdoc a 3.2.0 y la retirada de overrides heredados de Boot 4.1.1.

## 1. Alcance y metodología

| Campo | Valor |
|-------|-------|
| Fecha | 30/10/2026 |
| Herramienta | OWASP dependency-check 13.0.0 (analizadores NVD + RetireJS) |
| Proyecto | `eu.estilolibre.tfgunir:backend` |
| Versión base | Spring Boot 4.1.2 (Java 21), springdoc 3.2.0 |
| Detección | Reproducción local con `NVD_API_KEY` + job `dependency-check` de CI |

El escaneo se ejecutó sobre la rama `chore/springdoc-3.2.0`, que sube springdoc de 3.1.1 a 3.2.0
(construido sobre Boot 4.1.2 y con el webjar `swagger-ui` 5.34.0 transitivo).

## 2. Vulnerabilidades reales detectadas

| CVE | GHSA relacionado | Descripción | Afecta | Severidad |
|-----|------------------|-------------|--------|-----------|
| CVE-2026-116802 | GHSA-c6gr-qvv4-5v3f | XSS: el guard de `SMIL attributeName=href` se omite con `PARSER_MEDIA_TYPE=application/xhtml+xml` | DOMPurify < 3.4.17 | Low |
| CVE-2026-116803 | GHSA-mrv2-3pqr-j5jr | XSS con `IN_PLACE`: un hook que elimina un padre/hermano deja el subárbol con handlers armados (fix incompleto de GHSA-p98j-92pf-mc4p) | DOMPurify < 3.4.17 | High |
| CVE-2026-116804 | GHSA-cg2g-xh64-v4q7 | Bypass de `SAFE_FOR_TEMPLATES` en shadow roots | DOMPurify < 3.4.17 | Critical |

**Versión en uso:** el webjar `org.webjars:swagger-ui` **5.34.0**, gestionado por springdoc 3.2.0,
empaqueta **DOMPurify 3.4.16** → afectada por los tres avisos. Comprobado extrayendo el bundle:

```
swagger-ui-5.34.0.jar → swagger-ui-bundle.js → DOMPurify 3.4.16   (vulnerable)
swagger-ui-5.34.1.jar → swagger-ui-bundle.js → DOMPurify 3.4.17   (corregida)
```

## 3. Remediación

### 3.1 springdoc-openapi 3.1.1 → 3.2.0

Motivos:

- 3.2.0 está construido sobre **Spring Boot 4.1.2**, versión ya presente en `main`.
- Elimina la necesidad de declarar `springdoc-openapi-starter-common` explícitamente: el starter
  la arrastra alineada a 3.2.0 (antes quedaba fijada a 3.1.1 → versiones mixtas).
- Su versión *baked-in* de swagger-ui pasa de 5.32.14 a 5.34.0.

### 3.2 Override de `swagger-ui` a 5.34.1

El único override que se mantiene, justificado por los CVEs anteriores:

```xml
<swagger-ui.version>5.34.1</swagger-ui.version>
...
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>swagger-ui</artifactId>
    <version>${swagger-ui.version}</version>
</dependency>
```

Y **`springdoc.swagger-ui.version=5.34.1`** en `src/main/resources/application.properties` y
`src/test/resources/application.properties`. Si pom y properties no coinciden, springdoc apunta el
`ResourceHttpRequestHandler` a un directorio inexistente y `/swagger-ui/*` devuelve 404; lo detecta
`OpenApiDocumentationIT`.

> Se retirará cuando springdoc publique una versión que gestione 5.34.1 o superior.

### 3.3 Retirada de overrides de Tomcat y Jackson

Boot 4.1.2 gestiona **Tomcat 11.0.27** y **Jackson 3.1.7**, ambos por encima de los overrides que
se mantenían desde 4.1.1 (`tomcat.version=11.0.26`, `jackson-bom.version=3.1.6`). Eran ya
*down-pins* y se han eliminado del `pom.xml`.

## 4. Resultado del re-scan tras la remediación

Ejecutado el 30/10/2026 con `./mvnw -Pdependency-check dependency-check:check -Dformat=ALL`
(81 artefactos analizados):

| Dependencia | Antes | Después | CVEs |
|-------------|-------|---------|------|
| `swagger-ui` (DOMPurify) | 5.34.0 (DOMPurify 3.4.16) | **5.34.1** (DOMPurify 3.4.17) | 0 |
| Tomcat embed | 11.0.26 (override) | **11.0.27** (BOM) | 0 |
| Jackson 3 | 3.1.6 (override) | **3.1.7** (BOM) | 0 |

Los hallazgos restantes son los 2 falsos positivos ya documentados
(`spring-boot-data-rest`, `spring-boot-devtools`). **Total: 0 vulnerabilidades reales.**

## 5. Verificación

| Comprobación | Resultado |
|--------------|-----------|
| `./mvnw clean verify -Pintegration-tests` | ✅ 51 unit tests + 22 IT |
| `OpenApiDocumentationIT` | ✅ 5/5 (`/swagger-ui.html` y `/swagger-ui/index.html`) |
| `./mvnw compile spotbugs:check` | ✅ `BugInstance size is 0` |
| dependency-check local | ✅ `swagger-ui-5.34.1` sin vulnerabilidades |
| `dependency:tree` | ✅ springdoc 3.2.0, common 3.2.0, swagger-ui 5.34.1, Tomcat 11.0.27, Jackson 3.1.7 |

## 6. Falsos positivos (confirmados sobre Boot 4.1.2)

| Hallazgo | Motivo del descarte |
|----------|---------------------|
| `spring-boot-data-rest` CVE-2026-47849, CVE-2026-47850 | El CPE matcher asocia el módulo `spring-boot-data-rest` 4.1.2 con "Spring Data REST 4.0.0–4.4.15". La librería real (`spring-data-rest-webmvc/core` **5.1.2**, gestionada por Boot 4.1.2) es la **versión corregida** (fix OSS en 5.1.1). |
| `spring-boot-devtools` CVE-2022-31691 | El CVE afecta a las extensiones de IDE (Spring Tools 4 / VSCode), no a devtools. Además es dev-only y se excluye del jar empaquetado. |

## 7. Cambios documentales

- `pom.xml`: springdoc 3.2.0, override `swagger-ui` 5.34.1; retirados `springdoc-openapi-starter-common`,
  `tomcat.version` y `jackson-bom.version`.
- `src/main/resources/application.properties` y `src/test/resources/application.properties`:
  `springdoc.swagger-ui.version=5.34.1`.
- `AGENTS.md`: stack (Boot 4.1.2, Maven 3.10.0, Framework 7.0.10, Security 7.1.2, Tomcat 11.0.27,
  Jackson 3.1.7, rest-assured 6.3.0, springdoc 3.2.0) + §Known Vulnerabilities + falsos positivos.
- `README.md`: stack y fecha de actualización.
- `docs/security/SECURITY.md`: versiones de framework y fecha.
- `docs/security/README.md`: índice con este informe.
- `docs/security/accepted-advisories.md`: fecha de la última revisión.
- `docs/migration/SPRING_BOOT_LIFECYCLE.md`: versión actual 4.1.2 y retirada de overrides.
- `docs/docker/DOCKER_IMAGES_GUIDE.md`: nota de que `main` apunta a 4.1.2.
