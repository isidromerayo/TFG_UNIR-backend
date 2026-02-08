# Using Skills (AI Agents)

Este documento describe cómo funciona el sistema de *skills* versionadas en el repositorio para estandarizar el trabajo de agentes AI en el proyecto.

## Objetivo

- Mantener a todos los agentes AI alineados con el stack y convenciones del proyecto.
- Compartir patrones reutilizables (arquitectura, seguridad, TDD, etc.).
- Permitir que el equipo use distintos IDEs/herramientas sin perder consistencia.

## Estructura en el repositorio

- **Fuente canónica**: `.agents/skills/`
  - Aquí vive cada skill con su fichero `SKILL.md`.
- **Integraciones por herramienta** (symlinks que apuntan a `.agents/skills/`):
  - `.windsurf/skills/`
  - `.cursor/skills/`
  - `.kiro/skills/`
  - `.agent/skills/`

## Skills disponibles (stack Spring Boot)

- `springboot-patterns`
- `springboot-security`
- `springboot-tdd`

## Añadir / actualizar skills

Las skills se gestionan con el CLI `skills`.

```bash
# Añadir/actualizar una skill en el repo (scope: Project)
# Nota: revisa siempre los cambios antes de commitear.

npx skills add https://github.com/affaan-m/everything-claude-code --skill springboot-patterns
npx skills add https://github.com/affaan-m/everything-claude-code --skill springboot-security
npx skills add https://github.com/affaan-m/everything-claude-code --skill springboot-tdd
```

Qué esperar tras ejecutar el comando:

- Se crean/actualizan los ficheros bajo `.agents/skills/<skill>/SKILL.md`.
- Se crean/actualizan symlinks en `.windsurf/skills/`, `.cursor/skills/`, `.kiro/skills/` y `.agent/skills/`.

## Revisión y commit

Recomendación:

- Haz commit en `main` cuando el objetivo sea **estandarizar el equipo**.
- Usa un commit separado de cambios funcionales del backend.

Ejemplo:

```bash
git status

git add .agents/ .windsurf/ .cursor/ .kiro/ .agent/

git commit -m "feat: standardize AI skills for Spring Boot development"
```

## Seguridad

Las skills son contenido que guía al agente (texto/patrones), pero **tienen impacto real** porque pueden influir en decisiones de implementación.

- Revisa el contenido de `SKILL.md` antes de aceptarlo en el repositorio.
- No incluyas secretos ni ejemplos con credenciales reales.
