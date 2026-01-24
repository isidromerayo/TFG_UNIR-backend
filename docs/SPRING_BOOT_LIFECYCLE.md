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

### Spring Boot 4.0 (Versión Actual) ✅

Esta es la versión actualmente utilizada en el proyecto (`4.0.2`).

*   **Fecha de Lanzamiento:** Enero de 2026.
*   **Fin de Soporte Open Source (OSS):** ~Enero 2027.
*   **Soporte Extendido (Comercial):** TBD.
    *   Salto a la nueva generación de Spring Boot (4.x).
*   **Ventajas de la Actualización:**
    *   ✅ Alineación con Spring Framework 7.0.3.
    *   ✅ Hibernate 7.2.1.Final con soporte para Jakarta EE 11.
    *   ✅ Spring Security 7.0.2.
    *   ✅ Soporte completo para Groovy 5 (vía RestAssured 6.0.0).

## Resumen de Fechas Clave

| Versión | Lanzamiento | Fin Soporte OSS | Fin Soporte Comercial | Estado |
| :--- | :--- | :--- | :--- | :--- |
| **3.4** | Nov 2024 | ~Nov 2025 | TBD | ❌ Fuera de soporte |
| **3.5** | Mayo 2025 | Junio 2026 | Junio 2032 | ❌ Anterior |
| **4.0** | Enero 2026 | Enero 2027 | TBD | ✅ Actual |

## Cambios en la Migración a 4.0.2

### Dependencias Actualizadas
- Spring Boot: 3.5.9 → **4.0.2** ✅
- Spring Framework: 6.2.x → **7.0.3** ✅
- Hibernate: 6.6.x → **7.2.1.Final** ✅
- Spring Security: 6.5.x → **7.0.2** ✅
- SpringDoc OpenAPI: 2.8.5 → **3.0.1** ✅
- RestAssured: 5.5.0 → **6.0.0** ✅ (Corrección Groovy 5)
- MariaDB Driver: 3.5.6 → **3.5.7** ✅

### Cambios de Configuración
- ✅ Eliminada propiedad `spring.jpa.properties.hibernate.dialect` (Hibernate 6.6+ auto-detecta)
- ✅ Verificada compatibilidad de todas las dependencias transitivas
- ✅ Todos los tests pasando (22/22)
- ✅ Cobertura de código mantenida en 99%

### Documentación
- ✅ Creado documento de migración: `RESUMEN_MIGRACION_v4.md`
- ✅ Actualizada documentación del proyecto
- ✅ Verificadas imágenes Docker actualizadas (Java 17 JRE)

## Recomendación

El proyecto ha sido **exitosamente migrado a Spring Boot 4.0.2** (Enero 2026), asegurando:
- ✅ Recepción de parches de seguridad y alineación con la última generación del framework.
- ✅ Compatibilidad con Jakarta EE 11.
- ✅ Estabilidad del stack tecnológico para el desarrollo futuro.
