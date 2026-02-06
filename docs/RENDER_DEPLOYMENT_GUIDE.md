# Despliegue en Render.com

## 🚀 Guía Completa para Desplegar el Backend TFG_UNIR en Render.com

Render.com es una plataforma de hosting cloud que ofrece servicios gratuitos para aplicaciones web y bases de datos. Es ideal para proyectos de TFG porque:

- Ofrece **bases de datos MariaDB gratuitas** (512MB RAM, 1GB almacenamiento)
- Soporta **Docker containers** para fácil deploy
- Proporciona **certificados SSL gratuitos**
- Integración con GitHub para **CI/CD automático**
- Sin coste para proyectos pequeños

## 📋 Prerequisitos

1. **Cuenta en Render.com** (https://render.com)
2. **Cuenta en Docker Hub** (https://hub.docker.com)
3. **Repositorio GitHub** del proyecto (https://github.com/isidromerayo/TFG_UNIR-backend)
4. **Variables de entorno**: JWT_SECRET (generar con `openssl rand -base64 64`)

## 🐬 Paso 1: Desplegar la Base de Datos MariaDB

1. **Crear una base de datos en Render.com**:
   - Inicia sesión en Render.com
   - Haz clic en "New" → "PostgreSQL" (Nota: Render.com no ofrece MariaDB directo, pero PostgreSQL es compatible con la mayoría de las aplicaciones. Si necesitas MariaDB, usa la opción "Private Service" con Docker)
   - Configura:
     - Nombre de la base de datos: `tfg_unir`
     - Región: EU (Frankfurt) - Recomendado para España
     - Plan: Free
   - Haz clic en "Create Database"

2. **Obtener credenciales de la base de datos**:
   - Después de crear la base de datos, obtén la URL de conexión, usuario y contraseña
   - La URL tendrá el formato: `jdbc:postgresql://<host>:5432/<dbname>`

3. **Importar datos iniciales**:
   - Conecta a la base de datos usando herramientas como DBeaver o pgAdmin
   - Ejecuta el script `../recursos/db/dump.mariadb.sql` (Nota: Puedes necesitar convertir el script de MariaDB a PostgreSQL)

## 🚀 Paso 2: Desplegar la Aplicación Spring Boot

1. **Conectar el repositorio GitHub**:
   - En Render.com, haz clic en "New" → "Web Service"
   - Selecciona "GitHub" y conecta tu repositorio
   - Elige la rama `main`

2. **Configurar el servicio**:
   - Nombre del servicio: `tfg-unir-backend`
   - Región: EU (Frankfurt)
   - Plan: Free
   - Branch: main
   - Root Directory: `TFG_UNIR-backend`
   - Runtime: Docker
   - Dockerfile Path: `./Dockerfile`

3. **Variables de Entorno**:
   - Añade las siguientes variables de entorno:
     - `SPRING_DATASOURCE_URL`: URL de conexión a la base de datos PostgreSQL
     - `SPRING_DATASOURCE_USERNAME`: Usuario de la base de datos
     - `SPRING_DATASOURCE_PASSWORD`: Contraseña de la base de datos
     - `JWT_SECRET`: Clave secreta para JWT (genera una con `openssl rand -base64 64`)
     - `SPRING_PROFILES_ACTIVE`: `prod`

4. **Deploy**:
   - Haz clic en "Create Web Service"
   - Render.com compilará y deployará la aplicación automáticamente

## 🔄 Paso 3: Configurar CI/CD Automático

1. **Añadir secrets en GitHub**:
   - En tu repositorio GitHub, ve a "Settings" → "Secrets and variables" → "Actions"
   - Añade las siguientes secrets:
     - `DOCKER_HUB_USERNAME`: Tu username de Docker Hub
     - `DOCKER_HUB_TOKEN`: Token de acceso a Docker Hub (genera uno en https://hub.docker.com/settings/security)
     - `RENDER_SERVICE_ID`: ID del servicio web en Render.com (obténlo de la URL del servicio)
     - `RENDER_API_KEY`: API Key de Render.com (obténlo en https://dashboard.render.com/account#api-keys)

2. **Workflow de GitHub Actions**:
   - El archivo `.github/workflows/render-deploy.yml` está configurado para:
     - Build de la imagen Docker en cada push a main
     - Push de la imagen a Docker Hub
     - Deploy automático en Render.com

## ✅ Paso 4: Probar la API Desplegada

1. **Verificar el healthcheck**:
   - Abre la URL: `https://<tu-servicio>.onrender.com/actuator/health`
   - Deberías obtener una respuesta como: `{"status":"UP"}`

2. **Prueba de login**:
   - Usa curl o Postman para probar el endpoint de login:
   ```bash
   curl -X POST -H "Content-Type: application/json" -d '{"email":"c@example.com","password":"1234"}' https://<tu-servicio>.onrender.com/api/login
   ```

3. **Acceder a Swagger UI**:
   - Abre la URL: `https://<tu-servicio>.onrender.com/swagger-ui.html`

## 🔍 Troubleshooting

### Problemas Comunes

1. **La aplicación no se conecta a la base de datos**:
   - Verifica las variables de entorno `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD`
   - Asegúrate de que la base de datos esté en funcionamiento
   - Comprueba que la URL de conexión sea correcta (para PostgreSQL es `jdbc:postgresql://`)

2. **El healthcheck falla**:
   - Verifica que la aplicación esté escuchando en el puerto 8080
   - Asegúrate de que el endpoint `/actuator/health` esté expuesto

3. **El login no funciona**:
   - Verifica la variable `JWT_SECRET`
   - Asegúrate de que el usuario exista en la base de datos
   - Comprueba los logs de la aplicación en Render.com

### Visualización de Logs

En Render.com, ve a tu servicio web → "Logs" para ver los logs de la aplicación.

## 📊 Monitoreo

Render.com ofrece herramientas de monitoreo básicas:

- **Logs**: Registros de la aplicación
- **Metrics**: Uso de CPU, memoria y red
- **Alerts**: Notificaciones por email para errores
- **Healthchecks**: Verificación automática del estado

## 🔒 Seguridad

### Mejoras de Seguridad para Producción

1. **Variables de Entorno**: No hardcodear credenciales
2. **HTTPS**: Render.com proporciona SSL gratuitamente
3. **CORS**: Configurar correctamente el acceso CORS
4. **Firewall**: Restringir el acceso a la base de datos
5. **Actualizaciones**: Mantener las dependencias al día

## 📈 Escalabilidad

Si necesitas más recursos:

1. **Cambiar de plan**: Render.com ofrece planes pagos con más RAM y CPU
2. **Base de datos**: Puedes escalar la base de datos a un plan más grande
3. **Balanceo de Carga**: Render.com soporta balanceo de carga para servicios web

## 🤝 Contribuciones

Si encuentras problemas o mejoras para esta guía, por favor abre un Issue o Pull Request en el repositorio.
