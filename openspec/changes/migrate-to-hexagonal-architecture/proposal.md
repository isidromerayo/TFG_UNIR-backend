# Proposal: Migrate to Hexagonal Architecture

## Why

The current layered architecture (controller/service/repository) tightly couples business logic to Spring Boot and JPA implementation details. This makes the codebase harder to test in isolation, more difficult to maintain, and less adaptable to future changes (e.g., switching databases, adding new interfaces like gRPC or GraphQL).

Hexagonal Architecture (Ports & Adapters) will:
- **Decouple** the domain logic from infrastructure concerns (Spring, JPA, REST)
- **Improve testability** by allowing pure unit tests of domain logic without Spring context
- **Enhance maintainability** with clear boundaries and single responsibility per component
- **Future-proof** the application for new interfaces and data sources

The migration aligns with the project's TDD mandate by enabling more focused, faster unit tests for domain logic.

## What Changes

### Architectural Structure
- **New**: `domain/` package with core business logic (entities, value objects, domain services, domain exceptions)
- **New**: `application/` package with use cases/ports (application services, DTOs, use case orchestration)
- **New**: `infrastructure/` package with adapters (Spring REST controllers, JPA repositories, JWT security, etc.)
- **Modify**: Reorganize existing code into these packages maintaining current functionality
- **Modify**: Update imports and dependencies to respect hexagonal boundaries

### Key Refactorings
- Extract domain models from JPA entities (separate domain model from persistence model)
- Create application ports (interfaces) for driven (outgoing) and driving (incoming) adapters
- Implement adapters that connect ports to Spring/JPA infrastructure
- Maintain existing REST API contracts (no breaking changes for clients)
- Preserve all existing database tables and relationships

### Constraints
- **NO BREAKING CHANGES**: REST API endpoints, request/response formats remain unchanged
- **TDD Mandatory**: All new code must have failing tests before implementation
- **Incremental**: Migrate module by module, maintaining a working system at each step
- **Authentication Last**: JWT security layer migration deferred to final phase

### Modified Files (Examples)
- `src/main/java/eu/estilolibre/tfgunir/backend/model/*` → Split into domain models + JPA entities
- `src/main/java/eu/estilolibre/tfgunir/backend/service/*` → Move to application layer as use cases
- `src/main/java/eu/estilolibre/tfgunir/backend/controller/*` → Become driving adapters (REST)
- `src/main/java/eu/estilolibre/tfgunir/backend/repository/*` → Become driven adapters (JPA)

## Capabilities

### New Capabilities
This is a **pure architectural refactoring** with no changes to system behavior or API contracts. All existing capabilities remain unchanged.

### Modified Capabilities
None. No spec-level behavior changes.

## Impact

### Code Impact
- **All Java source files** will be reorganized into new package structure
- **Import statements** will be updated across the codebase
- **Test files** will need updates to reflect new structure (maintaining TDD)

### API Impact
- **None**: REST API contracts remain identical
- Existing clients will continue to work without modifications

### Build/Dependency Impact
- No new external dependencies required
- Existing dependencies (Spring Boot, JPA, JWT) remain unchanged

### Testing Impact
- **Positive**: Domain logic can be tested without Spring context, improving test speed and isolation
- **Requires**: Update of existing test files to new package structure
- **TDD**: All new code must follow project's TDD mandate

### Risk Assessment
- **Medium Risk**: Large code movement but with no functional changes
- **Mitigation**: Incremental migration with frequent verification, comprehensive test coverage

## Decision Rationale

Hexagonal Architecture was chosen over alternatives (Clean Architecture, Onion Architecture) because:
1. **Simplicity**: Clear separation between domain, application, and infrastructure
2. **Spring Compatibility**: Works naturally with Spring's dependency injection
3. **Testability**: Enables pure unit tests for domain layer
4. **Flexibility**: Easy to add new interfaces (GraphQL, CLI, messaging) without affecting domain

The incremental approach minimizes disruption and allows the team to validate each step before proceeding.
