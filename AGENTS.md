# Guía para Agentes AI

Este documento proporciona directrices para agentes de inteligencia artificial que trabajen en este repositorio.

## Descripción del Proyecto

Este es un proyecto backend desarrollado en Java con el framework Spring Boot. Utiliza Maven para la gestión de dependencias y el ciclo de vida de la compilación.

### Tecnologías Clave
- **Lenguaje:** Java 21
- **Framework:** Spring Boot 3.4.12
- **Gestor de dependencias:** Maven
- **Base de datos:** H2 (tests), MariaDB (producción)
- **Testing:** JUnit 5, Mockito, REST Assured (integración)
- **Herramientas:** vfox (gestión versiones Java), SpotBugs, JaCoCo

## Flujo de Trabajo para Contribuciones

Para asegurar la calidad y estabilidad del código, sigue estos pasos al realizar cambios:

1.  **Análisis y Desarrollo:**
    *   Comprende la tarea y el código existente antes de modificarlo.
    *   Aplica los cambios siguiendo las convenciones de código y patrones de diseño ya establecidos en el proyecto.

2.  **Ejecución de Tests:**
    *   Después de realizar cualquier modificación, es **mandatorio** ejecutar la suite completa de tests (unitarios e integración). Esto asegura que tus cambios no han roto ninguna funcionalidad existente (regresiones).
    *   **Tests completos (unitarios + integración):**
        ```bash
        ./mvnw -Pfailsafe verify
        ```
    *   **Solo tests unitarios (para desarrollo rápido):**
        ```bash
        ./mvnw test
        ```
    *   **Solo tests de integración:**
        ```bash
        ./mvnw -DskipUTs -Pfailsafe verify
        ```
    *   Cuando fallen los test y se este arreglando, centrarse primero en lanzar solo los que fallan en lugar de lanzar todos siempre:
        - Tests unitarios específicos: `./mvnw test -Dtest=NombreDelTest`
        - Tests de integración específicos: `./mvnw -Pfailsafe verify -Dit.test=NombreDelTestIT`
    *   Aplicar TDD a la hora de implementar
    *   Preferiblemente preparar los datos como carga inicial en BBDD en lugar de crear en los test

3.  **Verificación de Calidad del Código con SpotBugs:**
    *   Antes de considerar tu trabajo finalizado y listo para un commit, ejecuta un análisis estático de código con SpotBugs para detectar bugs potenciales y malas prácticas.
    *   Utiliza el siguiente comando para lanzar el análisis:
        ```bash
        ./mvnw compile spotbugs:check
        ```

4.  **Creación de Commits:**
    *   Asegúrate de que todos los tests se ejecutan correctamente y que el análisis de SpotBugs no reporta problemas críticos.
    *   Escribe un mensaje de commit claro y descriptivo.

5.  **Subir cambios al repositorio remoto (git push):**
    *   Asegúrate de que todos los tests se ejecutan correctamente con el comando:
        ```bash
        ./mvnw clean verify -Pfailsafe
        ```
    *   Para mantener la calidad, revisar que se siguen las guías de SonarQube

El cumplimiento de estos pasos es fundamental para mantener la integridad del proyecto.
