---
name: testing-standards
description: Testing standards including FIRST principles, Arrange-Act-Assert structure, naming conventions, and mocks policy. Required for all Spring Boot test code.
---

# Testing Standards

Comprehensive testing guidelines for maintainable, reliable test suites.

## FIRST Principles

| Principle | Description | Application |
|-----------|-------------|-------------|
| **F**ast | Tests should run quickly | Avoid slow I/O, database, network calls in unit tests |
| **I**ndependent | Tests should not depend on each other | Each test sets up its own data, no shared state |
| **R**epeatable | Tests should produce the same result | No reliance on external systems, timestamps, randomness |
| **S**elf-validating | Tests should pass/fail automatically | No manual verification needed |
| **T**imely | Tests written just before production code | Follow TDD cycle |

## Arrange-Act-Assert Pattern

Every test should follow this structure:

```java
@Test
void findById_returnsUser_whenUserExists() {
    // Arrange - Set up test data and mocks
    Usuario expectedUser = new Usuario();
    expectedUser.setId(1L);
    expectedUser.setEmail("test@example.com");
    when(repository.findById(1L)).thenReturn(Optional.of(expectedUser));
    
    // Act - Execute the behavior being tested
    Optional<Usuario> result = service.findById(1L);
    
    // Assert - Verify the expected outcome
    assertThat(result)
        .isPresent()
        .hasValueSatisfying(user -> 
            assertThat(user.getEmail()).isEqualTo("test@example.com")
        );
}
```

### Arrange Section
- Create test objects
- Set up mock behaviors
- Prepare input data
- Should be readable at a glance

### Act Section
- Call the method under test
- Should be a single statement
- Capture return value if needed

### Assert Section
- Verify expected behavior
- Use descriptive assertion messages
- Assert outcomes, not implementation

## Test Naming Conventions

### Method: `[MethodName]_[Scenario]_[ExpectedResult]`

```java
// Good
void save_returnsUser_whenDataIsValid()
void save_throwsException_whenEmailAlreadyExists()
void findByEmail_returnsOptional_whenUserNotFound()

// Bad
void testSave()
void saveUser()
void testFindByEmail()
```

### Class Naming

| Test Type | Suffix | Example |
|-----------|--------|---------|
| Unit Test | `Test` | `UsuarioServiceTest` |
| Integration Test | `IT` | `UsuarioControllerIT` |
| Test Helper | `TestDataBuilder` | `UsuarioTestDataBuilder` |

## Mocks Policy

### When to Use Mocks

✅ **Use mocks for:**
- External dependencies (repositories, services)
- Slow operations (network, file I/O)
- Non-deterministic behavior (time, random)
- Complex collaborators

### When NOT to Use Mocks

❌ **Don't mock:**
- Value objects (DTOs, records)
- Simple data structures
- The class under test itself

### Mock Best Practices

```java
// Good: Mock at boundaries
@Mock
UsuarioRepository repository;

@InjectMocks
UsuarioService service;

// Good: Specific stubbing
when(repository.findByEmail("test@example.com"))
    .thenReturn(Optional.of(usuario));

// Bad: Overly specific
when(repository.findById(anyLong()))
    .thenReturn(Optional.of(usuario));
```

## Test Data Builders

Use builders for complex test objects:

```java
public class UsuarioTestDataBuilder {
    private Long id = 1L;
    private String email = "test@example.com";
    private String nombre = "Test User";
    
    public UsuarioTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }
    
    public UsuarioTestDataBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
    
    public Usuario build() {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setEmail(email);
        usuario.setNombre(nombre);
        return usuario;
    }
}

// Usage
Usuario usuario = new UsuarioTestDataBuilder()
    .withEmail("custom@example.com")
    .build();
```

## Common Test Smells

| Smell | Problem | Solution |
|-------|---------|----------|
| Assertion roulette | Multiple unrelated assertions | Split into separate tests |
| Eager test | Too many assertions | Test one behavior per test |
| Test logic in production | `if` statements in tests | Use parameterized tests |
| Hardcoded paths | Brittle file/data loading | Use classpath resources |
| Mystery guest | External dependencies | Inline test data |
| General fixture | Shared setup with unrelated tests | Extract only common parts |
| Test code duplication | Repeated setup/assertions | Use builders, shared helpers |

## Test Organization

```
src/test/java/
└── eu/estilolibre/tfgunir/backend/
    ├── service/
    │   └── UsuarioServiceTest.java
    ├── controller/
    │   └── UsuarioControllerTest.java
    └── repository/
        └── UsuarioRepositoryTest.java
```

## Test Coverage Goals

| Layer | Minimum Coverage |
|-------|------------------|
| Service layer | 90% |
| Controller layer | 80% |
| Repository layer | 70% |
| Overall | 80% |

## Assertions Best Practices

```java
// Good: Descriptive with values
assertThat(user.getEmail())
    .isEqualTo("expected@example.com");

// Bad: No context
assertThat(user.getEmail()).isEqualTo("expected@example.com");

// Good: Chained for readability
assertThat(result)
    .isPresent()
    .hasValueSatisfying(u -> {
        assertThat(u.getEmail()).isEqualTo("test@example.com");
        assertThat(u.isActive()).isTrue();
    });

// Good: Exception testing
assertThatThrownBy(() -> service.save(null))
    .isInstanceOf(IllegalArgumentException.class)
    .hasMessageContaining("cannot be null");
```

## Integration vs Unit Tests

| Aspect | Unit Test | Integration Test |
|--------|----------|-----------------|
| Scope | Single class | Multiple layers |
| Dependencies | Mocked | Real (or Testcontainers) |
| Speed | < 100ms | Variable |
| Database | No | Yes (H2/Testcontainers) |
| Annotation | `@ExtendWith(MockitoExtension.class)` | `@SpringBootTest` |

## Non-Negotiable Rules

1. **Tests must be independent** - No shared mutable state
2. **Tests must be deterministic** - Same result every run
3. **Tests must be fast** - Keep unit tests under 100ms
4. **One assertion focus** - One behavior per test
5. **No commented tests** - Delete unused tests, don't comment

---

**Related Guidelines:**
- `xp-tdd-practices` - TDD cycle, TPP
- `springboot-tdd` - Spring Boot specific test patterns
