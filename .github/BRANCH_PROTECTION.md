# Configuración de Branch Protection Rules

Para configurar las reglas de protección de la rama `main`, sigue estos pasos:

## 📋 Configuración recomendada

1. Ve a **Settings** → **Branches** en tu repositorio de GitHub
2. Haz clic en **Add rule**
3. En **Branch name pattern** escribe: `main`
4. Activa las siguientes opciones:

### ✅ Reglas básicas
- **Require a pull request before merging**
  - Require approvals: `1`
  - Dismiss stale PR approvals when new commits are pushed
  - Require review from code owners (si tienes CODEOWNERS)

### ✅ Reglas de CI/CD
- **Require status checks to pass before merging**
  - Require branches to be up to date before merging
  - Status checks requeridos:
    - `Unit Tests`
    - `Integration Tests` 
    - `Build Verification`
    - `Code Quality Check`

### ✅ Reglas adicionales
- **Restrict pushes that create files**
- **Require linear history** (opcional, para historial limpio)
- **Include administrators** (para que todos sigan las reglas)

## 🚀 Comandos Git recomendados

```bash
# Crear nueva rama para feature
git checkout main
git pull origin main
git checkout -b feature/nombre-descriptivo

# Trabajar en la rama
git add .
git commit -m "feat: descripción del cambio"

# Push y crear PR
git push -u origin feature/nombre-descriptivo
# Luego crear PR desde GitHub web

# Después del merge, limpiar
git checkout main
git pull origin main
git branch -d feature/nombre-descriptivo
```

## 📝 Convenciones de commits

Usar [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` nueva funcionalidad
- `fix:` corrección de bug
- `docs:` cambios en documentación
- `style:` cambios de formato (no afectan funcionalidad)
- `refactor:` refactoring de código
- `test:` añadir o modificar tests
- `chore:` tareas de mantenimiento