# AGENTS.md - TFG UNIR Backend

## Stack
- Java 21 + Spring Boot 4.1.2 + Maven 3.10.0 (`./mvnw`) — 4.0.8 → 4.1.1 el 2026-09-13; 4.1.1 → 4.1.2 el 2026-10-30
- Spring Framework 7.0.10, Spring Security 7.1.2, Tomcat 11.0.27 y Jackson 3.1.7: gestionados por Boot 4.1.2, **sin overrides** (los de Tomcat 11.0.26 y Jackson 3.1.6 se retiraron al quedar por debajo del BOM)
- Jackson 3 (`tools.jackson.*`) **3.1.7** (gestionado por Boot 4.1.2); Jackson 2 solo transitivo (jjwt)
- Test slices modulares: `spring-boot-data-jpa-test`, `spring-boot-jdbc-test`, `spring-boot-resttestclient` + `spring-boot-restclient`
- rest-assured 6.3.0 vía `rest-assured-bom` (Boot 4 ya no lo gestiona), springdoc-openapi 3.2.0 + swagger-ui 5.34.1 (override: DOMPurify 3.4.17)
- H2 (tests), PostgreSQL (prod)
- JWT auth, CSRF disabled, stateless API
- Logging: `java.util.logging.Logger` (not SLF4J)
- Security plugins: SpotBugs 4.10.5 + FindSecBugs 1.14.0 + sb-contrib 7.7.4

## Package Layout
`src/main/java/eu/estilolibre/tfgunir/backend/`
- `controller/` REST, `service/` business logic, `repository/` JPA
- `model/` entities, `dto/` records for responses, Lombok for requests
- `security/` JWT filters, `config/`, `exception/` global handler

## Essential Commands
```bash
# Compile + unit tests + SpotBugs (run before commit)
./mvnw clean compile && ./mvnw test && ./mvnw compile spotbugs:check

# Single test
./mvnw test -Dtest=UsuarioServiceTest
./mvnw -Pintegration-tests verify -Dit.test=LoginControllerIT

# Full verification with coverage
./mvnw clean verify -Pintegration-tests

# Vulnerability scan (requires NVD_API_KEY; el pom la lee de ${env.NVD_API_KEY})
NVD_API_KEY=$NVD_API_KEY ./mvnw -Pdependency-check dependency-check:check
```

## Non-Negotiable Rules
1. **TDD mandatory** - failing test before production code
2. **Always create a branch** - never work directly on `main`:
   - `feature/` — new functionality
   - `fix/` — bug fixes
   - `fix/remove-` — removing legacy code/dependencies
   - `security/` — vulnerability fixes (third-party or project)
   - `docs/` — documentation-only changes
   - `refactor/` — code restructuring
   - `chore/` — maintenance (deps, CI, config)
3. **SpotBugs must pass** before commit
4. **Never log secrets** (passwords, tokens)
5. **Validate inputs** with Jakarta annotations (`@Valid`, `@NotBlank`, `@Email`)
6. **Update docs before PR** - review and update affected documentation (see Documentation Workflow)

## Code Conventions
- Constructor injection with `final` fields
- `record` for immutable responses, `@Data` (Lombok) for mutable DTOs/entities
- Test naming: `ClassNameTest` (unit), `ClassNameIT` (integration)
- JaCoCo excludes: `model/`, `dto/`, `config/` (pom.xml exclusions - do not add new ones)

## Release Flow (Docker images)

### Automatizado (recomendado)

Workflow **`.github/workflows/release.yml`** (`workflow_dispatch`):

1. Actions → *Release* → *Run workflow* con `release_version` (p. ej. `0.8.0`) y
   `next_version` (p. ej. `0.8.1`). `dry_run` prepara el release sin push, sin Docker y sin GitHub Release.
2. Hace `mvn release:prepare`, sube `main` + tag `vX.Y.Z`, construye el jar desde el tag, publica
   `spring-backend-tfg:X.Y.Z` + `latest` en Docker Hub y crea la GitHub Release con notas generadas.

**Requisito**: secret `RELEASE_TOKEN` (PAT con *Contents: Read and write*). `main` tiene branch
protection y el `GITHUB_TOKEN` del workflow no puede hacer push directo; un PAT de admin sí
(`enforce_admins=false`). Se usan además `DOCKER_HUB_USERNAME` / `DOCKER_HUB_TOKEN`.

### Manual (fallback)

```bash
# 1. Preparar release (elimina -SNAPSHOT, crea tag vX.Y.Z, sube a next SNAPSHOT)
mvn release:prepare

# 2. Compilar desde el tag
git checkout vX.Y.Z && ./mvnw clean package -Dmaven.test.skip=true

# 3. Publicar backend (valida que NO sea SNAPSHOT)
./scripts/publish-images.sh

# 4. Verificar lo que se publicaría sin ejecutar
./scripts/publish-images.sh --dry-run

# 5. (Opcional) Publicar BD si hay cambios estructurales
POSTGRES_PASSWORD=<password> ./scripts/publish-db-image.sh 1.1

# 6. Volver a main y subir tags
git checkout main && git push origin main --tags
```

## Documentation Workflow

When making changes, update the affected docs **before commit**:

| Change type | Files to update |
|-------------|-----------------|
| New/modified REST endpoint | `README.md` (API section), Swagger annotations |
| New script in `scripts/` | `scripts/README.md` |
| Docker/Dockerfile change | `docs/docker/DOCKER_IMAGES_GUIDE.md`, `docker-compose.yml` |
| Security change | `docs/security/` relevant file, `AGENTS.md` Known Vulnerabilities |
| Dependency upgrade | `pom.xml` versions, `AGENTS.md` Stack section, `README.md` (stack, comandos), `docs/docker/DOCKER_IMAGES_GUIDE.md` si afecta a imágenes |
| New feature/bugfix | `README.md` if user-facing |
| Removing legacy code | Update all references to removed files/commands |

**Pre-PR checklist:**
- [ ] All affected documentation updated
- [ ] `grep -r "removed-feature" docs/ scripts/ README.md` returns no stale references
- [ ] No broken links to deleted files
- [ ] `AGENTS.md` Stack section updated if dependencies changed
- [ ] Stale-reference scan: `grep -rn "skipUTs\|maria_db\|app_db\|Spring Boot 3" README.md docs/ STRUCTURE.md` — sin resultados fuera de docs marcados como legacy/históricos

### Documentation Audit (migraciones de Spring Boot)

Tras cada migración de versión mayor/menor de Spring Boot, ejecutar una **revisión de
consistencia global** (no basta con actualizar solo lo que el cambio toca directamente):

1. `grep -rn "Spring Boot 3\|Boot 4\.0\|skipUTs\|maria_db\|app_db\|3306" README.md docs/ STRUCTURE.md AGENTS.md` — las referencias a versiones anteriores solo pueden quedar en docs históricos (`docs/migration/`, changelogs) o guías marcadas explícitamente como legacy
2. Verificar que los comandos documentados existen en el `pom.xml` (ej: flags como `-DskipUTs` pueden no existir; failsafe/surefire también honran `skipTests`)
3. Verificar que las salidas de ejemplo (nombres de contenedores, puertos, tablas de tags de imágenes) coinciden con `docker-compose.yml` y `scripts/`
4. Verificar que la tabla de tags de `docs/docker/DOCKER_IMAGES_GUIDE.md` refleja la versión de Boot actual

Lección aprendida (2026-09-13): las reglas per-tipo-de-cambio no detectan contenido stale
anterior a la regla; por eso cada migración requiere este audit completo.

## Tooling
- JUnit 5 + Mockito + AssertJ
- `@DataJpaTest`, `@WebMvcTest`, `@SpringBootTest` + Testcontainers
- SpotBugs config: `src/main/resources/spotbugs-exclude.xml`
- SonarQube: https://sonarcloud.io/project/overview?id=isidromerayo_TFG_UNIR-backend

## Known Vulnerabilities
Estado tras revisión de vulnerabilidades del 02/10/2026: **0 vulnerabilidades reales** en el classpath.
La revisión detectó 10 CVEs nuevos de Tomcat 11.0.25 (23/09/2026) que el escáner NVD no veía
(CPEs pendientes de enriquecimiento); remediados con el override a Tomcat **11.0.26**.
Informe: `docs/security/informe-vulnerabilidades-2026-10-02.md`.
Revisión del 15/10/2026: el escaneo detectó **CVE-2026-109344** y **CVE-2026-109345** (XSS en
**DOMPurify < 3.4.16**) en el webjar `swagger-ui` 5.32.14 que arrastra springdoc 3.1.1, cuyo bundle
empaqueta DOMPurify 3.4.13. Remedado con el override a `swagger-ui` **5.33.2**, que ya trae
DOMPurify 3.4.16. `CVE-2026-109344` es el mismo aviso que antes se documentaba como falso positivo
con el identificador `GHSA-p98j-92pf-mc4p`.
Revisión del 30/10/2026: el escaneo detectó **CVE-2026-116802** (LOW), **CVE-2026-116803** (HIGH) y
**CVE-2026-116804** (CRITICAL) en **DOMPurify 3.4.16**, que empaqueta el webjar `swagger-ui` 5.34.0
gestionado por springdoc 3.2.0. Remedado con el override a `swagger-ui` **5.34.1**, que trae
DOMPurify 3.4.17. En la misma revisión se retiraron los overrides de Tomcat 11.0.26 y Jackson 3.1.6:
Boot 4.1.2 ya gestiona **11.0.27** y **3.1.7**. Informe:
`docs/security/informe-vulnerabilidades-2026-10-30.md`.
OWASP scan ejecutado con `NVD_API_KEY` (secret configurado en GitHub, verificado en CI — PR #147).
Run OWASP scan periodically: `NVD_API_KEY=$NVD_API_KEY ./mvnw -Pdependency-check dependency-check:check`
(El workflow `owasp-dependency-check-maven.yml` ahora corre el escaneo NVD completo solo en schedule
semanal + dispatch; el escaneo rápido en PRs lo hacen `security-osv.yml` y `security-trivy.yml`.)
> **Requiere `NVD_API_KEY` en AMBOS almacenes de secretos**: el de Actions y el de **Dependabot**
> (Settings → Secrets and variables → Dependabot). Los PRs de Dependabot leen solo de su propio
> almacén, así que si la clave falta ahí el scan se omite (skipped) en lugar de fallar el build.
> **Resiliencia ante caídas de NVD**: el job se ejecuta con `-DfailOnError=false`, así que un `503`
> de NIST al actualizar los datos NVD no rompe el build; se genera y sube el informe con los datos
> cacheados y el log avisa (`Unable to update ... using local data instead`). En ese caso el informe
> puede estar incompleto y requiere revisión manual.
> **Limitación:** dependency-check solo detecta CVEs con CPEs en NVD; los avisos recientes tardan
> semanas en aparecer. Cada revisión requiere triaje manual contra los avisos de Apache/Spring.

### Security workflows y comandos

Un workflow por herramienta, para que fallen y se programen de forma independiente:

- **`.github/workflows/security-osv.yml`** ("OSV Scanner"): OSV Scanner sobre `pom.xml` (transitivas vía
  deps.dev) en `push`/`pull_request`/`schedule` diario/`workflow_dispatch`. **Gate duro** en PRs +
  accepted-advisories recheck.
- **`.github/workflows/security-trivy.yml`** ("Trivy Scan"): Trivy filesystem scan (CRITICAL/HIGH,
  `ignore-unfixed`) con los mismos triggers. **Gate duro** en PRs + accepted-advisories recheck.
- **`.github/workflows/owasp-dependency-check-maven.yml`**: escaneo profundo con OWASP Dependency-Check
  (NVD). Solo `schedule` semanal (`0 3 * * 0`) + `workflow_dispatch`, para no bloquear PRs ni tardar
  en cada push.
- Local: `./scripts/security-check.sh` (OSV Scanner + Trivy si están instalados + accepted recheck);
  `./scripts/check-accepted-advisories.sh` (revisión de advisories aceptados).

### Dependency-Check False Positives (Boot 4.1.2 — escaneo 2026-10-30)
These entries are flagged by the CPE matcher but do **not** affect the project:
- **CVE-2026-47849, CVE-2026-47850** on `spring-boot-data-rest-4.1.2.jar` — el CPE matcher matchea el módulo de Boot (4.1.2) contra rangos de "Spring Data REST". La librería real (`spring-data-rest-webmvc/core` **5.1.2**, gestionada por Boot 4.1.2) es la versión corregida (fix OSS en 5.1.1).
- **CVE-2022-31691** on `spring-boot-devtools-4.1.2.jar` — el CVE afecta a las extensiones de IDE (Spring Tools 4 / VSCode), no a devtools. Además es dev-only y se excluye del jar empaquetado.

### Advisories aceptados (sin parche upstream)
Regla: un CVE sin versión parcheada publicada sólo puede "aceptarse" con una entrada en
`docs/security/accepted-advisories.md` (motivo, alcance y ruta de dependencia). Se revisa en cada
iteración con `./scripts/check-accepted-advisories.sh` (paso *Accepted advisories recheck* de
`owasp-dependency-check-maven.yml`): consulta OSV y **falla** en cuanto aparezca una versión
parcheada para el paquete Maven afectado; entonces se sube la dependencia (o el override de Boot) y
se borra la entrada. No confundir con los falsos positivos de CPE de arriba: ahí no hay nada que
parchear.
Estado actual: **0 advisories aceptados**.

## Skills
Repositorio: `springboot-tdd`, `springboot-security`, `springboot-patterns`, `java-spring-development`, `xp-tdd-practices`, `testing-standards`, `action-tdd`, `task-validate`, `task-testing-review`
Globales (`~/.agents/skills`): `codely-git-conventional_commit` (commits), `codely-doc-create`, `codely-plan-create-gitlab`, `codely-plan_phase-implement-gitlab`, `find-skills`

---
**Updated:** 2026-10-30
