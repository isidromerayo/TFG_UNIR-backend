# INFORME: Análisis de Skills - TFG_UNIR-backend vs Masterclass-04

**Fecha:** 29 de Marzo de 2026  
**Proyecto:** TFG_UNIR-multi  
**Componente:** TFG_UNIR-backend  
**Referencia:** masterclass-04-skills/CLAUDE.md

---

## Resumen Ejecutivo

Este informe analiza las skills existentes en el proyecto backend Spring Boot y las compara con el estándar definido en masterclass-04. El objetivo es identificar brechas y proponer un plan de mejora para alinear las prácticas de desarrollo con las convenciones de la industria.

**Resultado:** El proyecto cuenta con 3 skills funcionales (7/10 promedio) pero carece de guidelines estructurados, actions interactivas, tasks automatizadas y non-negotiable rules que propone el estándar masterclass-04.

---

## 1. INVENTARIO ACTUAL DE SKILLS

### 1.1 Skills Existentes

| Skill | Ubicación | Descripción | Líneas | Estado |
|-------|-----------|-------------|--------|--------|
| `springboot-tdd` | `.agents/skills/springboot-tdd/SKILL.md` | Unit tests, MockMvc, Testcontainers, JaCoCo | 157 | ✅ Activa |
| `springboot-security` | `.agents/skills/springboot-security/SKILL.md` | Auth, JWT, CSRF, headers, OWASP | 119 | ✅ Activa |
| `springboot-patterns` | `.agents/skills/springboot-patterns/SKILL.md` | REST, repository, service layer, DTOs, exceptions | 304 | ✅ Activa |
| `AGENTS.md` | `TFG_UNIR-backend/AGENTS.md` | Convenciones, comandos, testing, Reglas de Oro | 247 | ✅ Activo |
| `USING_SKILLS.md` | `docs/USING_SKILLS.md` | Guía de uso de skills | 67 | ✅ Documentado |

### 1.2 Estructura de Archivos

```
TFG_UNIR-backend/
├── AGENTS.md                          (247 líneas)
├── docs/
│   └── USING_SKILLS.md               (67 líneas)
└── .agents/
    └── skills/
        ├── springboot-tdd/
        │   └── SKILL.md              (157 líneas)
        ├── springboot-security/
        │   └── SKILL.md              (119 líneas)
        └── springboot-patterns/
            └── SKILL.md              (304 líneas)
```

### 1.3 Evaluación de Calidad

| Skill | Puntuación | Fortalezas | Debilidades |
|-------|-------------|------------|-------------|
| `springboot-tdd` | 7/10 | Ejemplos claros, herramientas correctas | Falta TPP, 5 pasos TDD, FIRST principles |
| `springboot-security` | 8/10 | Checklist completo, OWASP integrado | No hay integración con actions |
| `springboot-patterns` | 7/10 | Completo: caching, async, logging | No menciona arquitectura hexagonal |
| `AGENTS.md` | 6/10 | Comandos y convenciones claras | Falta workflow, actions, tasks, rules |

**Promedio Global: 7/10**

---

## 2. ESTRUCTURA ESTÁNDAR MASTERCLASS-04

### 2.1 Organización de Archivos

```
masterclass-04-skills/
├── CLAUDE.md                           # Instrucciones principales del agente
├── guidelines/                         # Conocimiento auto-cargado
│   ├── architecture-hexagonal.md       # Arquitectura hexagonal
│   ├── design-principles.md             # Naming, funciones, errores
│   ├── testing-standards.md            # FIRST, mocks, estructura
│   ├── frontend-patterns.md             # Componentes, hooks
│   ├── xp-tdd-practices.md              # Ciclo 5 pasos, TPP
│   └── git-strategy.md                  # Branching, commits
├── actions/                            # Prácticas interactivas
│   ├── action-plan.md                   # Planificación TDD
│   ├── action-tdd.md                    # Forzar ciclo TDD
│   └── action-refactor.md               # Guía de refactorización
└── tasks/                               # Revisiones automatizadas
    ├── task-validate.md                 # Validación completa
    ├── task-testing-review.md           # Calidad de tests
    ├── task-architecture-review.md      # Cumplimiento hexagonal
    ├── task-frontend-review.md           # Patrones frontend
    ├── task-ux-review.md                 # UX visual con Playwright
    └── task-qa.md                        # QA funcional con Playwright
```

### 2.2 Elementos Clave del Estándar

#### 2.2.1 Guidelines (Conocimiento Auto-cargado)

| Guideline | Descripción |
|-----------|-------------|
| `architecture-hexagonal` | Arquitectura hexagonal, vertical slicing, dependencias de capas |
| `design-principles` | Naming, diseño de funciones, manejo de errores |
| `testing-standards` | FIRST principles, estructura de tests, política de mocks |
| `xp-tdd-practices` | Ciclo TDD 5 pasos, TPP (Transformation Priority Premise) |
| `git-strategy` | Feature branching, conventional commits, disciplina TDD |

#### 2.2.2 Actions (Prácticas Interactivas)

| Action | Propósito |
|--------|-----------|
| `/action-plan` | Crear plan de desarrollo TDD para una feature |
| `/action-tdd` | Forzar ciclo TDD cuando se intenta saltar |
| `/action-refactor` | Refactorizar código, tests, renombrar, patrones frontend |

#### 2.2.3 Tasks (Revisiones Automatizadas)

| Task | Propósito |
|------|-----------|
| `/task-validate` | Validación completa: compile, lint, format, tests |
| `/task-testing-review` | Revisar calidad de tests y cobertura |
| `/task-architecture-review` | Revisar cumplimiento de arquitectura hexagonal |
| `/task-qa` | QA funcional contra especificación con Playwright |

#### 2.2.4 Non-Negotiable Rules

```markdown
- Never skip TDD. No production code without a failing test first.
- Never commit directly to `master`.
- Always respond in English in code. Comments and documentation in English.
```

---

## 3. ANÁLISIS DE BRECHAS (Gap Analysis)

### 3.1 Matriz de Cumplimiento

| Categoría | Masterclass-04 | TFG_UNIR-backend | Estado | Severidad |
|-----------|----------------|-------------------|--------|-----------|
| **Guidelines** | | | | |
| Arquitectura | Hexagonal | Layered | ⚠️ Diferente | Baja |
| Design Principles | ✅ Separado | ❌ Mezclado en AGENTS.md | ❌ Falta | Alta |
| Testing Standards | ✅ Separado | ⚠️ Parcial | ⚠️ Incompleto | Alta |
| TDD Practices | ✅ TPP, 5 pasos | ⚠️ Solo 4 pasos simples | ⚠️ Mejorable | Alta |
| Git Strategy | ✅ Separado | ❌ No existe | ❌ Falta | Media |
| **Actions** | | | | |
| `/action-tdd` | ✅ Existe | ❌ No existe | ❌ Falta | Alta |
| `/action-refactor` | ✅ Existe | ❌ No existe | ❌ Falta | Media |
| `/action-plan` | ✅ Existe | ❌ No existe | ❌ Falta | Media |
| **Tasks** | | | | |
| `/task-validate` | ✅ Existe | ❌ No existe | ❌ Falta | Alta |
| `/task-testing-review` | ✅ Existe | ❌ No existe | ❌ Falta | Alta |
| `/task-architecture-review` | ✅ Existe | ❌ No existe | ❌ Falta | Baja |
| `/task-qa` | ✅ Existe | ❌ No existe | ❌ Falta | Baja |
| **Non-Negotiable Rules** | | | | |
| Never skip TDD | ✅ | ⚠️ "Tests obligatorios" | ⚠️ Débil | Alta |
| Never commit to main | ✅ | ⚠️ "Crear rama nueva" | ⚠️ Débil | Media |
| English in code | ✅ | ❌ Español en docs/código | ⚠️ Inconsistente | Baja |
| **Testing Coverage** | | | | |
| FIRST principles | ✅ | ❌ No mencionado | ❌ Falta | Alta |
| Mocks policy | ✅ | ⚠️ Parcial | ⚠️ Mejorable | Media |

### 3.2 Resumen de Brechas

| Prioridad | Cantidad | Items |
|-----------|----------|-------|
| Alta | 7 | Design principles, testing standards, TDD practices, action-tdd, task-validate, task-testing-review, FIRST principles |
| Media | 5 | Git strategy, action-refactor, action-plan, non-negotiable rules (débiles), mocks policy |
| Baja | 4 | Arquitectura (diferente), task-architecture-review, task-qa, idioma |

---

## 4. RECOMENDACIONES DE MEJORA

### 4.1 Por Prioridad

#### PRIORIDAD ALTA (Impacto inmediato - comenzar aquí)

| # | Recomendación | Esfuerzo | Impacto | Dependencias |
|---|---------------|-----------|---------|--------------|
| 1 | Crear `guidelines/xp-tdd-practices.md` | Medio | Alto | Ninguna |
| 2 | Crear `guidelines/testing-standards.md` | Medio | Alto | #1 |
| 3 | Crear `actions/action-tdd.md` | Bajo | Alto | #1 |
| 4 | Crear `tasks/task-validate.md` | Bajo | Alto | Ninguna |
| 5 | Actualizar AGENTS.md con "Non-Negotiable Rules" | Bajo | Medio | #4 |

#### PRIORIDAD MEDIA (Mejora incremental)

| # | Recomendación | Esfuerzo | Impacto | Dependencias |
|---|---------------|-----------|---------|--------------|
| 6 | Crear `guidelines/design-principles.md` | Medio | Medio | Ninguna |
| 7 | Crear `guidelines/git-strategy.md` | Bajo | Medio | Ninguna |
| 8 | Crear `tasks/task-testing-review.md` | Medio | Medio | #2 |
| 9 | Crear `actions/action-refactor.md` | Bajo | Medio | Ninguna |
| 10 | Actualizar `springboot-tdd.md` con referencias | Bajo | Medio | #1, #2 |

#### PRIORIDAD BAJA (Opcional)

| # | Recomendación | Esfuerzo | Impacto | Dependencias |
|---|---------------|-----------|---------|--------------|
| 11 | Migrar a arquitectura hexagonal | Alto | Bajo | Requiere refactor completo |
| 12 | Traducir código/comentarios a inglés | Alto | Bajo | Ninguna |
| 13 | Crear `tasks/task-architecture-review.md` | Medio | Bajo | #11 |
| 14 | Crear `tasks/task-qa.md` | Medio | Bajo | Ninguna |
| 15 | Crear `actions/action-plan.md` | Bajo | Bajo | Ninguna |

---

## 5. PLAN DE IMPLEMENTACIÓN PROPUESTO

### 5.1 Estructura Final Recomendada

```
TFG_UNIR-backend/
├── AGENTS.md                          (actualizar)
├── docs/
│   └── USING_SKILLS.md               (mantener)
└── .agents/
    └── skills/
        ├── springboot-tdd/SKILL.md   (actualizar)
        ├── springboot-security/SKILL.md (mantener)
        ├── springboot-patterns/SKILL.md (mantener)
        ├── guidelines/
        │   ├── xp-tdd-practices.md   (NUEVO)
        │   ├── testing-standards.md  (NUEVO)
        │   ├── design-principles.md  (NUEVO)
        │   └── git-strategy.md       (NUEVO)
        ├── actions/
        │   ├── action-tdd.md         (NUEVO)
        │   └── action-refactor.md    (NUEVO)
        └── tasks/
            ├── task-validate.md      (NUEVO)
            └── task-testing-review.md (NUEVO)
```

### 5.2 Fases de Implementación

---

#### FASE 1: Fundamentos TDD
**Tiempo estimado:** 2 horas  
**Objetivo:** Establecer las bases del ciclo TDD con prácticas profesionales

**Tareas:**

1.1. **Crear `guidelines/xp-tdd-practices.md`**
   ```
   Contenido:
   - Ciclo Red-Green-Refactor de 5 pasos
   - Transformation Priority Premise (TPP)
   - Inside-Out vs Outside-In development
   - Common TDD mistakes and how to avoid them
   - When to write tests (before, during, after)
   ```

1.2. **Crear `guidelines/testing-standards.md`**
   ```
   Contenido:
   - FIRST principles (Fast, Independent, Repeatable, Self-validating, Timely)
   - Estructura Arrange-Act-Assert
   - Naming conventions para tests
   - Mocks policy (cuándo usar, cuándo evitar)
   - Test data builders
   - Common test smells
   ```

1.3. **Actualizar `springboot-tdd/SKILL.md`**
   ```
   Cambios:
   - Agregar referencia a xp-tdd-practices
   - Agregar referencia a testing-standards
   - Agregar FIRST principles
   - Mejorar con TPP básico
   - Agregar sección de common mistakes
   ```

**Verificación:** Tests escritos siguiendo el nuevo estándar TDD

---

#### FASE 2: Actions y Non-Negotiable Rules
**Tiempo estimado:** 1 hora  
**Objetivo:** Implementar mecanismos para enforcing de prácticas

**Tareas:**

2.1. **Crear `actions/action-tdd.md`**
   ```
   Contenido:
   - Detecta cuando se intenta saltar el ciclo TDD
   - Fuerza escribir test fallido antes de código
   - Checklist de pre-implementación
   - Reglas de no negociar
   ```

2.2. **Crear `actions/action-refactor.md`**
   ```
   Contenido:
   - Checklist de refactorización segura
   - Regla del boy scout
   - Cuándo refactorizar (sin cambiar comportamiento)
   - Red flags que indican necesidad de refactor
   ```

2.3. **Actualizar `AGENTS.md`**
   ```
   Agregar secciones:
   - Development Workflow
   - Non-Negotiable Rules
   - Quick Verification Command
   - Referencia a actions disponibles
   ```

**Verificación:** AGENTS.md incluye todas las rules y commands

---

#### FASE 3: Tasks de Validación
**Tiempo estimado:** 1 hora  
**Objetivo:** Automatizar validación de calidad de código

**Tareas:**

3.1. **Crear `tasks/task-validate.md`**
   ```
   Contenido:
   - compile → lint → test → coverage
   - Checklist pre-commit
   - Commands para cada validación
   - Criterios de aceptación
   ```

3.2. **Crear `tasks/task-testing-review.md`**
   ```
   Contenido:
   - Coverage ≥80% check
   - Test quality metrics
   - FIRST compliance verification
   - Mock usage audit
   - Test coverage por módulo
   ```

**Verificación:** Tasks ejecutables y documentadas en AGENTS.md

---

#### FASE 4: Completar Guidelines
**Tiempo estimado:** 2 horas  
**Objetivo:** Cerrar brechas restantes de guidelines

**Tareas:**

4.1. **Crear `guidelines/design-principles.md`**
   ```
   Contenido:
   - Naming conventions detalladas
   - Function design (parámetros, retorno, side effects)
   - Class design (responsabilidad única)
   - Error handling patterns
   - Dependency injection best practices
   ```

4.2. **Crear `guidelines/git-strategy.md`**
   ```
   Contenido:
   - Feature branching strategy
   - Conventional commits format
   - TDD commit discipline (test → impl → refactor)
   - PR review checklist
   - Merge strategies
   ```

4.3. **Crear `actions/action-plan.md`** (opcional)
   ```
   Contenido:
   - Template para planificar features
   - Checklist de diseño
   - Estimation guidelines
   ```

**Verificación:** Todos los guidelines referenciados desde AGENTS.md

---

### 5.3 Cronograma Consolidado

| Fase | Tareas | Tiempo | Total Acumulado |
|------|--------|--------|-----------------|
| Fase 1 | Fundamentos TDD | 2 horas | 2 horas |
| Fase 2 | Actions + Rules | 1 hora | 3 horas |
| Fase 3 | Tasks | 1 hora | 4 horas |
| Fase 4 | Guidelines restantes | 2 horas | 6 horas |
| **Total** | | **6 horas** | |

---

## 6. DECISIONES APROBADAS

| # | Pregunta | Decisión | Fecha |
|---|----------|----------|-------|
| 6.1 | Arquitectura | ✅ **Layered** (mantener actual) | 01/04/2026 |
| 6.2 | Idioma documentos | ✅ **Inglés** (nuevos documentos) | 01/04/2026 |
| 6.3 | Alcance | ✅ **Recomendado (Fases 1-3)** | 01/04/2026 |
| 6.4 | Integración | ✅ **Skills separadas** | 01/04/2026 |
| 6.5 | Reutilización | ✅ **Skills existentes** (integrar nuevas en existentes) | 01/04/2026 |

### 6.1 Arquitectura del Proyecto

**Decisión:** Mantener arquitectura en capas (controller/service/repository)

**Justificación:** Menos refactor, más simple para TFG. Documentar como mejora futura.

### 6.2 Idioma de Documentación

**Decisión:** Usar **inglés** para nuevos documentos y código

**Justificación:** Consistencia con industria y tooling.

### 6.3 Alcance de Implementación

**Decisión:** **Fases 1-3** (Recomendado)

| Fase | Contenido | Tiempo |
|------|-----------|--------|
| 1 | Fundamentos TDD | 2h |
| 2 | Actions + Rules | 1h |
| 3 | Tasks de validación | 1h |
| **Total** | | **4 horas** |

### 6.4 Integración de Skills

**Decisión:** **Skills separadas** en `.agents/skills/`

```
.agents/skills/
├── guidelines/
│   ├── xp-tdd-practices.md
│   └── testing-standards.md
├── actions/
│   └── action-tdd.md
└── tasks/
    ├── task-validate.md
    └── task-testing-review.md
```

### 6.5 Estrategia de Reutilización

**Decisión:** Integrar contenido nuevo en skills existentes

| Skill Existente | Acción |
|-----------------|--------|
| `springboot-tdd` | **Actualizar** - agregar FIRST principles, TPP, referencias a guidelines |
| `springboot-security` | Mantener - ya completa |
| `springboot-patterns` | Mantener - ya completa |
| `AGENTS.md` | **Actualizar** - agregar Non-Negotiable Rules, Quick Verification |

---

## 7. PLAN DE IMPLEMENTACIÓN (APROBADO)

### 7.1 Archivos a Crear (5 nuevos)

```
TFG_UNIR-backend/.agents/skills/
├── guidelines/
│   ├── xp-tdd-practices.md      # TDD cycle, TPP, Inside-Out/Outside-In
│   └── testing-standards.md       # FIRST, A&A, naming, mocks policy
├── actions/
│   └── action-tdd.md            # Enforce TDD cycle
└── tasks/
    ├── task-validate.md         # compile → lint → test → coverage
    └── task-testing-review.md   # Test quality & coverage review
```

### 7.2 Archivos a Actualizar (2 existentes)

```
TFG_UNIR-backend/
├── AGENTS.md                              # Add: Rules, Commands, Quick Verify
└── .agents/skills/springboot-tdd/SKILL.md  # Enrich: FIRST, TPP, cross-refs
```

### 7.3 Archivos a Mantener (2 existentes)

```
├── .agents/skills/springboot-security/SKILL.md   # No changes
└── .agents/skills/springboot-patterns/SKILL.md   # No changes
```

### 7.4 Detalle por Fase

---

#### FASE 1: TDD Foundations (2h)

**1.1 Crear `guidelines/xp-tdd-practices.md`**
- Red-Green-Refactor 5-step cycle
- Transformation Priority Premise (TPP)
- Inside-Out vs Outside-In development
- Common TDD mistakes
- When to write tests

**1.2 Crear `guidelines/testing-standards.md`**
- FIRST principles
- Arrange-Act-Assert structure
- Test naming conventions
- Mocks policy (when to use, when to avoid)
- Test data builders
- Common test smells

**1.3 Actualizar `springboot-tdd/SKILL.md`**
- Add FIRST principles section
- Add TPP basics
- Cross-reference to xp-tdd-practices and testing-standards

---

#### FASE 2: Actions + Rules (1h)

**2.1 Crear `actions/action-tdd.md`**
- Detect when TDD is being skipped
- Force failing test before code
- Pre-implementation checklist
- Non-negotiable rules

**2.2 Actualizar `AGENTS.md`**
- Add "Non-Negotiable Rules" section
- Add "Quick Verification Command"
- Add reference to available tasks
- Add "Development Workflow" section

---

#### FASE 3: Validation Tasks (1h)

**3.1 Crear `tasks/task-validate.md`**
- compile → lint → test → coverage pipeline
- Pre-commit checklist
- Commands for each validation
- Acceptance criteria

**3.2 Crear `tasks/task-testing-review.md`**
- Coverage ≥80% check
- Test quality metrics
- FIRST compliance verification
- Mock usage audit
- Coverage per module

---

### 7.5 Checklist de Implementación

- [x] **FASE 1** ✅
  - [x] Crear `guidelines/xp-tdd-practices.md`
  - [x] Crear `guidelines/testing-standards.md`
  - [x] Actualizar `springboot-tdd/SKILL.md`
  
- [x] **FASE 2** ✅
  - [x] Crear `actions/action-tdd.md`
  - [x] Actualizar `AGENTS.md`
  
- [x] **FASE 3** ✅
  - [x] Crear `tasks/task-validate.md`
  - [x] Crear `tasks/task-testing-review.md`
  
- [ ] **VALIDACIÓN**
  - [ ] Test con tests reales del proyecto
  - [ ] Verificar integración con herramientas (opencode, etc.)

---

### 7.6 Cronograma Final

| Fase | Tareas | Archivos | Tiempo | Estado |
|------|--------|----------|--------|--------|
| 1 | TDD Foundations | 3 | 2h | ✅ Completada |
| 2 | Actions + Rules | 2 | 1h | ✅ Completada |
| 3 | Validation Tasks | 2 | 1h | ✅ Completada |
| **Total** | | **7** | **4h** | ✅ Implementado |

**Implementación completada:** 01/04/2026

---

## 8. COSTO-BENEFICIO

### 8.1 Inversión Requerida (Ajustado)

| Recurso | Estimación |
|---------|------------|
| Tiempo total | 4 horas |
| Archivos a crear | 5 |
| Archivos a modificar | 2 |

### 8.2 Beneficios Esperados

| Beneficio | Impacto |
|-----------|---------|
| Consistencia en desarrollo | Alto - todos los contribuidores siguen mismas prácticas |
| Reducción de bugs | Alto - TDD reduce errores en producción |
| Mejor mantenibilidad | Medio - código más limpio y testeable |
| Onboarding más rápido | Alto - documentación clara para nuevos desarrolladores |
| Alineación con industria | Medio - estándar masterclass-04 |

### 8.3 ROI Cualitativo

La inversión de 4 horas se amortiza rápidamente:
- Cada sesión de debugging evitada = 30+ minutos ahorrados
- Cada bug de producción evitado = 2+ horas de rework evitadas
- La consistencia reduce fricción en code reviews

---

## 9. RIESGOS Y MITIGACIONES

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|--------------|---------|------------|
| Sobrecarga de documentación | Baja | Bajo | Implementar gradualmente, empezar con mínimo viable |
| Skills no usadas | Media | Medio | Capacitación breve, incentivos |
| Mantenimiento de skills | Baja | Bajo | Revisión trimestral |
| Conflicto con convenciones | Baja | Medio | Validar cambios antes de implementar |

---

## 10. PRÓXIMOS PASOS

- [ ] **Iniciar Fase 1** según plan detallado
- [ ] **Validar con tests reales** que las nuevas prácticas funcionan
- [ ] **Iterar** según feedback

---

## 11. ANEXOS

### Anexo A: Recursos Externos

- [Masterclass-04 Source](../masterclass-04-skills/CLAUDE.md)
- [Spring Boot TDD Best Practices](https://reflectoring.io/spring-boot-test/)
- [TDD Guidelines](https://xp123.com/articles/three-styles-of-test-driven-development/)
- [FIRST Principles](https://pragprog.com/magazines/2012-01/unit-tests-are-first)

### Anexo B: Estructura Final del Proyecto

```
TFG_UNIR-backend/
├── AGENTS.md                              ✅ Existente (actualizar)
├── docs/
│   ├── USING_SKILLS.md                   ✅ Existente (mantener)
│   └── SKILLS_ANALYSIS_REPORT.md         ✅ Este informe
└── .agents/
    └── skills/
        ├── springboot-tdd/SKILL.md       ✅ Existente (actualizar)
        ├── springboot-security/SKILL.md   ✅ Existente (mantener)
        ├── springboot-patterns/SKILL.md  ✅ Existente (mantener)
        ├── guidelines/                    📁 NUEVO
        │   ├── xp-tdd-practices.md      📄 NUEVO
        │   └── testing-standards.md       📄 NUEVO
        ├── actions/                       📁 NUEVO
        │   └── action-tdd.md              📄 NUEVO
        └── tasks/                          📁 NUEVO
            ├── task-validate.md           📄 NUEVO
            └── task-testing-review.md     📄 NUEVO
```

---

**Documento generado:** 29 de Marzo de 2026  
**Última revisión:** 01 de Abril de 2026  
**Estado:** ✅ COMPLETADO  
**Aprobado por:** Usuario  
**Fecha aprobación:** 01/04/2026  
**Fecha implementación:** 01/04/2026
