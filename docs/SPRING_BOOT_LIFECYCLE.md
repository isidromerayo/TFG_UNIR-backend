# Ciclo de Vida de Spring Boot

Este documento detalla el ciclo de vida, soporte y fechas relevantes para las versiones de Spring Boot utilizadas en el proyecto y las versiones objetivo para futuras actualizaciones.

## Versiones Relevantes

### Spring Boot 3.4 (Versión Anterior)

Esta fue la versión utilizada anteriormente en el proyecto (`3.4.12`).

*   **Fecha de Lanzamiento:** 21 de Noviembre de 2024.
*   **Fin de Soporte Open Source (OSS):** ~Noviembre 2025.
    *   Las versiones menores de Spring Boot (ej. 3.4.x) típicamente tienen un soporte de código abierto de aproximadamente 12 meses.
    *   A fecha de Enero 2026, esta versión ha superado su periodo estándar de soporte gratuito.
*   **Estado en el Proyecto:** ✅ Migrado a Spring Boot 3.5.9 (Enero 2026).

### Spring Boot 3.5 (Versión Actual) ✅

Esta es la versión actualmente utilizada en el proyecto (`3.5.9`).

*   **Fecha de Lanzamiento:** 22 de Mayo de 2025.
*   **Fin de Soporte Open Source (OSS):** 30 de Junio de 2026.
*   **Soporte Extendido (Comercial):** Hasta el 30 de Junio de 2032.
    *   Al ser la última versión menor de la generación 3.x, Spring Boot 3.5 goza de un periodo de soporte extendido significativamente más largo (similar a las versiones LTS).
*   **Ventajas de la Actualización:**
    *   ✅ Recuperar el soporte activo de la comunidad y actualizaciones de seguridad gratuitas.
    *   ✅ Alineación con Spring Framework 6.2.15.
    *   ✅ Estabilidad a largo plazo (LTS) si se opta por soporte comercial.
    *   ✅ Hibernate 6.6.39 con validaciones más estrictas.
    *   ✅ Detección automática de dialecto de BD (sin necesidad de configuración explícita).

## Resumen de Fechas Clave

| Versión | Lanzamiento | Fin Soporte OSS | Fin Soporte Comercial | Estado |
| :--- | :--- | :--- | :--- | :--- |
| **3.4** | Nov 2024 | ~Nov 2025 | TBD | ❌ Fuera de soporte |
| **3.5** | Mayo 2025 | Junio 2026 | Junio 2032 | ✅ Actual |

## Cambios en la Migración a 3.5.9

### Dependencias Actualizadas
- Spring Boot: 3.4.12 → **3.5.9** ✅
- Spring Framework: 6.1.x → **6.2.15** ✅
- Hibernate: 6.5.x → **6.6.39** ✅
- Spring Security: 6.4.x → **6.5.7** ✅
- SpringDoc OpenAPI: 2.5.0 → **2.8.5** ✅
- MariaDB Driver: 3.5.6 → **3.5.7** ✅

### Cambios de Configuración
- ✅ Eliminada propiedad `spring.jpa.properties.hibernate.dialect` (Hibernate 6.6+ auto-detecta)
- ✅ Verificada compatibilidad de todas las dependencias transitivas
- ✅ Todos los tests pasando (15/15)
- ✅ Cobertura de código mantenida en 85%

### Documentación
- ✅ Creado documento de migración: `docs/SPRING_BOOT_3.5_MIGRATION.md`
- ✅ Actualizada documentación del proyecto
- ✅ Verificadas imágenes Docker publicadas

## Recomendación

El proyecto ha sido **exitosamente migrado a Spring Boot 3.5.9** (Enero 2026), asegurando:
- ✅ Recepción de parches de seguridad hasta Junio 2026 (OSS)
- ✅ Soporte extendido hasta Junio 2032 (comercial)
- ✅ Compatibilidad con el ecosistema Spring actual
- ✅ Mejoras de rendimiento y seguridad
