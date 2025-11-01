# Documentación del Proyecto

## 📁 Estructura

### `/workflows/`
Contiene plantillas de workflows de GitHub Actions que deben copiarse manualmente a `.github/workflows/` debido a limitaciones de permisos.

- `notify-monorepo-workflow-content.yml` - Para este repositorio (TFG_UNIR-backend)
- `update-submodules-workflow-content.yml` - Para el monorepo (TFG_UNIR-monorepo)

### Archivos de configuración en la raíz:
- `SETUP_MONOREPO_SYNC.md` - Guía completa de configuración
- `MONOREPO_WORKFLOW_DISTRIBUTION.md` - Distribución de archivos
- `MANUAL_WORKFLOW_SETUP.md` - Instrucciones paso a paso

## 🎯 Propósito

Estos archivos configuran la sincronización automática entre proyectos individuales y el monorepo, donde cada commit a `main` actualiza automáticamente los submódulos correspondientes.