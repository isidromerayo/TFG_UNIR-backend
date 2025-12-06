---
inclusion: always
---

# Estándares de Calidad de Código

## 🎯 Métricas de Calidad Obligatorias

### Cobertura de Tests
- **Mínimo**: 80% de cobertura de líneas
- **Comando**: `./mvnw jacoco:report`
- **Ubicación**: `target/site/jacoco/index.html`

### Análisis Estático
- **SpotBugs**: 0 bugs críticos permitidos
- **Comando**: `./mvnw spotbugs:check`
- **Plugins activos**: FindSecBugs, fb-contrib

### Dependencias
- **OWASP**: Verificar vulnerabilidades conocidas
- **Comando**: `./mvnw -Pdependency-check verify`
- **Solo si tienes NVD_API_KEY configurada**

## 📝 Convenciones de Código

### Naming Conventions
- **Clases**: PascalCase (`UsuarioService`)
- **Métodos**: camelCase (`buscarUsuario`)
- **Constantes**: UPPER_SNAKE_CASE (`MAX_RETRY_ATTEMPTS`)
- **Packages**: lowercase (`eu.estilolibre.tfgunir.backend`)

### Estructura de Tests
```java
@Test
void deberiaHacerAlgo_cuandoCondicion_entoncesResultado() {
    // Given (Arrange)
    
    // When (Act)
    
    // Then (Assert)
}
```

### Commits
- Usar Conventional Commits
- Formato: `tipo(scope): descripción`
- Tipos: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

## 🚫 Anti-patrones a Evitar

- Tests que dependen del orden de ejecución
- Hardcodear valores en lugar de usar constantes
- Métodos con más de 20 líneas (considerar refactoring)
- Clases con más de 300 líneas
- Dependencias circulares entre packages