# AGENTS.md - TFG UNIR Backend

## Stack
- Java 21 + Spring Boot 3.5.14 + Maven (`./mvnw`)
- H2 (tests), PostgreSQL (prod)
- JWT auth, CSRF disabled, stateless API
- Logging: `java.util.logging.Logger` (not SLF4J)

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

# Vulnerability scan (requires NVD_API_KEY)
./mvnw -Pdependency-check verify -Dnvd.api.key=$NVD_API_KEY
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

## Tooling
- JUnit 5 + Mockito + AssertJ
- `@DataJpaTest`, `@WebMvcTest`, `@SpringBootTest` + Testcontainers
- SpotBugs config: `src/main/resources/spotbugs-exclude.xml`
- SonarQube: https://sonarcloud.io/project/overview?id=isidromerayo_TFG_UNIR-backend

## Known Vulnerabilities
Current scan shows Apache Tomcat CVEs (CVE-2026-34483, 34486, 34487, 34500) in `tomcat-embed-core-10.1.53`.
Check `target/dependency-check-report.html` after running OWASP scan.

## Skills
`springboot-tdd`, `springboot-security`, `springboot-patterns`, `xp-tdd-practices`, `testing-standards`, `action-tdd`, `task-validate`, `task-testing-review`

---
**Updated:** 2026-05-01
