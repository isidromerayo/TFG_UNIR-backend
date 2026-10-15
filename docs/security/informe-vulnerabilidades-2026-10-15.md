# Informe de Revisión de Vulnerabilidades — 15/10/2026

Triaje de los CVEs detectados por OWASP Dependency-Check el 15/10/2026 y su remediación.

## 1. Alcance y metodología

| Campo | Valor |
|-------|-------|
| Fecha | 15/10/2026 |
| Herramienta | OWASP dependency-check 13.0.0 (analizadores NVD + RetireJS) |
| Proyecto | `eu.estilolibre.tfgunir:backend` |
| Versión base | Spring Boot 4.1.1 (Java 21), springdoc 3.1.1 |
| Detección | Job `dependency-check` de CI + reproducción local |

Los dos CVEs se detectaron a través del analizador **RetireJS** (librerías JavaScript
empaquetadas) al ejecutarse el escaneo en CI.

## 2. Vulnerabilidades reales detectadas

| CVE | GHSA relacionado | Descripción | Afecta | Severidad |
|-----|------------------|-------------|--------|-----------|
| CVE-2026-109344 | GHSA-p98j-92pf-mc4p | DOM XSS con `IN_PLACE` + hooks `afterSanitize*` (CWE-79) | DOMPurify ≥ 3.4.13 y < 3.4.16 | Low |
| CVE-2026-109345 | GHSA-6688-9rhm-gjv2 | XSS por raíz "rawtext" devuelta en modo `IN_PLACE` (CWE-79) | DOMPurify < 3.4.16 | Low |

**Versión en uso:** el webjar `org.webjars:swagger-ui` **5.32.14** (arrastrado por
springdoc-openapi 3.1.1) empaqueta **DOMPurify 3.4.13** → versión afectada por ambos CVEs.
Comprobado extrayendo el bundle:

```
swagger-ui-5.32.14.jar → swagger-ui-bundle.js → DOMPurify.version="3.4.13"   (vulnerable)
swagger-ui-5.33.2.jar  → swagger-ui-bundle.js → DOMPurify.version="3.4.16"   (corregida)
```

### Corrección de la evaluación previa

El informe del 02/10/2026 (§3) clasificaba `GHSA-p98j-92pf-mc4p` como **falso positivo**,
razonando que se requería `IN_PLACE` + hook `afterSanitize*` y que Swagger UI no usa ese
patrón. Esa evaluación queda **corregida**:

- El aviso tiene ya un CVE asignado (`CVE-2026-109344`), publicado el 12/10/2026.
- Aparece un segundo CVE de la misma familia (`CVE-2026-109345`).
- El webjar en uso está en el rango afectado, así que se remedia en lugar de descartarse.

Aunque la explotación depende de que la aplicación llame a `DOMPurify` con `IN_PLACE`
(nunca se procesa por el backend), actualizar el webjar es la opción correcta: elimina el
hallazgo y no tiene coste funcional.

## 3. Remediación

El arreglo requiere **dos cambios coordinados**; solo uno de ellos no es suficiente:

1. **`pom.xml`** — override del webjar para que existan los assets con DOMPurify 3.4.16:

   ```xml
   <swagger-ui.version>5.33.2</swagger-ui.version>
   ...
   <dependency>
       <groupId>org.webjars</groupId>
       <artifactId>swagger-ui</artifactId>
       <version>${swagger-ui.version}</version>
   </dependency>
   ```

2. **`springdoc.swagger-ui.version=5.33.2`** en `application.properties` (y en el de tests) —
   springdoc 3.1.1 trae la versión *baked-in* en su `springdoc.config.properties`
   (`springdoc.swagger-ui.version=5.32.14`) y configura el `ResourceHttpRequestHandler`
   apuntando a `classpath:/META-INF/resources/webjars/swagger-ui/<versión>/`.

> ⚠️ **Lección aprendida:** actualizar solo el webjar deja Swagger UI en **404** en todas sus
> rutas (`/swagger-ui.html`, `/swagger-ui/index.html`), porque springdoc sigue apuntando al
> directorio 5.32.14, que ya no existe. Lo detectó `OpenApiDocumentationIT`
> (`NoResourceFoundException: No static resource index.html`). Ambas cosas deben ir juntas.

## 4. Resultado del re-scan tras la remediación

Ejecutado el 15/10/2026 con `./mvnw org.owasp:dependency-check-maven:check -DfailOnError=false`:

| Dependencia | Antes | Después | CVEs |
|-------------|-------|---------|------|
| `swagger-ui` (DOMPurify) | 5.32.14 (DOMPurify 3.4.13) | **5.33.2** (DOMPurify 3.4.16) | 0 |

Los hallazgos restantes son los 2 falsos positivos ya documentados
(`spring-boot-data-rest`, `spring-boot-devtools`). **Total: 0 vulnerabilidades reales.**

## 5. Verificación

| Comprobación | Resultado |
|--------------|-----------|
| `./mvnw test` | ✅ 51 tests |
| `./mvnw -Pintegration-tests verify -Dit.test=OpenApiDocumentationIT` | ✅ 5 tests (Swagger UI servido) |
| `./mvnw compile spotbugs:check` | ✅ `BugInstance size is 0` |
| dependency-check local | ✅ sin hallazgos de DOMPurify |
| `dependency:tree -Dincludes=org.webjars:swagger-ui` | ✅ 5.33.2 |

## 6. Falsos positivos (confirmados)

| Hallazgo | Motivo del descarte |
|----------|---------------------|
| `spring-boot-data-rest` CVE-2026-47849, CVE-2026-47850 | El CPE matcher asocia el módulo `spring-boot-data-rest` 4.1.1 con "Spring Data REST 4.0.0–4.4.15". La librería real (`spring-data-rest-webmvc/core` **5.1.1**, gestionada por Boot 4.1.1) es la **versión corregida**. |
| `spring-boot-devtools` CVE-2022-31691 | El CVE afecta a las extensiones de IDE (Spring Tools 4 / VSCode), no a devtools. Además es dev-only y se excluye del jar empaquetado. |

## 7. Cambios documentales

- `pom.xml`: propiedad `swagger-ui.version` 5.33.2 y dependencia override del webjar.
- `src/main/resources/application.properties` y `src/test/resources/application.properties`:
  `springdoc.swagger-ui.version=5.33.2`.
- `AGENTS.md`: §Known Vulnerabilities actualizado; retirada la entrada de falso positivo de
  DOMPurify al quedar remediada.
- `docs/security/README.md`: índice actualizado con este informe.
- `docs/security/informe-vulnerabilidades-2026-10-15.md`: este informe.

## 8. Seguimiento posterior

- [ ] `dependency-check` sigue fallando en PRs de Dependabot si `NVD_API_KEY` no está en el
      almacén de secretos de Dependabot (ver `AGENTS.md` §Known Vulnerabilities).
- [ ] Revisar si springdoc publica una versión que ya gestione swagger-ui ≥ 5.33.2 (entonces
      podría retirarse el override del webjar y de la propiedad).
- [ ] Repetir el triaje ante cada nuevo aviso de DOMPurify/Swagger UI (no esperar al escáner NVD).
