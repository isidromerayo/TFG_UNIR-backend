---
inclusion: always
---

# Directrices del Proyecto

## Referencia a Guías Existentes

Este proyecto tiene documentación específica para agentes AI en:

#[[file:AGENTS.md]]

**IMPORTANTE**: Las directrices del archivo AGENTS.md son OBLIGATORIAS y deben seguirse en todo momento.

## Configuración del Entorno

- Usar **vfox** para gestión de versiones de Java
- Activar Java 21: `vfox use java@21`
- Verificar versión: `java -version`

## Stack Tecnológico Actual

- **Java 21** (migrado desde Java 17)
- **Spring Boot 3.4.12**
- **Maven** como build tool
- **JUnit 5** + **Mockito** para testing
- **H2** para tests, **MariaDB** para producción
- **JWT** para autenticación
- **Docker** para containerización

## Flujo de Desarrollo

Siempre seguir el flujo definido en AGENTS.md:
1. Verificar Java 21 activo (`java -version`)
2. Análisis y desarrollo
3. Tests completos (`./mvnw -Pfailsafe verify`) - incluye unitarios + integración
4. Análisis estático (`./mvnw compile spotbugs:check`)
5. Commit con mensaje descriptivo (Conventional Commits)
6. Verificación final (`./mvnw clean verify -Pfailsafe`) antes de push

## 📊 Archivos de Steering Disponibles

- `agents-workflow.md` - Flujo obligatorio para agentes AI
- `code-quality.md` - Estándares de calidad y métricas
- `environment-setup.md` - Configuración del entorno de desarrollo
- `security-guidelines.md` - Directrices de seguridad críticas
- `project-guidelines.md` - Este archivo (resumen general)