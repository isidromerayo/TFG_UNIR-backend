# Despliegue en Render.com

## 🚀 Guía Completa para Desplegar el Backend TFG_UNIR en Render.com

Render.com es la plataforma elegida para este proyecto debido a su facilidad de uso con Docker y su excelente capa gratuita para PostgreSQL.

### 📋 Prerrequisitos

1. **Cuenta en Render.com** (https://render.com)
2. **Cuenta en Docker Hub** (https://hub.docker.com)
3. **Repositorio GitHub** del proyecto
4. **JWT_SECRET**: Genera una cadena aleatoria fuerte (ej: `openssl rand -base64 64`)

---

## � Paso 1: Configurar la Base de Datos PostgreSQL

Aunque el proyecto usa MariaDB en local, **en Render usamos PostgreSQL** por su mejor soporte en la capa gratuita.

1. **Crear la Base de Datos**:
   - Inicia sesión en Render y ve a **New** → **PostgreSQL**.
   - Nombre: `tfg-unir-db`.
   - Región: `Frankfurt (EU)` (recomendado para baja latencia en España).
   - Plan: `Free`.
2. **Obtener la "Internal Database URL"**:
   - Una vez creada, copia el valor de **Internal Database URL**. 
   - ⚠️ **IMPORTANTE**: No uses la External URL para el backend, la Internal es más rápida y segura.

---

## � Paso 2: Crear un Environment Group (Recomendado)

Para gestionar mejor las credenciales y reutilizarlas en otros servicios:

1. Ve a **Dashboard** → **Environment Groups** → **New Environment Group**.
2. Nombre: `common`.
3. Añade las siguientes variables:
   - `SPRING_DATASOURCE_URL`: (Pega la **Internal Database URL** de PostgreSQL)
   - `SPRING_PROFILES_ACTIVE`: `prod`
   - `JWT_SECRET`: (Tu clave generada)
   - `SPRING_DATASOURCE_USERNAME`: (El usuario que te dio Render)
   - `SPRING_DATASOURCE_PASSWORD`: (La contraseña que te dio Render)

---

## 🚀 Paso 3: Desplegar el Web Service (Backend)

1. En el Dashboard de Render: **New** → **Web Service** → **Build and deploy from a Git repository**.
2. Conecta tu repositorio de GitHub.
3. **Configuración Inicial**:
   - **Name**: `tfg-unir-backend`.
   - **Runtime**: `Docker`.
   - **Branch**: `feature/render-deployment` (o la que uses para producción).
   - **Root Directory**: `TFG_UNIR-backend`.
4. **Vincular Variables**:
   - Ve a la pestaña **Environment**.
   - En **Linked Environment Groups**, selecciona `common` y dale a **Link**.

---

## 🔄 Paso 4: Automatización con GitHub Actions (CI/CD)

El archivo `.github/workflows/render-deploy.yml` gestiona el despliegue automático.

1. **GitHub Secrets**: En tu repo de GitHub (**Settings** → **Secrets** → **Actions**), añade:
   - `DOCKER_HUB_USERNAME`
   - `DOCKER_HUB_TOKEN`
   - `RENDER_API_KEY` (En Render: Account Settings → API Keys)
   - `RENDER_SERVICE_ID` (Se encuentra en la URL de tu servicio en Render: `srv-xxxxxxxx`)

2. **Seguridad**: Las acciones están fijadas por **commit SHA** para cumplir con estándares de seguridad (Codacy).

---

## 🔍 Troubleshooting (Solución de problemas)

### 1. Error: `'url' must start with "jdbc"` o `UnknownHostException: notset`
- **Causa**: El servicio no está leyendo las variables de entorno.
- **Solución**: Asegúrate de haber vinculado el **Environment Group** `common` al servicio web.

### 2. Error SSL: `Could not open SSL root certificate file /root/.postgresql/root.crt`
- **Solución**: El código ya está configurado para usar `NonValidatingFactory`. No necesitas subir certificados manualmente.

### 3. Error: `NumberFormatException` al arrancar
- **Causa**: Comentarios en la misma línea que un valor numérico en los archivos `.properties`.
- **Solución**: Mantén los comentarios en líneas separadas.

### 4. Error: `No open ports detected`
- **Solución**: La aplicación usa automáticamente `${PORT}` proporcionado por Render. No fuerces el puerto 8080 en la configuración del Dashboard de Render.

---

## ✅ Verificación final

Una vez desplegado, el estado en Render debe aparecer como **Live** ✅.
Puedes probar el estado de salud en:
`https://tu-servicio.onrender.com/actuator/health`
