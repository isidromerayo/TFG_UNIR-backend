---
name: task-testing-review
description: Reviews test quality and coverage for Spring Boot projects. Checks FIRST principles compliance, mock usage, and coverage metrics. Use after test implementation.
---

# Task: Testing Review

Comprehensive review of test quality and coverage metrics.

## Review Dimensions

### 1. Test Structure (FIRST Principles)

| Principle | Check | Status |
|-----------|-------|--------|
| **Fast** | No slow I/O in unit tests | ⬜ |
| **Independent** | No test dependencies | ⬜ |
| **Repeatable** | Same result every run | ⬜ |
| **Self-validating** | No manual checks | ⬜ |
| **Timely** | Written before code | ⬜ |

### 2. Test Naming Convention

Verify all tests follow: `[MethodName]_[Scenario]_[ExpectedResult]`

```java
// Good ✅
void findById_returnsUser_whenUserExists()
void save_throwsException_whenEmailIsInvalid()

// Bad ❌
void testFindById()
void testSaveUser()
```

### 3. Arrange-Act-Assert Structure

```java
@Test
void example() {
    // Arrange - Set up test data and mocks
    // ...
    
    // Act - Execute single action
    // ...
    
    // Assert - Verify outcome
    // ...
}
```

### 4. Mock Usage Policy

✅ **Good Mock Usage:**
- External dependencies (repositories, services)
- Slow operations (network, I/O)
- Non-deterministic (time, random)

❌ **Avoid Mocking:**
- Value objects (DTOs, records)
- Classes under test
- Simple data structures

## Coverage Review

### Coverage Targets

| Layer | Target | Command |
|-------|--------|---------|
| Service | ≥90% | JaCoCo report |
| Controller | ≥80% | JaCoCo report |
| Repository | ≥70% | JaCoCo report |
| **Overall** | **≥80%** | JaCoCo report |

### Coverage Report Location

```
target/site/jacoco/index.html
```

### Missing Coverage Check

1. Open JaCoCo report
2. Identify packages with <80% coverage
3. Add tests for:
   - Edge cases
   - Exception paths
   - Branch coverage

### Coverage Gaps to Address

Common areas often missing coverage:

- [ ] Exception handling paths
- [ ] Validation logic
- [ ] Boundary conditions
- [ ] Null checks
- [ ] Empty collections

## Test Quality Metrics

| Metric | Target | Measurement |
|--------|--------|-------------|
| Tests per class | 3-10 | Count |
| Avg test length | < 30 lines | Code review |
| Assertion count | 1-3 per test | Code review |
| Mock count | Minimized | Code review |

## Common Test Smells

Check for these issues:

| Smell | Description | Fix |
|-------|-------------|-----|
| Assertion roulette | Multiple unrelated assertions | Split tests |
| Eager test | Too many assertions | Focus on one behavior |
| Mystery guest | External dependencies | Inline test data |
| Test logic | `if` statements in tests | Use parameterized tests |
| Hardcoded paths | Brittle file loading | Use classpath resources |
| General fixture | Overly shared setup | Extract only common parts |

## Review Checklist

### Structure
- [ ] All tests follow Arrange-Act-Assert
- [ ] Tests are named descriptively
- [ ] No test dependencies (can run in any order)
- [ ] Tests are fast (<100ms unit tests)

### Coverage
- [ ] Overall coverage ≥80%
- [ ] Service layer coverage ≥90%
- [ ] Exception paths covered
- [ ] Edge cases covered

### Mocks
- [ ] Mocks at boundaries only
- [ ] No mocking of DTOs/value objects
- [ ] Stubs are specific (not `any()`)

### Data
- [ ] Test data builders used for complex objects
- [ ] No shared mutable state
- [ ] Test data is deterministic

## Commands for Review

### Generate Coverage Report
```bash
./mvnw verify -Pintegration-tests
```

### Run Specific Test Class
```bash
./mvnw test -Dtest=UsuarioServiceTest
```

### Run Tests with Details
```bash
./mvnw test -DtrimStackTrace=false
```

### Generate HTML Report
```bash
./mvnw jacoco:report
```

## Output Format

After review, provide:

```
## Test Quality Report

### Coverage Summary
- Overall: 85% ✅
- Service: 92% ✅
- Controller: 88% ✅

### Issues Found
1. [HIGH] UsuarioService: Missing test for null email validation
2. [MEDIUM] LoginController: Test has 5 assertions (should be 1-3)

### Recommendations
1. Add test for ResourceNotFoundException in findById
2. Split testSave_withInvalidEmail into two tests
```

## Related

- `testing-standards` - Detailed testing guidelines
- `xp-tdd-practices` - TDD cycle
- `task-validate` - Full validation pipeline
- `action-tdd` - Enforce TDD
