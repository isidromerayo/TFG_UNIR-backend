# Guía para Agentes AI

Este documento proporciona directrices para agentes de inteligencia artificial que trabajen en este repositorio.

## Descripción del Proyecto

Este es un proyecto backend desarrollado en Java con el framework Spring Boot. Utiliza Maven para la gestión de dependencias y el ciclo de vida de la compilación.

### Tecnologías Clave
- **Lenguaje:** Java
- **Framework:** Spring Boot
- **Gestor de dependencias:** Maven
- **Base de datos:** (Especificar si se conoce, ej. H2, PostgreSQL)
- **Testing:** JUnit, Mockito

## Flujo de Trabajo para Contribuciones

Para asegurar la calidad y estabilidad del código, sigue estos pasos al realizar cambios:

1.  **Análisis y Desarrollo:**
    *   Comprende la tarea y el código existente antes de modificarlo.
    *   Aplica los cambios siguiendo las convenciones de código y patrones de diseño ya establecidos en el proyecto.

2.  **Ejecución de Tests Unitarios:**
    *   Después de realizar cualquier modificación, es **mandatorio** ejecutar la suite completa de tests unitarios. Esto asegura que tus cambios no han roto ninguna funcionalidad existente (regresiones).
    *   Utiliza el Maven Wrapper (`mvnw`) para ejecutar los tests:
        ```bash
        ./mvnw test
        ```
    *   Cuando fallen los test y se este arreglando, centrarse primero en lanzar solo los que fallan en lugar de lanzar todos siempre
    *   Aplicar TDD a la hora de implentar
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

El cumplimiento de estos pasos es fundamental para mantener la integridad del proyecto.
