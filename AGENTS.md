# AGENTS.md - Guía para Agentes AI

## Proyecto
Java 21 + Spring Boot 3.5.11 + Maven. Backend con seguridad JWT, H2 (tests), PostgreSQL (prod).

## Comandos

### Build
```bash
./mvnw clean compile
./mvnw package -DskipTests
```

### Tests
```bash
# Unitarios
./mvnw test

# Integración
./mvnw -Pintegration-tests verify

# Un solo test unitario
./mvnw test -Dtest=UsuarioServiceTest

# Un solo test de integración
./mvnw -Pintegration-tests verify -Dit.test=LoginControllerIT
```

### Quality
```bash
# SpotBugs (análisis estático)
./mvnw compile spotbugs:check

# Cobertura (genera reportes en target/site/jacoco/)
./mvnw clean verify -Pintegration-tests

# OWASP Dependency Check
./mvnw -Pdependency-check verify -Dnvd.api.key=${NVD_API_KEY}
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

### Manejo de Errores
- `@ControllerAdvice` para excepciones centralizadas
- Códigos HTTP apropiados (400, 401, 403, 404, 500)
- No expongas detalles internos al cliente

### Validación
- Anotaciones Jakarta (`@NotBlank`, `@Email`, `@Size`, etc.)
- Usa `@Valid` en `@RequestBody`

### Logging
- `java.util.logging.Logger` (no SLF4J)
- No loguees passwords, tokens, datos sensibles

### Commits (Conventional Commits)
```bash
git commit -m "feat: add user registration endpoint"
git commit -m "fix: resolve null pointer in login"
git commit -m "test: add auth flow integration tests"
```

---

## Reglas de Oro

1. **Tests obligatorios** para cambios de código (unitarios + integración)
2. **SpotBugs** debe pasar antes de commit
3. **Cobertura objetivo**: ≥80%
4. Crear rama nueva para cada feature/fix
5. Ejecutar test específico que falla antes de todos: `./mvnw test -Dtest=NombreTest`

---

**Última actualización:** 2026-02-22
