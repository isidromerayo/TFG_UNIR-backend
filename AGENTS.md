# AGENTS.md - Guía para Agentes AI

## Proyecto
Java 21 + Spring Boot 3.5.11 + Maven. Backend con seguridad JWT, H2 (tests), PostgreSQL (prod).

## Estructura del Proyecto
```
src/main/java/eu/estilolibre/tfgunir/backend/
├── controller/    # REST endpoints
├── service/      # Lógica de negocio
├── repository/   # Acceso a datos JPA
├── model/        # Entidades JPA
├── dto/          # Data Transfer Objects
├── config/       # Configuración Spring
├── security/     # JWT, filtros, auth
├── exception/    # Manejo centralizado de errores
└── TfgUnirBackendApplication.java
```

## Comandos

### Build y Run
```bash
./mvnw clean compile
./mvnw package -DskipTests
./mvnw spring-boot:run
```

### Tests
```bash
# Unitarios (todos)
./mvnw test

# Integración
./mvnw -Pintegration-tests verify

# Un solo test unitario (clase exacta)
./mvnw test -Dtest=UsuarioServiceTest

# Un solo test de integración
./mvnw -Pintegration-tests verify -Dit.test=LoginControllerIT

# Por patrón (wildcard)
./mvnw test -Dtest="*ServiceTest"
./mvnw test -Dtest="*ControllerTest,*ServiceTest"

# Tests en paralelo (más rápido)
./mvnw test -Dparallel=classes -DuseUnlimitedThreads=true

# Verbose output para debug
./mvnw test -Dtest=UsuarioServiceTest -Dsurefire.useFile=false
```

### Quality
```bash
# SpotBugs (análisis estático)
./mvnw compile spotbugs:check

# Cobertura (reportes en target/site/jacoco/)
./mvnw clean verify -Pintegration-tests

# OWASP Dependency Check (vulnerabilidades)
./mvnw -Pdependency-check verify -Dnvd.api.key=${NVD_API_KEY}

# Análisis SonarQube
./mvnw sonar:sonar
```

---

## Convenciones de Código

### Imports (orden específico)
```java
// 1. Paquete
package eu.estilolibre.tfgunir.backend.controller;

// 2. java stdlib (alfabético)
import java.util.List;

// 3. Librerías externas (Spring, Jakarta, etc.)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

// 4. Locales del proyecto
import eu.estilolibre.tfgunir.backend.model.Usuario;
```

### Nomenclatura
- Clases/Interfaces: `PascalCase` (e.g., `UsuarioService`)
- Métodos/variables: `camelCase` (e.g., `findByEmail`)
- Constantes: `UPPER_SNAKE_CASE` con `static final`
- Paquetes: minúsculas (e.g., `eu.estilolibre.tfgunir.backend.security`)
- Tests: `NombreClaseTest` / `NombreClaseIT` (para integración)

### Inyección de Dependencias
- **Prefiere constructor**, usa `final` para campos inmutables
```java
private final UsuarioRepository repository;

@Autowired
public LoginController(UsuarioRepository repository) {
    this.repository = repository;
}
```

### DTOs
- `record` para response DTOs inmutables
- `@Data` (Lombok) para request DTOs y entidades JPA
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
- Usa `@Data` + `@EqualsAndHashCode(exclude = {...})` para evitar ciclos
- `@Column(unique = true)` para restricciones únicas
```java
@Data
@Entity
@Table(name = "usuarios")
@lombok.EqualsAndHashCode(exclude = {"cursos", "avances"})
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(unique = true)
    private String email;
}
```

### Servicios
- `@Transactional` para operaciones de escritura
- `@Transactional(readOnly = true)` para consultas
```java
@Service
public class UsuarioService {
    private final UsuarioRepository repository;

    @Transactional(readOnly = true)
    public Optional<Usuario> findById(long id) {
        return repository.findById(id);
    }
}
```

### Controladores REST
- Usa `@RestController` + `@RequestMapping`
- Devuelve `ResponseEntity<?>` para control de HTTP
- Anota DTOs de request con `@Valid`
```java
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService service;

    @PostMapping
    public ResponseEntity<UsuarioResponse> create(@Valid @RequestBody CreateUserRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }
}
```

### Manejo de Errores
- `@ControllerAdvice` para excepciones centralizadas
- Códigos HTTP apropiados (400, 401, 403, 404, 500)
- No expongas detalles internos al cliente
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

### Validación
- Anotaciones Jakarta (`@NotBlank`, `@Email`, `@Size`, etc.)
- Usa `@Valid` en `@RequestBody`

### Logging
- `java.util.logging.Logger` (no SLF4J)
- No loguees passwords, tokens, datos sensibles
```java
private static final Logger LOGGER = Logger.getLogger(UsuarioService.class.getName());
```

### Seguridad JWT
- Filtros que extienden `OncePerRequestFilter`
- Tokens en headers `Authorization: Bearer <token>`
- CSRF deshabilitado para APIs stateless
```java
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        // Validar token y establecer autenticación
    }
}
```

---

## Testing (TDD)

### Estructura de Tests
- Unitarios: `src/test/java/` (mismo paquete que clase testada)
- Integración: `src/test/java/` con sufijo `IT`
- Usa `@DataJpaTest` para tests de repositorio
- Usa `@WebMvcTest` para tests de controlador
- Usa `@SpringBootTest` para tests de integración

### Patrones de Test
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
- MockMvc para tests web
- Testcontainers para BD en integración

---

## API Documentation
- Swagger/OpenAPI disponible en `/swagger-ui.html`
- Annotations: `@Operation`, `@ApiResponse`, `@Parameter`
```java
@Operation(summary = "Crear usuario", description = "Crea un nuevo usuario en el sistema")
@ApiResponse(responseCode = "201", description = "Usuario creado")
@PostMapping
public ResponseEntity<UsuarioResponse> create(...) { }
```

---

## Commits (Conventional Commits)
```bash
git commit -m "feat: add user registration endpoint"
git commit -m "fix: resolve null pointer in login"
git commit -m "test: add auth flow integration tests"
git commit -m "security: add rate limiting to login endpoint"
```

---

## Reglas de Oro

1. **Tests obligatorios** para cambios de código (unitarios + integración)
2. **SpotBugs** debe pasar antes de commit
3. **Cobertura objetivo**: ≥80%
4. Crear rama nueva para cada feature/fix
5. Ejecutar test específico que falla antes de todos: `./mvnw test -Dtest=NombreTest`
6. **Nunca expongas secrets** en código o logs
7. Valida todas las inputs con Bean Validation
8. Dependencias actualizadas (OWASP check)

---

## Perfiles Spring

- `default`: Desarrollo local (H2)
- `test`: Tests unitarios (H2 en memoria)
- `prod`: PostgreSQL, producción

---

**Última actualización:** 2026-02-22
