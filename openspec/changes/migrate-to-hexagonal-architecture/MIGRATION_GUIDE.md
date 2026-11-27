# Guía de Migración a Arquitectura Hexagonal

## 📋 Introducción

Este documento describe los patrones, convenciones y buenas prácticas para migrar el backend de TFG UNIR de una arquitectura en capas tradicional a **Arquitectura Hexagonal (Ports & Adapters)**.

**Objetivo:** Mantener la funcionalidad actual mientras se mejora la testabilidad, mantenibilidad y flexibilidad del código.

---

## 🏗️ Estructura de Paquetes

```
src/main/java/eu/estilolibre/tfgunir/backend/
├── domain/                    # CAPA DE DOMINIO (Core Business)
│   ├── modelo/                # Modelos de dominio (POJOs puros)
│   │   └── *.java            # Ej: Curso.java, Usuario.java
│   ├── repository/            # Puertos de repositorio (interfaces)
│   │   └── *.java            # Ej: CursoRepository.java
│   ├── service/               # Servicios de dominio (lógica pura)
│   │   └── *.java            # Ej: CursoDomainService.java
│   └── exception/             # Excepciones de dominio
│       └── DomainException.java
│
├── application/               # CAPA DE APLICACIÓN (Use Cases)
│   ├── port/                  # Puertos de aplicación
│   │   ├── in/                # Puertos de entrada (driving)
│   │   │   └── *.java        # Ej: CursoUseCase.java
│   │   └── out/               # Puertos de salida (driven)
│   │       └── *.java        # Ej: CursoPort.java
│   ├── service/               # Implementación de casos de uso
│   │   └── *.java            # Ej: CursoService.java
│   └── dto/                   # DTOs de aplicación
│       └── *.java            # Ej: CursoDto.java
│
└── infrastructure/           # CAPA DE INFRAESTRUCTURA (Adapters)
    ├── web/                   # Adaptadores de entrada (REST)
    │   ├── controller/        # Controladores Spring
    │   │   └── *.java        # Ej: CursoController.java
    │   └── mapper/            # Mappers REST ↔ Application DTO
    │       └── *.java
    ├── persistence/           # Adaptadores de salida (JPA)
    │   ├── entity/            # Entidades JPA
    │   │   └── *.java        # Ej: CursoEntity.java
    │   ├── repository/        # Repositorios Spring Data JPA
    │   │   └── *.java        # Ej: CursoJpaRepository.java
    │   └── mapper/            # Mappers Entity ↔ Domain
    │       └── *.java        # Ej: CursoMapper.java
    └── config/                # Configuración de Spring
        └── *.java
```

---

## 📝 Convenciones de Nomenclatura

### Modelos de Dominio
- **Ubicación:** `domain/modelo/`
- **Nombre:** Nombre singular en PascalCase (ej: `Curso`, `Usuario`)
- **Características:**
  - POJOs puros (sin anotaciones JPA, Spring, etc.)
  - Contienen lógica de negocio pura
  - Pueden tener métodos de validación
  - No dependen de frameworks externos

### Entidades JPA
- **Ubicación:** `infrastructure/persistence/entity/`
- **Nombre:** Nombre singular + `Entity` (ej: `CursoEntity`, `UsuarioEntity`)
- **Características:**
  - Anotadas con `@Entity`, `@Table`, etc.
  - Manejan relaciones JPA
  - Pueden tener campos técnicos (fechas de auditoría, etc.)
  - No contienen lógica de negocio

### Puertos (Interfaces)
- **Ubicación:** 
  - Puertos de repositorio: `domain/repository/`
  - Puertos de aplicación: `application/port/`
- **Nombre:** Nombre singular + `Repository` o `Port` (ej: `CursoRepository`, `CursoPort`)
- **Características:**
  - Solo declaraciones de métodos (sin implementación)
  - No dependen de Spring u otros frameworks
  - Definen el contrato que los adapters deben implementar

### Adaptadores (Implementaciones)
- **Ubicación:** 
  - Adaptadores web: `infrastructure/web/`
  - Adaptadores JPA: `infrastructure/persistence/`
- **Nombre:** 
  - Controladores: Nombre singular + `Controller` (ej: `CursoController`)
  - Repositorios JPA: Nombre singular + `JpaRepository` (ej: `CursoJpaRepository`)
  - Mappers: Nombre singular + `Mapper` (ej: `CursoMapper`)
  - Adapters: Nombre singular + `Adapter` (ej: `CursoJpaAdapter`)
- **Características:**
  - Implementan puertos
  - Pueden usar anotaciones Spring (`@Service`, `@Repository`, `@RestController`)
  - Manejan conversiones entre capas

### Casos de Uso
- **Ubicación:** `application/service/`
- **Nombre:** Nombre singular + `Service` (ej: `CursoService`)
- **Características:**
  - Implementan puertos de entrada (`application/port/in/`)
  - Orquestan lógica usando servicios de dominio y puertos de salida
  - No contienen lógica de negocio pura (eso va en `domain/service/`)

---

## 🔄 Patrones de Migración

### Patrón 1: Separación Modelo/Entidad

**Antes:**
```java
// model/Curso.java
@Entity
@Table(name = "cursos")
public class Curso {
    @Id @GeneratedValue
    private Long id;
    private String titulo;
    // ... lógica de negocio mezclada con JPA
}
```

**Después:**
```java
// domain/modelo/Curso.java
public class Curso {
    private Long id;
    private String titulo;
    // ... lógica de negocio pura
    
    public boolean esValido() {
        return titulo != null && !titulo.isBlank();
    }
}

// infrastructure/persistence/entity/CursoEntity.java
@Entity
@Table(name = "cursos")
public class CursoEntity {
    @Id @GeneratedValue
    private Long id;
    private String titulo;
    // ... solo anotaciones JPA y campos técnicos
}

// infrastructure/persistence/mapper/CursoMapper.java
public class CursoMapper implements BaseMapper<Curso, CursoEntity> {
    @Override
    public Curso toDomain(CursoEntity entity) {
        Curso curso = new Curso();
        curso.setId(entity.getId());
        curso.setTitulo(entity.getTitulo());
        return curso;
    }
    
    @Override
    public CursoEntity toEntity(Curso domain) {
        CursoEntity entity = new CursoEntity();
        entity.setId(domain.getId());
        entity.setTitulo(domain.getTitulo());
        return entity;
    }
}
```

### Patrón 2: Puertos y Adaptadores JPA

**Antes:**
```java
// repository/CursoRepository.java
public interface CursoRepository extends JpaRepository<Curso, Long> {
    List<Curso> findByCategoriaId(Long categoriaId);
}

// service/CursoService.java
@Service
public class CursoService {
    @Autowired
    private CursoRepository repository;
    
    public List<Curso> findByCategoria(Long categoriaId) {
        return repository.findByCategoriaId(categoriaId);
    }
}
```

**Después:**
```java
// domain/repository/CursoRepository.java
public interface CursoRepository {
    Optional<Curso> findById(Long id);
    List<Curso> findByCategoriaId(Long categoriaId);
    Curso save(Curso curso);
}

// infrastructure/persistence/repository/CursoJpaRepository.java
public interface CursoJpaRepository extends JpaRepository<CursoEntity, Long> {
    List<CursoEntity> findByCategoriaId(Long categoriaId);
}

// infrastructure/persistence/CursoJpaAdapter.java
@Service
@RequiredArgsConstructor
public class CursoJpaAdapter implements CursoRepository {
    private final CursoJpaRepository jpaRepository;
    private final CursoMapper mapper;
    
    @Override
    public Optional<Curso> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
    
    @Override
    public List<Curso> findByCategoriaId(Long categoriaId) {
        return jpaRepository.findByCategoriaId(categoriaId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public Curso save(Curso curso) {
        CursoEntity entity = mapper.toEntity(curso);
        CursoEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
```

### Patrón 3: Casos de Uso

**Antes:**
```java
// controller/CursoController.java
@RestController
@RequestMapping("/api/cursos")
public class CursoController {
    @Autowired
    private CursoService service;
    
    @GetMapping("/categoria/{id}")
    public List<CursoResponse> findByCategoria(@PathVariable Long id) {
        return service.findByCategoria(id).stream()
                .map(this::toResponse)
                .toList();
    }
}

// service/CursoService.java
@Service
public class CursoService {
    @Autowired
    private CursoRepository repository;
    
    public List<Curso> findByCategoria(Long id) {
        return repository.findByCategoriaId(id);
    }
}
```

**Después:**
```java
// application/port/in/CursoUseCase.java
public interface CursoUseCase {
    List<Curso> findByCategoria(Long categoriaId);
}

// application/port/out/CursoPort.java
public interface CursoPort {
    List<Curso> findByCategoriaId(Long categoriaId);
}

// application/service/CursoService.java
@Service
@RequiredArgsConstructor
public class CursoService implements CursoUseCase {
    private final CursoPort cursoPort;
    
    @Override
    public List<Curso> findByCategoria(Long categoriaId) {
        // Validación de aplicación
        if (categoriaId == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }
        return cursoPort.findByCategoriaId(categoriaId);
    }
}

// infrastructure/web/controller/CursoController.java
@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {
    private final CursoUseCase cursoUseCase;
    private final CursoResponseMapper mapper;
    
    @GetMapping("/categoria/{id}")
    public List<CursoResponse> findByCategoria(@PathVariable Long id) {
        return cursoUseCase.findByCategoria(id).stream()
                .map(mapper::toResponse)
                .toList();
    }
}

// infrastructure/web/mapper/CursoResponseMapper.java
@Component
public class CursoResponseMapper {
    public CursoResponse toResponse(Curso domain) {
        CursoResponse response = new CursoResponse();
        response.setId(domain.getId());
        response.setTitulo(domain.getTitulo());
        return response;
    }
}
```

---

## 🧪 TDD en la Migración

### para cada módulo:

1. **Escribir test para modelo de dominio**
   - Test de creación
   - Test de validaciones
   - Test de lógica de negocio

2. **Implementar modelo de dominio**

3. **Escribir test para servicio de dominio**
   - Test de reglas de negocio
   - Test de invariantes

4. **Implementar servicio de dominio**

5. **Escribir test para caso de uso**
   - Test de orquestación
   - Test de manejo de errores

6. **Implementar caso de uso**

7. **Escribir test para adaptador**
   - Test de mapeo
   - Test de integración con Spring

8. **Implementar adaptador**

9. **Verificar que todos los tests pasan**

10. **Eliminar código antiguo**

---

## 🔒 Dependencias entre Capas

```
DOMAIN (domain/)
    └── No depende de nada (puro Java)

APPLICATION (application/)
    ├── Depende de: DOMAIN
    └── No depende de: INFRASTRUCTURE

INFRASTRUCTURE (infrastructure/)
    ├── Depende de: DOMAIN + APPLICATION
    └── Puede usar: Spring, JPA, etc.
```

**Regla de oro:** Las dependencias solo fluyen **hacia adentro** (Domain ← Application ← Infrastructure)

---

## ✅ Checklist de Validación

Antes de considerar un módulo migrado:

- [ ] Todos los tests del módulo pasan
- [ ] El build completo compila sin errores
- [ ] SpotBugs no reporta nuevos problemas
- [ ] La API REST mantiene su contrato (mismos endpoints, mismos DTOs)
- [ ] No hay referencias al código antiguo
- [ ] El código nuevo sigue las convenciones de esta guía

---

## 📚 Recursos Adicionales

- [Design Document](design.md) - Decisiones técnicas detalladas
- [Tasks List](tasks.md) - Tareas de implementación
- [Proposal](proposal.md) - Motivación y alcance

---

**Última actualización:** 2026-11-27
**Versión:** 1.0
