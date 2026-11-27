# Tasks: Hexagonal Architecture Migration

## 0. Preparation and Setup

- [x] 0.1 Create new package structure (domain/, application/, infrastructure/) and verify all directories exist
- [x] 0.2 Create base domain exception class and verify it compiles
- [x] 0.3 Create base mapper interface/utility class and verify it compiles
- [x] 0.4 Update pom.xml if any new test dependencies needed and verify build succeeds
- [x] 0.5 Document migration patterns and conventions in a team guide

## 1. Categoria Module Migration (Week 1 - Pilot)

- [x] 1.1 Create domain model `Categoria` in domain/modelo/ and verify unit tests pass
- [x] 1.2 Create domain repository port interface in domain/repository/ and verify it compiles
- [x] 1.3 Create JPA entity `CategoriaEntity` in infrastructure/persistence/entity/ and verify JPA mapping
- [x] 1.4 Create JPA mapper for Categoria and verify mapping works both directions
- [x] 1.5 Create JPA adapter implementing domain repository port and verify integration with Spring Data
- [x] 1.6 Create application port interfaces for Categoria use cases in application/port/in/ and application/port/out/
- [ ] 1.7 Create use case implementation in application/service/ and verify business logic with unit tests (TDD)
- [x] 1.8 Update REST controller to use new structure and verify all Categoria endpoints still work
- [ ] 1.9 Remove old Categoria code from model/ and controller/ and verify build succeeds
- [x] 1.10 Run full test suite and verify all tests pass
- [x] 1.11 Manual API testing for Categoria endpoints and verify responses match old behavior

## 2. Contenido Module Migration (Week 2)

- [ ] 2.1 Create domain model `Contenido` in domain/modelo/ and verify unit tests pass
- [ ] 2.2 Create domain repository port interface in domain/repository/ and verify it compiles
- [ ] 2.3 Create JPA entity `ContenidoEntity` with relationship to CursoEntity and verify JPA mapping
- [ ] 2.4 Create JPA mapper for Contenido and verify mapping works both directions
- [ ] 2.5 Create JPA adapter implementing domain repository port and verify integration
- [ ] 2.6 Create application port interfaces for Contenido use cases and verify they compile
- [ ] 2.7 Create use case implementation and verify business logic with unit tests (TDD)
- [ ] 2.8 Update or create REST endpoints for Contenido (if any) and verify they work
- [ ] 2.9 Update Curso domain model to reference Contenido correctly and verify relationships
- [ ] 2.10 Remove old Contenido code and verify build succeeds
- [ ] 2.11 Run full test suite and verify all tests pass

## 3. Valoracion Module Migration (Week 2)

- [ ] 3.1 Create domain model `Valoracion` in domain/modelo/ and verify unit tests pass
- [ ] 3.2 Create domain repository port interface in domain/repository/ and verify it compiles
- [ ] 3.3 Create JPA entity `ValoracionEntity` with relationships to CursoEntity and UsuarioEntity and verify JPA mapping
- [ ] 3.4 Create JPA mapper for Valoracion and verify mapping works both directions
- [ ] 3.5 Create JPA adapter implementing domain repository port and verify integration
- [ ] 3.6 Create application port interfaces for Valoracion use cases and verify they compile
- [ ] 3.7 Create use case implementation and verify business logic with unit tests (TDD)
- [ ] 3.8 Update REST endpoints for Valoracion and verify they work
- [ ] 3.9 Remove old Valoracion code and verify build succeeds
- [ ] 3.10 Run full test suite and verify all tests pass

## 4. Curso Module Migration (Week 3)

- [ ] 4.1 Create domain model `Curso` in domain/modelo/ with all business logic and verify unit tests pass
- [ ] 4.2 Create domain repository port interface in domain/repository/ and verify it compiles
- [ ] 4.3 Create JPA entity `CursoEntity` with relationships to CategoriaEntity, InstructorEntity, and Set<ContenidoEntity> and verify JPA mapping
- [ ] 4.4 Create JPA mapper for Curso and verify mapping handles all relationships correctly
- [ ] 4.5 Create JPA adapter implementing domain repository port and verify integration with Spring Data
- [ ] 4.6 Create application port interfaces for Curso use cases in application/port/in/ and application/port/out/
- [ ] 4.7 Create use case implementation in application/service/ and verify business logic with unit tests (TDD)
- [ ] 4.8 Update REST controller to use new Curso use cases and verify all endpoints work
- [ ] 4.9 Update Curso endpoints to use new structure while maintaining API contract and verify responses
- [ ] 4.10 Remove old Curso code from model/ and service/ and verify build succeeds
- [ ] 4.11 Run full test suite and verify all Curso-related tests pass
- [ ] 4.12 Manual API testing for all Curso endpoints and verify behavior matches old implementation

## 5. Avance Module Migration (Week 3)

- [ ] 5.1 Create domain model `Avance` in domain/modelo/ and verify unit tests pass
- [ ] 5.2 Create domain repository port interface in domain/repository/ and verify it compiles
- [ ] 5.3 Create JPA entity `AvanceEntity` with relationships to UsuarioEntity and CursoEntity and verify JPA mapping
- [ ] 5.4 Create JPA mapper for Avance and verify mapping works both directions
- [ ] 5.5 Create JPA adapter implementing domain repository port and verify integration
- [ ] 5.6 Create application port interfaces for Avance use cases and verify they compile
- [ ] 5.7 Create use case implementation and verify business logic with unit tests (TDD)
- [ ] 5.8 Update REST endpoints for Avance (if any) and verify they work
- [ ] 5.9 Remove old Avance code and verify build succeeds
- [ ] 5.10 Run full test suite and verify all tests pass

## 6. Instructor Module Migration (Week 3-4)

- [ ] 6.1 Create domain model `Instructor` in domain/modelo/ and verify unit tests pass
- [ ] 6.2 Create domain repository port interface in domain/repository/ and verify it compiles
- [ ] 6.3 Create JPA entity `InstructorEntity` and verify JPA mapping
- [ ] 6.4 Create JPA mapper for Instructor and verify mapping works both directions
- [ ] 6.5 Create JPA adapter implementing domain repository port and verify integration
- [ ] 6.6 Update Curso domain model to reference Instructor correctly and verify relationships
- [ ] 6.7 Remove old Instructor code and verify build succeeds
- [ ] 6.8 Run full test suite and verify all tests pass

## 7. Usuario Module Migration (Week 4)

- [ ] 7.1 Create domain model `Usuario` in domain/modelo/ with all business logic and verify unit tests pass
- [ ] 7.2 Create domain repository port interface in domain/repository/ and verify it compiles
- [ ] 7.3 Create JPA entity `UsuarioEntity` with relationships to CursoEntity and AvanceEntity and verify JPA mapping
- [ ] 7.4 Create JPA mapper for Usuario and verify mapping handles all relationships correctly
- [ ] 7.5 Create JPA adapter implementing domain repository port and verify integration with Spring Data
- [ ] 7.6 Create application port interfaces for Usuario use cases and verify they compile
- [ ] 7.7 Create use case implementation in application/service/ and verify business logic with unit tests (TDD)
- [ ] 7.8 Update Usuario REST controller to use new structure while maintaining API contract and verify endpoints work
- [ ] 7.9 Update UsuarioController endpoints (registro, cursos comprados) to use new use cases
- [ ] 7.10 Verify anti-IDOR protection still works in new structure
- [ ] 7.11 Remove old Usuario code from model/ and service/ (keep security-related code for now) and verify build succeeds
- [ ] 7.12 Run full test suite and verify all Usuario-related tests pass
- [ ] 7.13 Manual API testing for Usuario endpoints and verify behavior matches old implementation

## 8. Authentication and Security Migration (Week 5)

- [ ] 8.1 Create domain model for authentication tokens and verify unit tests pass
- [ ] 8.2 Create application port interfaces for authentication use cases and verify they compile
- [ ] 8.3 Create authentication use case implementation and verify with unit tests (TDD)
- [ ] 8.4 Update JWT authentication filter to use new structure and verify security still works
- [ ] 8.5 Update LoginController to use new authentication use cases and verify login/logout work
- [ ] 8.6 Update TokenService to work with new structure and verify token generation/validation
- [ ] 8.7 Update LoginRateLimiter to work with new structure and verify rate limiting still works
- [ ] 8.8 Update WebSecurityConfig to reference new service structure and verify security configuration
- [ ] 8.9 Verify all security tests pass
- [ ] 8.10 Manual testing of authentication flow and verify it works as before

## 9. Cleanup and Finalization

- [ ] 9.1 Remove all deprecated code and old package structures (controller/, service/, repository/, model/) and verify build succeeds
- [ ] 9.2 Update all import statements across the codebase to use new package structure and verify no broken imports
- [ ] 9.3 Update all test files to use new package structure and verify all tests pass
- [ ] 9.4 Update Swagger/OpenAPI documentation if needed and verify API docs are correct
- [ ] 9.5 Update README.md with new architecture documentation
- [ ] 9.6 Run SpotBugs and verify no new issues introduced
- [ ] 9.7 Run full integration test suite and verify all tests pass
- [ ] 9.8 Manual smoke testing of all API endpoints and verify all functionality works
- [ ] 9.9 Performance testing to verify no significant degradation

## 10. Verification and Documentation

- [ ] 10.1 Create architecture diagram showing new structure and add to docs/
- [ ] 10.2 Update project STRUCTURE.md with new architecture
- [ ] 10.3 Document migration patterns and lessons learned
- [ ] 10.4 Final code review and approval
- [ ] 10.5 Create Git tag for architecture migration completion

## Workflow follow-up

- Archive the change after the project's review requirements are satisfied
- Verify the archived result
- Consider creating a presentation for the team on the new architecture
