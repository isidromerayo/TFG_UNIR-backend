# AGENTS.md - TFG UNIR Backend

## Proyecto
- **Java 21** + **Spring Boot 3.5.11** + **Maven**
- H2 (tests/local), PostgreSQL (prod)
- JWT authentication

## Estructura
```
src/main/java/eu/estilolibre/tfgunir/backend/
├── controller/   # REST endpoints
├── service/      # Lógica de negocio
├── repository/   # JPA data access
├── model/        # JPA entities
├── dto/          # Data Transfer Objects
├── config/       # Spring configuration
├── security/     # JWT, filters, auth
└── exception/    # Centralized error handling
```

## Comandos

### Build & Run
```bash
./mvnw clean compile
./mvnw package -DskipTests
./mvnw spring-boot:run
```

### Tests
```bash
# All unit tests
./mvnw test

# Integration tests
./mvnw -Pintegration-tests verify

# Single test class
./mvnw test -Dtest=UsuarioServiceTest
./mvnw -Pintegration-tests verify -Dit.test=LoginControllerIT

# By pattern (wildcard)
./mvnw test -Dtest="*ServiceTest"

# Parallel execution
./mvnw test -Dparallel=classes -DuseUnlimitedThreads=true
```

### Quality
```bash
# SpotBugs static analysis
./mvnw compile spotbugs:check

# Coverage reports (target/site/jacoco/)
./mvnw clean verify -Pintegration-tests

# SonarQube
./mvnw sonar:sonar

# Vulnerability scanning (syft + grype)
syft . -o cyclonedx-json=sbom.json
grype sbom:sbom.json --only-fixed --by-cve

# Trivy scan
trivy fs --scanners vuln --severity HIGH,CRITICAL --ignore-unfixed .
```

---

## Convenciones de Código

### Imports (orden)
```java
package eu.estilolibre.tfgunir.backend.controller;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import eu.estilolibre.tfgunir.backend.model.Usuario;
```

### Nomenclatura
| Elemento | Formato |
|----------|---------|
| Clases/Interfaces | `PascalCase` (`UsuarioService`) |
| Métodos/variables | `camelCase` (`findByEmail`) |
| Constantes | `UPPER_SNAKE_CASE` (`MAX_RETRIES`) |
| Paquetes | minúsculas |
| Tests unitarios | `NombreClaseTest` |
| Tests integración | `NombreClaseIT` |

### Inyección de Dependencias
Constructor injection con `final`:
```java
private final UsuarioRepository repository;
private final TokenService tokenService;

@Autowired
public LoginController(UsuarioRepository repository, TokenService tokenService) {
    this.repository = repository;
    this.tokenService = tokenService;
}
```

### DTOs
- `record` para responses inmutables
- `@Data` (Lombok) para requests y entidades JPA
```java
// Response inmutable
private record ErrorResponse(String message) {}

// Request mutable
@Data
public class FormUser {
    private String email;
    private String password;
}
```

### Entidades JPA
```java
@Data
@Entity
@Table(name = "usuarios")
@lombok.EqualsAndHashCode(exclude = {"misCursosComprados", "avances"})
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(unique = true)
    private String email;
}
```

### Servicios
```java
@Service
public class UsuarioService {
    private final UsuarioRepository repository;

    @Transactional(readOnly = true)
    public Optional<Usuario> findById(long id) {
        return repository.findById(id);
    }
    
    @Transactional
    public Usuario save(Usuario usuario) {
        return repository.save(usuario);
    }
}
```

### Controladores REST
```java
@RestController
@RequestMapping("/api/auth")
public class LoginController {
    private final UsuarioRepository repository;

    @PostMapping
    public ResponseEntity<Object> auth(@RequestBody FormUser login) {
        return ResponseEntity.ok(result);
    }
}
```

### Manejo de Errores
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage()));
    }
}
```

### Logging
- Usa `java.util.logging.Logger` (NO SLF4J)
- Nunca loguear passwords, tokens o datos sensibles
```java
private static final Logger logger = Logger.getLogger(UsuarioService.class.getName());
```

### Validación
- Anotaciones Jakarta (`@NotBlank`, `@Email`, `@Size`)
- Usa `@Valid` en `@RequestBody`

### Seguridad JWT
- Filtros extienden `OncePerRequestFilter`
- Tokens en `Authorization: Bearer <token>`
- CSRF deshabilitado (stateless API)

---

## Testing

### Patrones
```java
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock UsuarioRepository repository;
    @InjectMocks UsuarioService service;

    @Test
    void findById_returnsUser_whenExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        Optional<Usuario> result = service.findById(1L);
        assertThat(result).isPresent();
    }
}
```

### Herramientas
- JUnit 5 + Mockito
- AssertJ para assertions
- `@DataJpaTest` para repositorios
- `@WebMvcTest` para controladores
- `@SpringBootTest` para integración
- Testcontainers para BD en tests de integración

---

## Development Workflow

Follow TDD for every change:

1. **RED**: Write a failing test first
2. **GREEN**: Write minimal code to pass
3. **REFACTOR**: Clean up with tests green
4. **VERIFY**: Run full validation

See `xp-tdd-practices` for detailed TDD cycle guidance.

---

## Non-Negotiable Rules

> These rules are **never optional**. Violating them requires immediate correction.

1. **No production code without a failing test first**
2. **Never skip TDD** - Tests are not optional
3. **Never commit directly to main** - Use feature branches
4. **Never leave the build red** - Fix failures immediately
5. **Never expose secrets in code or logs**
6. **Always validate inputs with Bean Validation**
7. **Always refactor only with tests green**

---

## Quick Verification

Before any commit, run:

```bash
./mvnw clean compile && ./mvnw test && ./mvnw compile spotbugs:check
```

This validates: compile → unit tests → static analysis

For full coverage check:
```bash
./mvnw clean verify -Pintegration-tests
```

---

## Coverage Strategy

### JaCoCo Exclusions (Current)

The project intentionally excludes certain packages from coverage to focus on tested business logic:

| Package | Coverage | Reason |
|---------|----------|--------|
| `model/` | Excluded | JPA entities - tested via integration tests |
| `dto/` | Excluded | Simple DTOs with minimal logic |
| `config/` | Excluded | Spring configuration classes |

**Current coverage: 100%** on included packages (security, controller, repository)

### Coverage Goals

| Layer | Target | Method |
|-------|--------|--------|
| Security | 100% | Unit + Integration tests |
| Controller | 100% | Integration tests (MockMvc) |
| Repository | 100% | @DataJpaTest |
| Service | ≥90% | Unit tests (when implemented) |
| Model/DTO | N/A | Excluded from metrics |

### Adding Tests for Excluded Packages

When adding tests for model/dto/config:

1. **Remove exclusions** from `pom.xml` (jacoco-maven-plugin configuration)
2. **Add corresponding tests** following TDD
3. **Update coverage targets** to ≥80% overall

```bash
# After adding tests and removing exclusions:
./mvnw clean verify -Pintegration-tests
```

---

## API Documentation
Swagger UI en `/swagger-ui.html`
```java
@Operation(summary = "Crear usuario")
@ApiResponse(responseCode = "201")
@PostMapping
public ResponseEntity<UsuarioResponse> create(...) { }
```

---

## Skills Available

| Skill | Purpose |
|-------|---------|
| `springboot-tdd` | TDD workflow, JUnit 5, Mockito patterns |
| `springboot-security` | Auth, JWT, CSRF, OWASP best practices |
| `springboot-patterns` | REST APIs, layered architecture, DTOs |
| `xp-tdd-practices` | TDD cycle, TPP, Inside-Out/Outside-In |
| `testing-standards` | FIRST principles, Arrange-Act-Assert, naming |
| `action-tdd` | Enforce TDD cycle when being skipped |
| `task-validate` | Full validation: compile → test → coverage |
| `task-testing-review` | Test quality and coverage review |

---

## Reglas de Oro

1. Tests obligatorios para cambios de código
2. SpotBugs debe pasar antes de commit
3. Cobertura objetivo: ≥80%
4. Crear rama nueva para cada feature/fix
5. Nunca expongas secrets en código o logs
6. Valida todas las inputs con Bean Validation

---

**Última actualización:** 2026-04-01
