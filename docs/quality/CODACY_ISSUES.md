# Análisis de Issues de Codacy

**Fecha**: 2025-12-09  
**Total de issues**: 36

---

## 📊 Resumen por Severidad

| Severidad | Cantidad | Categoría |
|-----------|----------|-----------|
| **CRITICAL** | 3 | Security |
| **HIGH** | 18 | Error prone (Unicode) |
| **MEDIUM** | 15 | Best practice (SQL) |

---

## 🔴 CRITICAL - Seguridad (3 issues)

### 1. Hard-coded Passwords en Tests (2 issues)

**Severidad**: CRITICAL  
**Categoría**: Insecure Storage

#### Ubicaciones:
1. `src/test/java/eu/estilolibre/tfgunir/backend/repository/ValoracionRepositoryTests.java:48`
   ```java
   testUsuario.setPassword("password");
   ```

2. `src/test/java/eu/estilolibre/tfgunir/backend/rest/LoginControllerIT.java:30`
   ```java
   user.setPassword("admin");
   ```

3. `src/test/java/eu/estilolibre/tfgunir/backend/rest/LoginControllerIT.java:46`
   ```java
   user.setPassword("invalid");
   ```

**Análisis**:
- Son passwords en **tests**, no en código de producción
- Es una práctica común en tests usar valores hardcoded
- **Riesgo real**: BAJO (solo afecta a entorno de test con H2 en memoria)

**Solución recomendada**:
- Opción 1: Usar constantes con nombres descriptivos
- Opción 2: Suprimir warning con comentario explicativo
- Opción 3: Configurar Codacy para ignorar archivos de test

### 2. Command Injection en GitHub Actions (1 issue)

**Severidad**: CRITICAL  
**Categoría**: Command Injection

#### Ubicación:
`.github/workflows/notify-monorepo-workflow-content.yml:19`

```yaml
run: |
  # Usando ${{...}} con github context
```

**Análisis**:
- Uso de interpolación de variables en `run:` step
- Potencial inyección si un atacante controla el contexto de GitHub

**Solución recomendada**:
- Usar variables de entorno en lugar de interpolación directa
- Validar y sanitizar inputs

---

## 🟠 HIGH - Error Prone (18 issues)

### Unicode Characters en SQL (18 issues)

**Severidad**: HIGH  
**Categoría**: Error prone - Use of unicode characters in non-unicode string

**Archivos afectados**:
- `src/test/resources/data.sql` (todas las líneas con caracteres españoles)

**Ejemplos**:
- Línea 2: `'Música',''`
- Línea 3: `'Fotografía y vídeo',''`
- Línea 6: `'Informática y software',''`
- Línea 9-11: Nombres con tildes (Isidro, Triángulo)
- Línea 17-19: Títulos de cursos con tildes
- Línea 28: `'Helena','García Sánchez'`
- Líneas 50-57: Contenidos con texto en español (tildes, ñ, etc.)

**Análisis**:
- Son datos de prueba en español con caracteres UTF-8
- El archivo SQL debería tener encoding UTF-8
- **No es un error real**, es una limitación del analizador de Codacy

**Solución recomendada**:
- Opción 1: Añadir `-- encoding: UTF-8` al inicio del archivo
- Opción 2: Configurar Codacy para ignorar `data.sql`
- Opción 3: Mantener como está (falso positivo)

### GitHub Actions sin Pin a SHA (2 issues)

**Severidad**: HIGH  
**Categoría**: Insecure Modules Libraries

#### Ubicaciones:
1. `.github/workflows/notify-monorepo-workflow-content.yml:27`
   ```yaml
   uses: peter-evans/repository-dispatch@v3
   ```

2. `docs/workflows/notify-monorepo-workflow-content.yml:18`
   ```yaml
   uses: peter-evans/repository-dispatch@v3
   ```

**Análisis**:
- Uso de tag `@v3` en lugar de commit SHA completo
- Riesgo: Si el tag se mueve, podría ejecutar código diferente

**Solución recomendada**:
```yaml
# En lugar de:
uses: peter-evans/repository-dispatch@v3

# Usar:
uses: peter-evans/repository-dispatch@ff45666b9427631e3450c54a1bcbee4d9ff4d7c0  # v3.0.0
```

---

## 🟡 MEDIUM - Best Practice (15 issues)

### SQL Best Practices (15 issues)

Todos en `src/test/resources/data.sql`:

1. **Expected SET TRANSACTION ISOLATION LEVEL** (línea 1)
2. **Expected SET ANSI_NULLS ON** (línea 1)
3. **Expected SET NOCOUNT ON** (línea 1)
4. **Expected SET QUOTED_IDENTIFIER ON** (línea 1)
5. **Object name not schema qualified** (8 ocurrencias)
   - Línea 1: `INSERT INTO categorias`
   - Línea 8: `INSERT INTO instructores`
   - Línea 13: `INSERT INTO cursos`
   - Línea 21: `INSERT INTO usuarios`
   - Línea 27: `INSERT INTO usuarios`
   - Línea 36: `INSERT INTO usuarios_cursos`
   - Línea 49: `INSERT INTO contenidos`
   - Línea 60: `INSERT INTO avances`

**Análisis**:
- Son recomendaciones de SQL Server
- Este proyecto usa **H2/MariaDB**, no SQL Server
- **No aplican** estas recomendaciones

**Solución recomendada**:
- Configurar Codacy para desactivar reglas de SQL Server
- O ignorar el archivo `data.sql`

---

## 📋 Plan de Acción Recomendado

### Prioridad 1: CRITICAL - Seguridad Real

✅ **GitHub Actions - Pin to SHA**
- Actualizar actions a commit SHA completo
- Archivos: `.github/workflows/notify-monorepo-workflow-content.yml`

✅ **Command Injection**
- Revisar y sanitizar uso de `${{...}}` en workflows

### Prioridad 2: CRITICAL - Falsos Positivos

⚠️ **Hard-coded Passwords en Tests**
- Crear constantes descriptivas
- O suprimir warnings (son tests, no producción)

### Prioridad 3: HIGH - Configuración

⚠️ **Unicode en SQL**
- Configurar Codacy para ignorar `data.sql`
- O añadir encoding declaration

### Prioridad 4: MEDIUM - No Aplicables

❌ **SQL Server Best Practices**
- Configurar Codacy para desactivar reglas de SQL Server
- Este proyecto usa H2/MariaDB

---

## 🔧 Configuración de Codacy Recomendada

Actualizar `.codacy.yml`:

```yaml
---
exclude_paths:
  - 'target/**'
  - 'src/test/resources/**'  # Excluir data.sql

engines:
  # Desactivar análisis SQL Server
  tsqllint:
    enabled: false
  
  # Configurar SpotBugs
  spotbugs:
    enabled: true
    exclude_paths:
      - 'src/test/**'
```

---

## 📊 Resumen de Acciones

| Acción | Issues | Esfuerzo | Prioridad |
|--------|--------|----------|-----------|
| Pin GitHub Actions a SHA | 2 | Bajo | Alta |
| Revisar command injection | 1 | Medio | Alta |
| Refactor passwords en tests | 3 | Bajo | Media |
| Configurar exclusiones Codacy | 33 | Bajo | Media |

**Total issues reales a resolver**: 3-6  
**Total falsos positivos**: 30-33

---

## 🎯 Recomendación Final

1. **Resolver issues críticos de seguridad** (GitHub Actions)
2. **Configurar Codacy** para ignorar falsos positivos
3. **Refactorizar passwords en tests** (opcional, mejora)
4. **Mantener monitoreo** de nuevos issues

La mayoría de los issues (30+) son falsos positivos o no aplicables al stack tecnológico del proyecto.
