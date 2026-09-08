# Migración a Spring Boot 4.0.8

**Fecha:** 2026-09-08
**Versión Origen:** Spring Boot 3.5.16
**Versión Destino:** Spring Boot 4.0.8
**Estado:** ✅ Completada
**Rama:** `security/migrate-spring-boot-4`
**Motivación:** EOL OSS de la línea 3.5 (30/06/2026) + ~37 CVEs reales en librerías
(ver [informe de vulnerabilidades](../security/informe-vulnerabilidades-2026-09-08.md))

---

## 📊 Resumen Ejecutivo

Este documento registra el proceso de migración de Spring Boot 3.5.16 a 4.0.8.
Los cambios se dividen en dos grupos: los **contemplados en el plan inicial** y
los **descubiertos durante la ejecución** (problema → causa raíz → solución).

Resultado final: **51 tests unitarios + 22 tests de integración en verde**,
SpotBugs limpio y re-scan NVD con **0 vulnerabilidades reales** (3 falsos
positivos documentados).

---

## ✅ Cambios contemplados en el plan inicial

### 1. Actualización del Parent

```xml
<!-- ANTES -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.16</version>
</parent>

<!-- DESPUÉS -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.8</version>
</parent>
```

### 2. Fix de rest-assured (Boot 4 deja de gestionarlo)

Spring Boot 4 eliminó `rest-assured` de `spring-boot-dependencies`. Sin versión
explícita, el build ni siquiera resuelve el POM (`'dependencies.dependency.version'
for io.rest-assured:rest-assured:jar is missing`). Se usó el flujo TDD: el fallo
de resolución fue el "rojo" inicial y el fix el "verde".

```xml
<!-- AÑADIDO: importar BOM propio en dependencyManagement -->
<dependencyManagement>
    <dependencies>
        <!-- Spring Boot 4 ya no gestiona rest-assured; importamos su BOM -->
        <dependency>
            <groupId>io.rest-assured</groupId>
            <artifactId>rest-assured-bom</artifactId>
            <version>6.0.1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

La dependencia de test de `rest-assured` se mantiene sin versión explícita.

### 3. Informe de vulnerabilidades

`docs/security/informe-vulnerabilidades-2026-09-08.md`: análisis previo (~70
hallazgos en 9 dependencias), triaje de reales vs falsos positivos, decisiones
de remediación y re-scan post-migración.

### 4. Actualización de documentación

- **AGENTS.md**: Stack (4.0.8, Jackson 3, test slices modulares), Known
  Vulnerabilities con re-scan y nuevos falsos positivos.
- **README.md**: stack tecnológico, versiones y referencias a la línea 3.5.
- **docs/docker/DOCKER_IMAGES_GUIDE.md**: nota de la próxima release 0.7.0.

### 5. Workflow OWASP bloqueante

Eliminado `continue-on-error: true` de `.github/workflows/owasp-dependency-check-maven.yml`
→ en la **rama separada** `chore/owasp-ci-nvd` (cambio independiente de la migración).

---

## 🔧 Cambios descubiertos durante la ejecución

### 1. springdoc-openapi incompatible: 2.8.17 → 3.1.1

- **Problema:** el contexto no cargaba — `ClassNotFoundException:
  org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties`
  al procesar `SwaggerConfig`.
- **Causa:** Boot 4 modularizó los autoconfigure; `WebMvcProperties` se movió de
  paquete. springdoc 2.x referencia el paquete antiguo.
- **Solución:** springdoc-openapi **3.1.1** (línea compatible con Boot 4).

```xml
<!-- ANTES -->
<springdoc-openapi-starter-webmvc-ui.version>2.8.17</springdoc-openapi-starter-webmvc-ui.version>
<!-- DESPUÉS -->
<springdoc-openapi-starter-webmvc-ui.version>3.1.1</springdoc-openapi-starter-webmvc-ui.version>
```

### 2. Test slices modulares: `@DataJpaTest` y `@AutoConfigureTestDatabase`

- **Problema:** compilación de tests rota — los paquetes
  `org.springframework.boot.test.autoconfigure.orm.jpa/jdbc` ya no existen.
- **Causa:** Boot 4 repartió el test-autoconfigure en módulos por tecnología.
- **Solución:** nuevos artefactos test-scope + nuevos paquetes de anotaciones:

| Anotación | Paquete Boot 4 | Artefacto nuevo |
|-----------|----------------|-----------------|
| `@DataJpaTest` | `org.springframework.boot.data.jpa.test.autoconfigure` | `spring-boot-data-jpa-test` |
| `@AutoConfigureTestDatabase` | `org.springframework.boot.jdbc.test.autoconfigure` | `spring-boot-jdbc-test` |

### 3. `TestRestTemplate` modularizado

- **Problema 1:** compilación rota — `org.springframework.boot.test.web.client`
  ya no existe.
- **Solución 1:** la clase vive en `org.springframework.boot.resttestclient`
  (artefacto `spring-boot-resttestclient`, test-scope).
- **Problema 2:** el bean no se auto-configuraba aunque la clase existiera
  (`NoSuchBeanDefinitionException: TestRestTemplate`).
- **Causa:** en Boot 3, `@SpringBootTest(RANDOM_PORT)` proveía el bean; en Boot 4
  la auto-configuración se activa con anotación explícita.
- **Solución 2:** anotar el IT con `@AutoConfigureTestRestTemplate`
  (`org.springframework.boot.resttestclient.autoconfigure`).
- **Problema 3:** con la anotación, el contexto fallaba con
  `NoClassDefFoundError: org/springframework/boot/restclient/RestTemplateBuilder`.
- **Causa:** `RestTemplateBuilder` se movió al módulo `spring-boot-restclient`,
  que `spring-boot-resttestclient` no arrastra.
- **Solución 3:** añadir `spring-boot-restclient` (test-scope).

### 4. rest-assured 5.5.x roto con Groovy 5 → 6.0.1

- **Problema:** todos los ITs con rest-assured fallaban con `NullPointerException`
  en `ClosureMetaClass.invokeOnDelegationObject` (internos de Groovy).
- **Causa:** Boot 4 gestiona **Groovy 5** (`org.apache.groovy:groovy` 5.0.8);
  rest-assured 5.5.7 está compilado contra **Groovy 4.0.22**. Incompatibilidad
  binaria en runtime.
- **Solución:** BOM de rest-assured **6.0.1**, que declara `groovy.version 5.0.3`
  (rango `[5.0,6.0)`), compatible con el Groovy gestionado por Boot 4.

### 5. Jackson 2 → Jackson 3 (`tools.jackson`)

- **Problema:** `NoSuchBeanDefinitionException: com.fasterxml.jackson.databind.ObjectMapper`.
- **Causa:** Boot 4 auto-configura **Jackson 3** (`tools.jackson.databind.ObjectMapper`);
  el bean de Jackson 2 ya no existe. Jackson 2 queda solo como dependencia
  transitiva (jjwt).
- **Solución:**
  - `JacksonSerializationTest`: inyectar `tools.jackson.databind.ObjectMapper`.
  - Jackson 3 elimina las checked exceptions: suprimidos los
    `throws JsonProcessingException` de los métodos de test.
  - Las anotaciones de los modelos (`com.fasterxml.jackson.annotation.*`)
    **siguen siendo compatibles** — sin cambios en `model/`.
  - Eliminada la propiedad `jackson.version` (2.21.1) del pom: obsoleta en
    Boot 4 (gestionado ahora por el BOM de Jackson 3).

### 6. Constructor de `NoResourceFoundException` (Spring Framework 7)

- **Problema:** compilación de `GlobalExceptionHandlerTest` rota.
- **Causa:** el constructor cambió de `(HttpMethod, String)` a
  `(HttpMethod, String, String)` (tercer parámetro: mensaje).
- **Solución:** adaptado el test:

```java
// ANTES
new NoResourceFoundException(HttpMethod.GET, "api/usuarios");
// DESPUÉS
new NoResourceFoundException(HttpMethod.GET, "api/usuarios", "api/usuarios");
```

### 7. Override de Tomcat 11.0.24 → 11.0.25

- **Problema:** el re-scan NVD posterior a la migración seguía mostrando ~11
  CVEs de Tomcat (algunos CRITICAL) contra `tomcat-embed-core 11.0.24`.
- **Causa:** Boot 4.0.8 (20/08/2026) gestiona Tomcat 11.0.24; los fixes de los
  CVE-2026-65637 y familia llegaron con Tomcat 11.0.25.
- **Solución:** override soportado por Boot mediante propiedad:

```xml
<!-- Boot 4.0.8 gestiona Tomcat 11.0.24, vulnerable a CVE-2026-65637 et al.; fix en 11.0.25 -->
<tomcat.version>11.0.25</tomcat.version>
```

### 8. Re-scan post-migración: falsos positivos restantes

De ~70 hallazgos (9 dependencias) a **3 hallazgos, todos falsos positivos**
(documentados en el informe §7 y en AGENTS.md):

| Hallazgo | Motivo del descarte |
|----------|---------------------|
| CVE-2026-47849/47850 en `spring-boot-data-rest-4.0.8.jar` | El CPE matcher matchea el módulo de Boot (4.0.8) contra los rangos de "Spring Data REST 4.0.0–4.4.15". La librería real (`spring-data-rest-webmvc/core` 5.0.7) está fuera de los rangos vulnerables y parcheada. |
| CVE-2022-31691 en `spring-boot-devtools-4.0.8.jar` | El CVE afecta a las extensiones de IDE (Spring Tools 4 / VSCode), no a devtools. Además es dev-only y se excluye del jar empaquetado. |

### 9. Override de Jackson 3: 3.1.5 → 3.1.6 (detectado por Snyk)

- **Problema:** el check Snyk de la PR marcó fallo con 2 CVEs medias en
  `tools.jackson.core:jackson-databind` **3.1.5** (gestionado por Boot 4.0.8):
  CVE-2026-19032 (Unsafe Reflection, CVSS 6.9) y CVE-2026-83557
  (Deserialization of Untrusted Data, CVSS 6.3), introducidas vía
  `springdoc-openapi-starter-common`.
- **Causa:** el BOM de Boot 4.0.8 fija `jackson-bom.version` en 3.1.5; el fix
  (3.1.6) es posterior a la release.
- **Solución:** override de la propiedad (patrón igual que Tomcat):

```xml
<!-- Boot 4.0.8 gestiona Jackson 3.1.5, vulnerable a CVE-2026-19032/CVE-2026-83557 (Snyk); fix en 3.1.6 -->
<jackson-bom.version>3.1.6</jackson-bom.version>
```

---

## 🧪 Verificación

| Comando | Resultado |
|---------|-----------|
| `./mvnw test` | ✅ 51/51 |
| `./mvnw clean verify -Pintegration-tests` | ✅ 51 unit + 22 IT |
| `./mvnw compile spotbugs:check` | ✅ limpio (SpotBugs 4.10.4 + FindSecBugs) |
| `./mvnw -Pdependency-check dependency-check:check -Dnvd.api.key=$NVD_API_KEY` | ✅ 0 reales (3 FPs) |

Versiones finales del classpath: Tomcat **11.0.25**, Spring Framework **7.0.9**,
Spring Security **7.0.7**, Spring Data JPA **4.0.7**, Hibernate **7.2.24.Final**,
PostgreSQL JDBC **42.7.13**, log4j-api **2.25.5**, springdoc **3.1.1**,
rest-assured **6.0.1**, Jackson 3 (**3.1.6**) + transitivo Jackson 2 (**2.21.5**).

---

## 📅 Seguimiento pendiente

- [ ] Configurar el secret `NVD_API_KEY` en GitHub (mantenedor)
- [ ] Tras el merge: verificar que el workflow OWASP ejecuta el escaneo NVD completo
- [ ] **Bump a Spring Boot 4.1.x antes del 31/12/2026** (fin de soporte OSS de la línea 4.0)
