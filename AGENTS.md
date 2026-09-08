# AGENTS.md - TFG UNIR Backend

## Stack
- Java 21 + Spring Boot 4.0.8 + Maven 3.9.9 (`./mvnw`) — migrado desde 3.5.16 (EOL OSS) el 2026-09-08
- Spring Framework 7.0.x, Spring Security 7.0.x, Tomcat 11.0.25 (override: Boot 4.0.8 gestiona 11.0.24, vulnerable)
- Jackson 3 (`tools.jackson.*`) **3.1.6** (override de 3.1.5: CVE-2026-19032/83557, Snyk); Jackson 2 solo transitivo (jjwt)
- Test slices modulares: `spring-boot-data-jpa-test`, `spring-boot-jdbc-test`, `spring-boot-resttestclient` + `spring-boot-restclient`
- rest-assured 6.0.1 vía `rest-assured-bom` (Boot 4 ya no lo gestiona), springdoc-openapi 3.1.1
- H2 (tests), PostgreSQL (prod)
- JWT auth, CSRF disabled, stateless API
- Logging: `java.util.logging.Logger` (not SLF4J)
- Security plugins: SpotBugs 4.10.2 + FindSecBugs 1.14.0 + sb-contrib 7.7.4

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

# Vulnerability scan (requires NVD_API_KEY) - skips tests, only runs OWASP check
./mvnw -Pdependency-check dependency-check:check -Dnvd.api.key=$NVD_API_KEY
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

```bash
# 1. Preparar release (elimina -SNAPSHOT, crea tag vX.Y.Z, sube a next SNAPSHOT)
mvn release:prepare

# 2. Compilar desde el tag
git checkout vX.Y.Z && ./mvnw clean package -DskipTests

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
| Dependency upgrade | `pom.xml` versions, `AGENTS.md` Stack section |
| New feature/bugfix | `README.md` if user-facing |
| Removing legacy code | Update all references to removed files/commands |

**Pre-PR checklist:**
- [ ] All affected documentation updated
- [ ] `grep -r "removed-feature" docs/ scripts/ README.md` returns no stale references
- [ ] No broken links to deleted files
- [ ] `AGENTS.md` Stack section updated if dependencies changed

## Tooling
- JUnit 5 + Mockito + AssertJ
- `@DataJpaTest`, `@WebMvcTest`, `@SpringBootTest` + Testcontainers
- SpotBugs config: `src/main/resources/spotbugs-exclude.xml`
- SonarQube: https://sonarcloud.io/project/overview?id=isidromerayo_TFG_UNIR-backend

## Known Vulnerabilities
Estado tras migración a Spring Boot 4.0.8 (2026-09-08): **0 vulnerabilidades reales** en el classpath
(re-scan NVD completo en `docs/security/informe-vulnerabilidades-2026-09-08.md`).
Pendiente: **bump a Spring Boot 4.1.x antes del 31/12/2026** (fin de soporte OSS de la línea 4.0).
Run OWASP scan periodically: `./mvnw -Pdependency-check dependency-check:check -Dnvd.api.key=$NVD_API_KEY`
(En CI requiere el secret `NVD_API_KEY`; el workflow debe ejecutar el escaneo NVD completo.)

### Dependency-Check False Positives
These CVEs are flagged by the CPE matcher but do **not** affect the project:
- **CVE-2026-47849, CVE-2026-47850** on `spring-boot-data-rest-4.0.8.jar` — the CPE matcher matches the Boot module version (4.0.8) against "Spring Data REST 4.0.0–4.4.15" ranges. The real libraries (`spring-data-rest-webmvc`/`spring-data-rest-core` **5.0.7**, managed by Boot 4.0.8) are outside the vulnerable ranges (5.0.0–5.0.6) and already patched.
- **CVE-2022-31691** on `spring-boot-devtools-4.0.8.jar` — this CVE targets the Spring Tools 4 Eclipse/VSCode extensions, not `spring-boot-devtools`. Devtools is dev-only and excluded from the repackaged jar.
- **CVE-2026-34479, CVE-2026-34477** on `log4j-api-2.24.3.jar` — both require `log4j-core` (not present). The project only has `log4j-api` (interfaces) and `log4j-to-slf4j` (routing bridge). These CVEs target the Log4j 1→2 bridge XML layout and SocketAppender SSL — none of which are used. *(Ya no aplican: la migración trae `log4j-api` 2.25.5.)*
- **All CVEs on `swagger-ui-5.32.2.jar` (DOMPurify@3.3.2)** — Swagger UI is a dev-only client-side tool served via `springdoc-openapi`. DOMPurify runs in the browser, sanitizing user-supplied HTML before rendering. The backend never passes user HTML through DOMPurify, so these CVEs are not exploitable server-side. No remediation required. *(Actual: `swagger-ui` 5.32.14.)*

## Skills
`springboot-tdd`, `springboot-security`, `springboot-patterns`, `xp-tdd-practices`, `testing-standards`, `action-tdd`, `task-validate`, `task-testing-review`

---
**Updated:** 2026-09-08
