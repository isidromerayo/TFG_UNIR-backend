---
name: task-validate
description: Full validation task for Spring Boot projects. Executes compile → test → static analysis → coverage pipeline. Use before commits and after major changes.
---

# Task: Full Validation

Executes a complete validation pipeline to ensure code quality before commits or releases.

## Validation Pipeline

Execute in order. Stop on first failure.

### Step 1: Compile
```bash
./mvnw clean compile
```
**Validates:** Syntax, type checking, dependencies

### Step 2: Unit Tests
```bash
./mvnw test
```
**Validates:** All unit tests pass, no regressions

### Step 3: Static Analysis
```bash
./mvnw compile spotbugs:check
```
**Validates:** No SpotBugs violations, security issues

### Step 4: Integration Tests (optional)
```bash
./mvnw -Pintegration-tests verify
```
**Validates:** Full integration, database, API endpoints

### Step 5: Coverage Check
```bash
./mvnw verify -Pintegration-tests
```
**Validates:** Coverage reports in `target/site/jacoco/`

## Quick Validation (Pre-commit)

```bash
./mvnw clean compile && ./mvnw test && ./mvnw compile spotbugs:check
```

## Full Validation (Release)

```bash
./mvnw clean verify -Pintegration-tests
```

This generates:
- `target/site/jacoco/index.html` - Coverage report
- `target/site/spotbugs.html` - Static analysis report

## Acceptance Criteria

| Check | Minimum | Command |
|-------|---------|---------|
| Compilation | Success | `./mvnw compile` |
| Unit tests | 100% pass | `./mvnw test` |
| SpotBugs | 0 violations | `./mvnw spotbugs:check` |
| Coverage | 100% (included) | JaCoCo report |
| Integration | 100% pass | `./mvnw verify -Pintegration-tests` |

### Coverage Notes

This project uses **targeted coverage** - only security, controller, and repository packages count toward the 100% target. The following packages are excluded from metrics:

- `model/` - JPA entities
- `dto/` - Simple DTOs
- `config/` - Spring configuration

Review coverage at: `target/site/jacoco/index.html`

## Failure Handling

### Compilation Fails
- Check import statements
- Verify all dependencies resolved
- Run `./mvnw dependency:resolve`

### Tests Fail
- Run single test: `./mvnw test -Dtest=ClassName`
- Check test output for assertion errors
- Verify test data is correct

### SpotBugs Fails
- Review `target/spotbugsXml.xml`
- Fix high/medium priority issues
- Suppress false positives with `@SuppressFBWarnings`

### Coverage Fails
- Review `target/site/jacoco/index.html`
- Add tests for uncovered branches
- Target: Service layer ≥90%, Controller ≥80%

## Pre-commit Checklist

- [ ] `./mvnw clean compile` succeeds
- [ ] `./mvnw test` all pass
- [ ] `./mvnw spotbugs:check` passes
- [ ] Coverage = 100% (included packages: security, controller, repository)
- [ ] No secrets in code (check git diff)
- [ ] Tests follow naming convention
- [ ] Code follows conventions in AGENTS.md

## CI Integration

For automated pipelines:

```yaml
# Example: GitHub Actions
- name: Validate
  run: |
    ./mvnw clean compile
    ./mvnw test
    ./mvnw compile spotbugs:check
    ./mvnw verify -Pintegration-tests

- name: Coverage
  run: |
    ./mvnw verify -Pintegration-tests
    # Upload target/site/jacoco/index.html
```

## Related

- `xp-tdd-practices` - TDD cycle
- `testing-standards` - Test quality
- `action-tdd` - Enforce TDD
- `task-testing-review` - Detailed test review
