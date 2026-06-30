# AGENTS.md - TFG UNIR Backend

## Stack
- Java 21 + Spring Boot 3.5.16 + Maven 3.9.9 (`./mvnw`)
- H2 (tests), PostgreSQL (prod)
- JWT auth, CSRF disabled, stateless API
- Logging: `java.util.logging.Logger` (not SLF4J)
- Security plugins: SpotBugs 4.10.2 + FindSecBugs 1.14.0 + fb-contrib 7.7.4

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
2. **No direct commits to main** - use feature branches
3. **SpotBugs must pass** before commit
4. **Never log secrets** (passwords, tokens)
5. **Validate inputs** with Jakarta annotations (`@Valid`, `@NotBlank`, `@Email`)
6. **Update docs** - review and update README/docs when changing features or config

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

# 3. Publicar imágenes (valida que NO sea SNAPSHOT)
./scripts/publish-images.sh

# 4. Verificar lo que se publicaría sin ejecutar
./scripts/publish-images.sh --dry-run

# 5. Volver a main y subir tags
git checkout main && git push origin main --tags
```

## Tooling
- JUnit 5 + Mockito + AssertJ
- `@DataJpaTest`, `@WebMvcTest`, `@SpringBootTest` + Testcontainers
- SpotBugs config: `src/main/resources/spotbugs-exclude.xml`
- SonarQube: https://sonarcloud.io/project/overview?id=isidromerayo_TFG_UNIR-backend

## Known Vulnerabilities
No Tomcat CVEs — Spring Boot 3.5.16 ships `tomcat-embed-core-10.1.55` (all previous CVEs fixed).
Run OWASP scan periodically: `./mvnw -Pdependency-check dependency-check:check -Dnvd.api.key=$NVD_API_KEY`

### Dependency-Check False Positives
These CVEs are flagged by the CPE matcher but do **not** affect the project:
- **CVE-2026-34479, CVE-2026-34477** on `log4j-api-2.24.3.jar` — both require `log4j-core` (not present). The project only has `log4j-api` (interfaces) and `log4j-to-slf4j` (routing bridge). These CVEs target the Log4j 1→2 bridge XML layout and SocketAppender SSL — none of which are used.
- **All CVEs on `swagger-ui-5.32.2.jar` (DOMPurify@3.3.2)** — Swagger UI is a dev-only client-side tool served via `springdoc-openapi`. DOMPurify runs in the browser, sanitizing user-supplied HTML before rendering. The backend never passes user HTML through DOMPurify, so these CVEs are not exploitable server-side. No remediation required.

## Skills
`springboot-tdd`, `springboot-security`, `springboot-patterns`, `xp-tdd-practices`, `testing-standards`, `action-tdd`, `task-validate`, `task-testing-review`

---
**Updated:** 2026-06-28
