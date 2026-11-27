# Design: Hexagonal Architecture Migration

## Context

Current architecture follows a traditional 3-layer Spring Boot pattern:
- **Controller Layer**: REST endpoints (`controller/`)
- **Service Layer**: Business logic (`service/`)
- **Repository Layer**: Data access (`repository/`)
- **Model Layer**: JPA entities (`model/`)

All layers are tightly coupled to Spring and JPA. The `model/` package contains JPA entities that mix domain logic with persistence concerns.

See proposal.md - Why for the motivation behind this migration.

## Goals / Non-Goals

**Goals:**
- Achieve clean separation of concerns with Hexagonal Architecture
- Make domain logic independent of frameworks (Spring, JPA)
- Enable pure unit testing of domain logic without Spring context
- Maintain 100% backward compatibility with existing REST API
- Follow TDD methodology for all new code
- Incremental migration with working system at each step

**Non-Goals:**
- Change REST API contracts (endpoints, request/response formats)
- Change database schema or JPA entity structure
- Introduce new external dependencies
- Migrate authentication/security layer until final phase
- Optimize performance (focus is on architecture clarity)

## Decisions

### 1. Hexagonal Architecture Pattern

**Decision**: Use Ports & Adapters (Hexagonal) with 3 main layers:
```
+------------------+     +------------------+     +------------------+
|   DRIVING        |     |   APPLICATION    |     |     DOMAIN       |
|   ADAPTERS       |     |   (Use Cases)    |     |   (Core Logic)   |
+------------------+     +------------------+     +------------------+
        ^                        ^                        ^
        |                        |                        |
+------------------+     +------------------+     +------------------+
|  REST Controllers|     |   Application    |     |   Domain Models  |
|  (HTTP)          |     |   Services       |     |   & Value Objects|
+------------------+     |   (Orchestration) |     +------------------+
        ^                        ^                        
        |                        |
+------------------+     +------------------+
|  GraphQL/CLI     |     |   Ports          |  (Future)
|  (Future)        |     |   (Interfaces)   |
+------------------+     +------------------+
```

**Rationale**:
- Clear separation between what the system DOES (domain) and HOW it's accessed (adapters)
- Domain layer contains pure business logic, independent of frameworks
- Application layer orchestrates use cases using domain logic
- Adapters handle infrastructure concerns (REST, JPA, etc.)

**Alternatives Considered**:
- **Clean Architecture**: More layers (entities, use cases, interface adapters, framework), but adds complexity without clear benefit for this project size
- **Onion Architecture**: Similar to Hexagonal but with different terminology, less commonly used with Spring
- **Keep current**: Not viable - tight coupling makes maintenance increasingly difficult

### 2. Package Structure

**Decision**: Use package-by-feature with hexagonal layers:
```
src/main/java/eu/estilolibre/tfgunir/backend/
├── domain/                    # DOMAIN LAYER (Core)
│   ├── modelo/                # Domain models (pure POJOs)
│   │   ├── Curso.java         # Domain Curso (no JPA annotations)
│   │   ├── Usuario.java       # Domain Usuario
│   │   ├── Categoria.java     # Domain Categoria
│   │   └── ...
│   ├── exception/             # Domain exceptions
│   │   └── DomainException.java
│   ├── repository/            # Domain ports (interfaces for driven adapters)
│   │   ├── CursoRepository.java    # Interface
│   │   ├── UsuarioRepository.java  # Interface
│   │   └── ...
│   └── service/               # Domain services (pure business logic)
│       ├── CursoDomainService.java
│       └── ...
│
├── application/               # APPLICATION LAYER (Use Cases)
│   ├── port/                  # Application ports
│   │   ├── in/                # Driving ports (interfaces for incoming)
│   │   │   ├── CursoUseCase.java
│   │   │   └── UsuarioUseCase.java
│   │   └── out/               # Driven ports (interfaces for outgoing)
│   │       ├── CursoPort.java
│   │       └── UsuarioPort.java
│   ├── service/               # Use case implementations
│   │   ├── CursoService.java  # Implements CursoUseCase
│   │   └── UsuarioService.java
│   └── dto/                   # Application DTOs (request/response for use cases)
│
└── infrastructure/           # INFRASTRUCTURE LAYER (Adapters)
    ├── web/                   # DRIVING ADAPTERS (REST)
    │   ├── controller/        # Spring @RestController
    │   │   ├── CursoController.java
    │   │   └── UsuarioController.java
    │   └── mapper/            # DTO mappers (to/from REST)
    │
    ├── persistence/           # DRIVEN ADAPTERS (JPA)
    │   ├── entity/            # JPA entities
    │   │   ├── CursoEntity.java
    │   │   ├── UsuarioEntity.java
    │   │   └── ...
    │   ├── repository/        # Spring Data JPA repositories
    │   │   ├── CursoJpaRepository.java
    │   │   └── UsuarioJpaRepository.java
    │   └── mapper/            # Entity-Domain mappers
    │
    └── config/                # Spring configuration
        ├── SecurityConfig.java
        └── ...
```

**Rationale**:
- **domain/**: Contains the heart of the business, completely framework-agnostic
- **application/**: Orchestrates use cases, depends on domain but not on infrastructure
- **infrastructure/**: Handles all framework-specific code (Spring, JPA)

**Mapping from current structure**:
- Current `model/` → Split: domain pure models + infrastructure JPA entities
- Current `service/` → application/service (use case implementations)
- Current `repository/` → infrastructure/persistence/repository
- Current `controller/` → infrastructure/web/controller

### 3. Domain vs JPA Entity Separation

**Decision**: Maintain separate domain models and JPA entities with bidirectional mapping.

**Example**: Curso
```java
// Domain Model (domain/modelo/Curso.java)
package eu.estilolibre.tfgunir.backend.domain.modelo;

public class Curso {
    private Long id;
    private String titulo;
    private String descripcion;
    private BigDecimal precio;
    // ... pure domain logic, no JPA annotations
}

// JPA Entity (infrastructure/persistence/entity/CursoEntity.java)
package eu.estilolibre.tfgunir.backend.infrastructure.persistence.entity;

@Entity
@Table(name = "cursos")
public class CursoEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descripcion;
    @Column(precision = 8, scale = 2)
    private BigDecimal precio;
    // ... JPA specific fields and relationships
}

// Mapper (infrastructure/persistence/mapper/CursoMapper.java)
public class CursoMapper {
    public Curso toDomain(CursoEntity entity) { ... }
    public CursoEntity toEntity(Curso domain) { ... }
}
```

**Rationale**:
- Domain models remain pure and testable without JPA/Hibernate
- JPA entities handle persistence concerns (annotations, lazy loading, etc.)
- Mappers handle conversion between layers
- This is a common pattern in Hexagonal Architecture with JPA

**Alternatives Considered**:
- Single model with JPA annotations: Would couple domain to JPA
- JPA entities extend domain models: Causes issues with JPA inheritance

### 4. Ports & Adapters Pattern

**Decision**: Use interface-based ports with explicit adapter implementations.

**Driving Ports (Incoming)**:
```java
// Application port (application/port/in/CursoUseCase.java)
public interface CursoUseCase {
    Curso crearCurso(Curso curso);
    Curso obtenerCurso(Long id);
    List<Curso> obtenerCursosPorCategoria(Long categoriaId);
    // ...
}

// Adapter (infrastructure/web/controller/CursoController.java)
@RestController
@RequestMapping("/api/cursos")
public class CursoController {
    private final CursoUseCase cursoUseCase;
    private final CursoDtoMapper mapper;
    
    @PostMapping
    public ResponseEntity<CursoResponse> crearCurso(@Valid @RequestBody CursoRequest request) {
        Curso curso = mapper.toDomain(request);
        Curso creado = cursoUseCase.crearCurso(curso);
        return ResponseEntity.ok(mapper.toResponse(creado));
    }
    // ...
}
```

**Driven Ports (Outgoing)**:
```java
// Application port (application/port/out/CursoPort.java)
public interface CursoPort {
    Optional<Curso> findById(Long id);
    Curso save(Curso curso);
    List<Curso> findByCategoriaId(Long categoriaId);
    // ...
}

// Adapter (infrastructure/persistence/CursoJpaAdapter.java)
@Component
@RequiredArgsConstructor
public class CursoJpaAdapter implements CursoPort {
    private final CursoJpaRepository jpaRepository;
    private final CursoMapper mapper;
    
    @Override
    public Optional<Curso> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
    
    @Override
    public Curso save(Curso curso) {
        CursoEntity entity = mapper.toEntity(curso);
        CursoEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    // ...
}
```

**Rationale**:
- Clear dependency direction: Application depends on interfaces, not implementations
- Easy to swap adapters (e.g., change from JPA to JDBC)
- Easy to mock for testing
- Follows Dependency Inversion Principle

### 5. Incremental Migration Strategy

**Decision**: Migrate modules in the following order (lowest risk to highest):

1. **Categoría** - Simplest domain, no complex relationships
2. **Contenido** - Depends on Curso, but simpler logic
3. **Valoración** - Depends on Curso and Usuario
4. **Curso** - Core domain, most complex
5. **Avance** - Depends on Usuario and Curso
6. **Usuario** - Complex domain with authentication
7. **Authentication** - Security layer (deferred to last)

**Rationale**:
- Start with least complex modules to build confidence and establish patterns
- Migrate dependencies before dependents (e.g., Categoria before Curso)
- Defer authentication to minimize risk of breaking security

**Migration Steps per Module**:
1. Create domain model (pure POJO)
2. Create domain repository interface (port)
3. Create application use case interface and implementation
4. Create JPA entity and mapper
5. Create JPA adapter implementation
6. Create/update REST controller adapter
7. Update tests to reflect new structure (TDD)
8. Verify all existing tests pass

### 6. TDD Approach

**Decision**: Strict TDD for all new code:

For each module migration:
1. Write failing test for domain model behavior
2. Implement domain model to pass test
3. Write failing test for domain service
4. Implement domain service
5. Write failing test for use case
6. Implement use case
7. Write failing test for adapter
8. Implement adapter
9. Refactor as needed

**Rationale**:
- Project mandate requires TDD
- Hexagonal architecture enables better unit testing
- Domain tests run without Spring context (faster)

## Risks / Trade-offs

### [Risk: Large code movement] → Mitigation
- **Risk**: Moving many files increases chance of import errors, broken references
- **Mitigation**: 
  - Use IDE refactoring tools for package moves
  - Run `./mvnw clean compile` after each module migration
  - Maintain comprehensive test suite
  - Frequent commits with clear messages

### [Risk: Temporary duplication] → Mitigation
- **Risk**: During migration, both old and new code may exist temporarily
- **Mitigation**:
  - Use `@Deprecated` annotations on old code
  - Remove old code immediately after new code is verified
  - Each module migration is atomic (old code removed, new code added)

### [Risk: Breaking existing functionality] → Mitigation
- **Risk**: API behavior changes despite best efforts
- **Mitigation**:
  - Run full integration test suite after each module
  - Manual API testing with Postman/curl
  - Maintain backward compatibility layer if needed

### [Risk: Performance overhead from mappers] → Mitigation
- **Risk**: Additional mapping between domain models and entities adds overhead
- **Mitigation**:
  - Use efficient mappers (e.g., MapStruct or simple static methods)
  - Profile performance before/after migration
  - Accept minimal overhead for better architecture

### [Risk: Team learning curve] → Mitigation
- **Risk**: Hexagonal Architecture may be new to team members
- **Mitigation**:
  - Document patterns and conventions
  - Code reviews with focus on architectural consistency
  - Pair programming for first few modules

## Migration Plan

### Phase 1: Preparation (Week 1)
1. Create new package structure
2. Set up base classes (exceptions, base mappers)
3. Create example migration for simplest module (Categoria)
4. Document patterns and conventions
5. Team review and approval

### Phase 2: Core Domain Migration (Weeks 2-4)
1. Migrate Categoria module
2. Migrate Contenido module
3. Migrate Valoracion module
4. Migrate Curso module
5. Migrate Avance module
6. Migrate Usuario module (without authentication)

### Phase 3: Final Phase (Week 5)
1. Migrate authentication/security layer
2. Clean up old code and packages
3. Final integration testing
4. Performance profiling
5. Documentation updates

### Rollback Strategy
- Git provides rollback capability
- Each module migration is a separate commit/PR
- Can rollback individual modules if issues arise
- Full rollback possible by reverting to pre-migration commit

## Open Questions

1. **Mapper Library**: Should we use MapStruct for automatic mapper generation, or keep manual mappers for simplicity? (Manual mappers chosen for now, can refactor later)

2. **Validation**: Where should validation (e.g., @NotBlank, @Email) live? Currently in DTOs. Should it move to domain models or stay in adapters? (Keep in adapters for now, domain validates business rules)

3. **Transaction Management**: Currently handled by Spring @Transactional in service layer. How to handle in Hexagonal? (Will use Spring @Transactional in application layer for now)

4. **Pagination**: Current REST endpoints use Spring Data pagination. How to handle in Hexagonal? (Will keep pagination in adapter layer)
