# Ciclo de Vida de Spring Boot

Este documento detalla el ciclo de vida, soporte y fechas relevantes para las versiones de Spring Boot utilizadas en el proyecto y las versiones objetivo para futuras actualizaciones.

## Versiones Relevantes

### Spring Boot 3.4 (Versión Anterior)

Esta fue la versión utilizada anteriormente en el proyecto (`3.4.12`).

*   **Fecha de Lanzamiento:** 21 de Noviembre de 2024.
*   **Fin de Soporte Open Source (OSS):** ~Noviembre 2025.
    *   Las versiones menores de Spring Boot (ej. 3.4.x) típicamente tienen un soporte de código abierto de aproximadamente 12 meses.
    *   A fecha de Enero 2026, esta versión ha superado su periodo estándar de soporte gratuito.
*   **Estado en el Proyecto:** ✅ Migrado a Spring Boot 3.5.10 (Febrero 2026).

### Spring Boot 3.5 (Versión Anterior)

Esta fue la versión utilizada en el proyecto hasta septiembre de 2026 (`3.5.16`).

*   **Fecha de Lanzamiento:** 22 de Mayo de 2025.
*   **Fin de Soporte Open Source (OSS):** 30 de Junio de 2026.
*   **Soporte Extendido (Comercial):** Hasta el 30 de Junio de 2032.
    *   Al ser la última versión menor de la generación 3.x, Spring Boot 3.5 goza de un periodo de soporte extendido significativamente más largo (similar a las versiones LTS).
*   **Estado en el Proyecto:** ✅ Migrado a Spring Boot 4.0.8 (Septiembre 2026).

### Spring Boot 4.0 (Versión Actual) ✅

Esta es la versión actualmente utilizada en el proyecto (`4.0.8`).

*   **Fecha de Lanzamiento:** 30 de Noviembre de 2025.
*   **Fin de Soporte Open Source (OSS):** 31 de Diciembre de 2026.
*   **Soporte Extendido (Comercial):** Hasta el 31 de Diciembre de 2027.
*   **Ventajas de la Actualización:**
    *   ✅ Recuperar el soporte activo de la comunidad y actualizaciones de seguridad gratuitas (3.5 estaba EOL OSS).
    *   ✅ Remediar ~70 CVEs de librerías (Tomcat 11.0.25, Spring Framework 7.0.9, Spring Security 7.0.7, etc.).
    *   ✅ Spring Framework 7, Spring Security 7, Hibernate 7, Tomcat 11.
    *   ✅ Jackson 3 (`tools.jackson`) como stack JSON principal.

### Spring Boot 4.1 (Versión Objetivo) ⏳

*   **Fecha de Lanzamiento:** 30 de Junio de 2026.
*   **Fin de Soporte Open Source (OSS):** 31 de Julio de 2027.
*   **Soporte Extendido (Comercial):** Hasta el 31 de Julio de 2028.
*   **Estado en el Proyecto:** ⏳ Pendiente — bump planificado **antes del 31/12/2026** (fin de soporte OSS de la línea 4.0). Registrado en `docs/migration/SPRING_BOOT_4_MIGRATION.md` y en AGENTS.md.

## Resumen de Fechas Clave

| Versión | Lanzamiento | Fin Soporte OSS | Fin Soporte Comercial | Estado |
| :--- | :--- | :--- | :--- | :--- |
| **3.4** | Nov 2024 | ~Nov 2025 | TBD | ❌ Fuera de soporte |
| **3.5** | Mayo 2025 | Junio 2026 | Junio 2032 | ❌ EOL OSS (migrado) |
| **4.0** | Nov 2025 | Diciembre 2026 | Diciembre 2027 | ✅ Actual |
| **4.1** | Junio 2026 | Julio 2027 | Julio 2028 | ⏳ Objetivo |

## Cambios en la Migración a 4.0.8

Registro completo en `docs/migration/SPRING_BOOT_4_MIGRATION.md` (septiembre 2026):

### Dependencias Actualizadas
- Spring Boot: 3.5.16 → **4.0.8** ✅
- Spring Framework: 6.2.19 → **7.0.9** ✅
- Hibernate: 6.6.53 → **7.2.24.Final** ✅
- Spring Security: 6.5.11 → **7.0.7** ✅
- SpringDoc OpenAPI: 2.8.17 → **3.1.1** ✅
- Tomcat: 10.1.55 → **11.0.25** (override) ✅
- rest-assured: gestionado por Boot → **BOM propio 6.0.1** ✅
- Jackson: 2.x → **3.x (`tools.jackson`)** ✅

## Histórico: Migración a 3.5.10 (Febrero 2026)

### Dependencias Actualizadas
- Spring Boot: 3.4.12 → 3.5.10 ✅
- Spring Framework: 6.1.x → 6.2.15 ✅
- Hibernate: 6.5.x → 6.6.41 ✅
- Spring Security: 6.4.x → 6.5.7 ✅
- SpringDoc OpenAPI: 2.5.0 → 2.8.5 ✅
- MariaDB Driver: 3.5.6 → 3.5.7 ✅

### Cambios de Configuración
- ✅ Eliminada propiedad `spring.jpa.properties.hibernate.dialect` (Hibernate 6.6+ auto-detecta)
- ✅ Verificada compatibilidad de todas las dependencias transitivas
- ✅ Todos los tests pasando (15/15)
- ✅ Cobertura de código mantenida en 85%

## Recomendación

El proyecto ha sido **exitosamente migrado a Spring Boot 4.0.8** (Septiembre 2026), asegurando:
- ✅ Recepción de parches de seguridad hasta Diciembre 2026 (OSS)
- ✅ Soporte extendido hasta Diciembre 2027 (comercial)
- ✅ Compatibilidad con el ecosistema Spring actual (Framework 7, Security 7)
- ✅ ~70 CVEs de librerías remediados (ver informe de vulnerabilidades)
- ⏳ Próximo paso: bump a 4.1.x antes del 31/12/2026
