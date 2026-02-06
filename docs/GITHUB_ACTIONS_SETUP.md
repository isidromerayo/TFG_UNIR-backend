# Configuración de GitHub Actions para Despliegue en Render

Esta guía explica cómo configurar los secretos necesarios en GitHub para que el flujo de CI/CD pueda construir la imagen Docker, publicarla en Docker Hub y notificar a Render para el despliegue.

## Secretos de GitHub Necesarios

Para que el workflow `.github/workflows/render-deploy.yml` funcione, debes añadir los siguientes secretos en tu repositorio (**Settings > Secrets and variables > Actions > New repository secret**):

### Docker Hub
| Secreto | Descripción | Cómo obtenerlo |
|---------|-------------|----------------|
| `DOCKER_HUB_USERNAME` | Tu usuario de Docker Hub | Es tu login de Docker Hub (ej: `isidromerayo`). |
| `DOCKER_HUB_TOKEN` | Token de acceso (PAT) | En Docker Hub: **Account Settings > Security > New Access Token**. Permisos: **Read & Write** (necesario para hacer push). |

### Render.com
| Secreto | Descripción | Cómo obtenerlo |
|---------|-------------|----------------|
| `RENDER_API_KEY` | Tu API Key de Render | En Render: **Account Settings > API Keys**. |
| `RENDER_SERVICE_ID` | ID único del servicio web | Es un código que empieza por `srv-`. Lo puedes encontrar así:<br>1. Ve al Dashboard de Render.<br>2. Entra en tu servicio (`tfg-unir-backend`).<br>3. Mira la **URL de tu navegador**. Verás algo como `.../web/srv-c7f8...`.<br>4. Copia solo la parte que empieza por `srv-` (incluido). |

## Pasos para el Despliegue

1.  **Configurar Secretos**: Añade los 4 secretos mencionados arriba en GitHub.
2.  **Cambiar Configuración en Render**:
    *   Ve a la configuración de tu servicio en Render.
    *   Cambia el **Runtime** de `Docker` (que usa el Dockerfile) a **Image** (si prefieres que Render solo tire de la imagen ya construida).
    *   *Nota*: Si mantienes el Runtime `Docker`, Render intentará construirlo también. El flujo con GitHub Actions está diseñado para que Render simplemente reciba la señal de "nueva imagen lista".
3.  **Realizar Push**: Una vez configurado todo, haz el push de los cambios en el workflow.

## Beneficios
- **Velocidad**: GitHub Actions suele ser más rápido construyendo imágenes.
- **Consistencia**: La misma imagen que se testea es la que se despliega.
- **Cache**: GitHub utiliza cache de capas para acelerar builds sucesivos.
