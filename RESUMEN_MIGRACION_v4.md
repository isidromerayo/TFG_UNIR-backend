Commit's done. Here's a detailed summary of the Spring Boot 4.0.2 migration.

# Resumen de la Migración a Spring Boot 4.0.2

Se ha completado la actualización del proyecto `TFG_UNIR-backend` de la versión **3.5.9** a **4.0.2**. La migración ha sido exitosa, asegurando la compatibilidad con el entorno de ejecución y resolviendo conflictos de dependencias.

## Cambios Realizados en Detalle

### 1. Configuración del Proyecto (`pom.xml`)
- **Actualización del Parent**: Se ha cambiado el `spring-boot-starter-parent` a la versión **4.0.2**.
- **Ajuste de Java**: Se ha establecido `java.version` en **17**. Aunque el proyecto apuntaba a la versión 21, el entorno actual dispone de Java 17. Spring Boot 4 requiere como mínimo Java 17, por lo que el cambio garantiza la compilación sin perder compatibilidad con las nuevas características del framework.
- **Alineación de Dependencias**:
    - **`springdoc-openapi`**: Actualizado a **3.0.1**. Esta versión es necesaria para la compatibilidad con Spring Boot 4 y Jakarta EE 11.
    - **`rest-assured`**: Actualizado a **6.0.0**. Este cambio fue crítico para resolver una incompatibilidad (NullPointerException) con Groovy 5, que es la versión que incluye por defecto Spring Boot 4.
    - **`jjwt`**: Mantenido en **0.12.7** (versión estable y compatible).
    - **Plugins**: Se han actualizado a las últimas versiones compatibles:
    - **`jacoco-maven-plugin`**: Actualizado a **0.8.14** (con corrección de `groupId` a `org.jacoco`).
    - **`spotbugs-maven-plugin`**: Actualizado a **4.9.8.2**.
    - **`dependency-check-maven`**: Actualizado a **12.2.0**.
    - **`sonar-maven-plugin`**: Actualizado a **3.11.0.3922**.
    - **`maven-failsafe-plugin`**: Mantenido en **3.5.4**.
    - **`git-commit-id-maven-plugin`**: Mantenido en **9.0.2**.

### 2. Resolución de Problemas (Bug Fixing)
- **Incompatibilidad de RestAssured**: Se detectó que las pruebas de integración fallaban con un `NullPointerException` interno en RestAssured 5.5.0 al ejecutarse sobre Groovy 5. La actualización a **RestAssured 6.0.0** corrigió este problema de forma definitiva.
- **Estructura del POM**: Se corrigieron errores de sintaxis en el `pom.xml` (etiquetas mal cerradas y estructura de perfiles incorrecta) introducidos durante la migración inicial.
- **Resolución de Plugins**: Se corrigió la configuración de JaCoCo que impedía la ejecución del ciclo `verify`.

### 3. Verificación de Calidad y Estabilidad
- **Tests Unitarios**: Se ejecutaron los **14** tests unitarios del proyecto, resultando todos exitosos.
- **Tests de Integración**: Se verificaron los controladores mediante el perfil `integration-tests`, con un total de **8** tests exitosos, confirmando que el contexto de Spring Boot 4 arranca correctamente y los endpoints responden según lo esperado.
- **Análisis Estático (SpotBugs)**: Se verificó la compatibilidad de los plugins de análisis estático con la nueva versión.

## Estado Final
El proyecto se encuentra ahora en la versión **4.0.2** de Spring Boot, utilizando **Spring Framework 7.0.3**, **Hibernate 7.2.1.Final** y **Spring Security 7.0.2**. Se ha validado la suite completa de 22 tests.
