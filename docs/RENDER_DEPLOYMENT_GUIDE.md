# Despliegue en Render.com

## 🚀 Guía Completa para Desplegar el Backend TFG_UNIR en Render.com

Render.com es la plataforma elegida para este proyecto debido a su facilidad de uso con Docker y su excelente capa gratuita para PostgreSQL.

### 📋 Prerrequisitos

1. **Cuenta en Render.com** (https://render.com)
2. **Cuenta en Docker Hub** (https://hub.docker.com)
3. **Repositorio GitHub** del proyecto
4. **JWT_SECRET**: Genera una cadena aleatoria fuerte (ej: `openssl rand -base64 64`)

---

## Paso 1: Configurar la Base de Datos PostgreSQL

El proyecto usa **PostgreSQL** tanto en desarrollo local como en producción. Render ofrece PostgreSQL nativo en su capa gratuita, lo que lo hace ideal para el despliegue.

1. **Crear la Base de Datos**:
   - Inicia sesión en Render y ve a **New** → **PostgreSQL**.
   - Nombre: `tfg-unir-db`.
   - Región: `Frankfurt (EU)` (recomendado para baja latencia en España).
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

## ⚡ Paso 4: Flujo de Despliegue Optimizado (Recomendado)

El proyecto utiliza un **flujo optimizado** que reduce drásticamente los tiempos de despliegue usando JAR pre-compilado.

### 🎯 Ventajas del Flujo Optimizado:
- ⚡ **Tiempo de deploy**: ~30s (vs 5-10min con multi-stage)
- 🎯 **Control total**: Sabes exactamente qué JAR se despliega
- 📦 **Cache eficiente**: Aprovecha Docker cache del JAR
- 💰 **Costo menor**: Menos tiempo de build en Render

### 🔄 Flujo Optimizado Paso a Paso:

```bash
# 1. Compilar el JAR localmente (con PostgreSQL)
./mvnw clean package -DskipTests

# 2. Añadir JAR y cambios al git
git add target/backend.jar Dockerfile .github/workflows/render-deploy.yml
git commit -m "feat: update JAR and deploy changes"

# 3. Push para activar despliegue automático
git push origin feature/render-deployment
```

### 📋 Qué sucede en el deploy:
1. **GitHub Actions** se activa automáticamente
2. **Build Docker** rápido (~30s) usando JAR pre-compilado
3. **Push a Docker Hub** con imagen optimizada
4. **Deploy a Render** usando la nueva imagen

### ⚠️ Notas Importantes:
- El JAR (`target/backend.jar`) está incluido en git con excepción en `.gitignore`
- El Dockerfile está optimizado para copiar directamente el JAR
- Los GitHub Actions usan versiones estables para evitar errores

---

## 🔄 Paso 5: Automatización con GitHub Actions (CI/CD)

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

### 5. Error: GitHub Actions "Action could not be found"
- **Causa**: SHAs desactualizados en el workflow.
- **Solución**: El workflow usa versiones estables (`@v4`, `@v3`, etc.) para evitar este problema.

### 6. Error: Deploy lento o fallido
- **Causa**: Olvidaste compilar el JAR antes del push.
- **Solución**: Siempre ejecuta `./mvnw clean package -DskipTests` antes de hacer commit y push.

### 7. Error: JAR no encontrado en Docker build
- **Causa**: El JAR no está en git o .gitignore lo está bloqueando.
- **Solución**: Verifica que `!target/backend.jar` esté en `.gitignore` y que el JAR esté commiteado.

---

## ✅ Verificación final

Una vez desplegado, el estado en Render debe aparecer como **Live** ✅.
Puedes probar el estado de salud en:
`https://tu-servicio.onrender.com/actuator/health`
