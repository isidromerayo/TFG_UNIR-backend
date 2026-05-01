# Distribución de Workflows para Sincronización de Monorepo

## 📁 Ubicación de Archivos

### En cada proyecto individual (TFG_UNIR-backend, TFG_UNIR-frontend, etc.):

```
.github/workflows/notify-monorepo.yml
```

**Propósito**: Notificar al monorepo cuando hay commits en main

### En el monorepo (TFG_UNIR-monorepo):

```
.github/workflows/update-submodules.yml
```

**Propósito**: Recibir notificaciones y actualizar submódulos automáticamente

## 🔄 Flujo de Archivos

1. **TFG_UNIR-backend** → Contiene `notify-monorepo.yml`
2. **TFG_UNIR-frontend** → Contiene `notify-monorepo.yml` 
3. **TFG_UNIR-monorepo** → Contiene `update-submodules.yml`

## 📋 Pasos de Implementación

### Para TFG_UNIR-backend (este repositorio):
```bash
# Ya está configurado:
.github/workflows/notify-monorepo.yml ✅
```

### Para TFG_UNIR-monorepo:
```bash
# Copiar el contenido de update-submodules-workflow.yml a:
.github/workflows/update-submodules.yml
```

### Para otros proyectos individuales:
```bash
# Copiar notify-monorepo.yml de este repositorio a:
# TFG_UNIR-frontend/.github/workflows/notify-monorepo.yml
# Otros-proyectos/.github/workflows/notify-monorepo.yml
```

## ⚙️ Configuración por Repositorio

| Repositorio | Workflow | Secret Requerido |
|-------------|----------|------------------|
| TFG_UNIR-backend | notify-monorepo.yml | MONOREPO_DISPATCH_TOKEN |
| TFG_UNIR-frontend | notify-monorepo.yml | MONOREPO_DISPATCH_TOKEN |
| TFG_UNIR-monorepo | update-submodules.yml | GITHUB_TOKEN (automático) |

## 🚀 Resultado Final

Cuando se hace commit a main en cualquier proyecto individual:
1. Se ejecuta `notify-monorepo.yml` en ese proyecto
2. Se dispara `update-submodules.yml` en el monorepo
3. El monorepo se actualiza automáticamente