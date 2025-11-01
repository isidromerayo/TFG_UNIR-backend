# Configuración Manual de Workflows (Solución a Problemas de Permisos)

## 🚨 Problema
GitHub rechaza el push de workflows debido a limitaciones de permisos del token OAuth de Kiro.

## ✅ Solución Manual

### 1. **Para TFG_UNIR-backend (este repositorio)**

1. Ve a GitHub web: https://github.com/isidromerayo/TFG_UNIR-backend
2. Navega a `.github/workflows/`
3. Crea un nuevo archivo: `notify-monorepo.yml`
4. Copia el contenido de: `docs/workflows/notify-monorepo-workflow-content.yml`

**O usando git directamente:**
```bash
# Desde tu terminal local (no Kiro)
git checkout main
git pull origin main

# Crear el directorio si no existe
mkdir -p .github/workflows

# Copiar el contenido del archivo docs/workflows/notify-monorepo-workflow-content.yml
# al archivo .github/workflows/notify-monorepo.yml

git add .github/workflows/notify-monorepo.yml
git commit -m "feat: add monorepo notification workflow"
git push origin main
```

### 2. **Para TFG_UNIR-monorepo**

1. Ve al repositorio monorepo
2. Navega a `.github/workflows/`
3. Crea un nuevo archivo: `update-submodules.yml`
4. Copia el contenido de: `docs/workflows/update-submodules-workflow-content.yml`

### 3. **Configurar Secrets**

En **cada proyecto individual** (TFG_UNIR-backend, etc.):

1. Ve a Settings → Secrets and variables → Actions
2. Añade nuevo secret:
   - **Name**: `MONOREPO_DISPATCH_TOKEN`
   - **Value**: Tu Personal Access Token (con permisos repo + workflow)

## 🧪 Testing

### Test del workflow:
```bash
# Hacer un commit a main en TFG_UNIR-backend
git checkout main
echo "test" >> README.md
git add README.md
git commit -m "test: trigger monorepo sync"
git push origin main

# Verificar en GitHub Actions:
# 1. TFG_UNIR-backend → Actions → "Notify Monorepo on Main Update"
# 2. TFG_UNIR-monorepo → Actions → "Update Submodules"
```

## 📋 Checklist de Configuración

- [ ] Token PAT creado con permisos `repo` + `workflow`
- [ ] Secret `MONOREPO_DISPATCH_TOKEN` añadido en TFG_UNIR-backend
- [ ] Workflow `notify-monorepo.yml` creado en TFG_UNIR-backend
- [ ] Workflow `update-submodules.yml` creado en TFG_UNIR-monorepo
- [ ] Test realizado con commit a main

## 🔄 Flujo Esperado

```
Commit a main (TFG_UNIR-backend)
    ↓
notify-monorepo.yml se ejecuta
    ↓
Envía repository_dispatch a monorepo
    ↓
update-submodules.yml se ejecuta en monorepo
    ↓
Submódulo actualizado automáticamente
```

## 🛠️ Troubleshooting

### Si el workflow no se ejecuta:
1. Verificar que el archivo está en `.github/workflows/`
2. Verificar sintaxis YAML
3. Verificar que el secret existe y es correcto

### Si falla la notificación:
1. Verificar permisos del token PAT
2. Verificar que el repositorio monorepo existe
3. Revisar logs en GitHub Actions

### Si falla la actualización del submódulo:
1. Verificar que el submódulo existe en el monorepo
2. Verificar permisos de escritura en el monorepo
3. Revisar configuración de git en el workflow