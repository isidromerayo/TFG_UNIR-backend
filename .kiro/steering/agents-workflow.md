---
inclusion: always
---

# Flujo de Trabajo Obligatorio para Agentes AI

Este documento establece las reglas **OBLIGATORIAS** que deben seguir todos los agentes AI al trabajar en este repositorio.

## ⚠️ REGLAS CRÍTICAS - NUNCA OMITIR

### 1. ANTES de cualquier commit:
```bash
# OBLIGATORIO: Ejecutar tests completos (unitarios + integración)
./mvnw -Pfailsafe verify

# OBLIGATORIO: Análisis de código
./mvnw compile spotbugs:check

# OBLIGATORIO: Verificación completa antes de push
./mvnw clean verify -Pfailsafe
```

### 2. Flujo de trabajo MANDATORIO:

1. **Análisis y Desarrollo**: Entender el código antes de modificar
2. **Verificar Java 21**: `java -version` - Si no es 21, ejecutar `vfox use java@21`
3. **Ejecutar Tests Completos**: `./mvnw -Pfailsafe verify` - Incluye unitarios + integración
4. **SpotBugs**: `./mvnw compile spotbugs:check` - Resolver problemas críticos
5. **Commit**: Solo después de que todo pase
6. **Verificación final**: `./mvnw clean verify -Pfailsafe` antes de push

### 2.1. Si hay fallos en tests:
- **Tests unitarios específicos**: `./mvnw test -Dtest=NombreDelTest`
- **Tests de integración específicos**: `./mvnw -Pfailsafe verify -Dit.test=NombreDelTestIT`
- **Solo tests unitarios (desarrollo rápido)**: `./mvnw test`
- **Solo tests de integración**: `./mvnw -DskipUTs -Pfailsafe verify`
- NO ejecutar toda la suite hasta que los fallos estén resueltos
- Aplicar TDD: escribir test → hacer que pase → refactorizar
- **CRÍTICO**: Si fallan tests de integración, revisar endpoints y configuración de seguridad

### 3. Principios de Testing:
- Aplicar TDD cuando sea posible
- Centrarse en tests que fallan antes de ejecutar toda la suite
- Preferir datos de carga inicial en BBDD vs crear en tests
- NUNCA hacer commit si los tests fallan

### 4. Tecnologías del proyecto:
- **Java 21** (usar vfox para gestión de versiones)
- **Spring Boot 3.4.12**
- **Maven** para gestión de dependencias
- **JUnit + Mockito** para testing
- **H2** para tests, **MariaDB** para producción

## 🚫 PROHIBIDO:
- Hacer commits sin ejecutar tests
- Ignorar fallos de SpotBugs críticos
- Subir código que no compile
- Omitir el flujo de verificación

## ✅ OBLIGATORIO:
- Seguir este flujo en CADA cambio
- Validar que Java 21 esté activo con vfox
- Ejecutar verificación completa antes de push
- Escribir mensajes de commit descriptivos siguiendo Conventional Commits

**Este flujo es OBLIGATORIO y NO OPCIONAL para mantener la calidad del código.**