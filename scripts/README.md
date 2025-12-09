# Scripts del Backend

Scripts de utilidad para el backend TFG UNIR.

## Scripts Disponibles

### 🔐 Seguridad y BCrypt

#### `publish-images.sh`
Script para construir y publicar imágenes en Docker Hub.

**Uso:**
```bash
./scripts/publish-images.sh
```

**Qué hace:**
1. Construye imagen de MariaDB v0.1.0
2. Construye imagen de Backend v0.3.0
3. Etiqueta con :latest
4. Verifica autenticación en Docker Hub
5. Publica todas las imágenes

**Requisitos:**
- Backend compilado (`./mvnw clean package`)
- Autenticado en Docker Hub (`docker login` o `podman login`)

#### `build-and-test-bcrypt.sh`
Script automatizado completo para construir y probar la migración a BCrypt.

**Uso:**
```bash
./scripts/build-and-test-bcrypt.sh
```

**Qué hace:**
1. Verifica contraseñas BCrypt en dump.mariadb.sql
2. Construye nueva imagen Docker (isidromerayo/mariadb-tfg:0.0.5-bcrypt)
3. Reinicia contenedores con volúmenes limpios
4. Verifica contraseñas hasheadas en la base de datos
5. Espera a que el backend esté listo
6. Prueba autenticación con múltiples usuarios
7. Valida rechazo de credenciales incorrectas
8. Muestra resumen de resultados

**Requisitos:**
- Docker o Podman
- curl
- jq (opcional, para formato JSON)

#### `test-login.sh`
Script simple para probar el login con diferentes usuarios.

**Uso:**
```bash
# Probar todos los usuarios de prueba
./scripts/test-login.sh

# Probar un usuario específico
./scripts/test-login.sh maria@localhost 1234
```

**Usuarios de prueba:**
- maria@localhost / 1234
- helena@localhost / 1234
- carlos@localhost / 1234
- ines@localhost / 1234
- Alva_Streich@example.net / TFG_1234

### 🐳 Contenedores

#### `podman-pod.sh`
Script para gestionar contenedores con Podman Pod (alternativa a docker-compose).

⚠️ **IMPORTANTE**: Si usas Podman, usa este script en lugar de `podman-compose` debido a problemas de DNS.

**Uso:**
```bash
# Iniciar pod
./scripts/podman-pod.sh start

# Detener pod
./scripts/podman-pod.sh stop

# Reiniciar pod
./scripts/podman-pod.sh restart

# Ver estado
./scripts/podman-pod.sh status

# Ver logs del backend
./scripts/podman-pod.sh logs
./scripts/podman-pod.sh logs api

# Ver logs de MariaDB
./scripts/podman-pod.sh logs db
```

**Ventajas sobre podman-compose:**
- ✅ Mejor resolución DNS entre contenedores (usan localhost)
- ✅ Más estable en sistemas Linux
- ✅ Similar a Pods de Kubernetes
- ✅ No requiere daemon

**Documentación completa**: Ver [docs/PODMAN_GUIDE.md](../docs/PODMAN_GUIDE.md)

## Estructura de Directorios

```
TFG_UNIR-backend/
├── scripts/
│   ├── README.md                    # Este archivo
│   ├── build-and-test-bcrypt.sh     # Build y test BCrypt
│   ├── test-login.sh                # Pruebas de login
│   └── podman-pod.sh                # Gestión Podman Pod
├── docs/
│   └── security/
│       ├── QUICK_START_BCRYPT.md           # Inicio rápido
│       ├── BCRYPT_MIGRATION_SUMMARY.md     # Resumen completo
│       ├── BUILD_AND_TEST_BCRYPT.md        # Guía detallada
│       ├── PR_SNYK_TIMING_ATTACK.md        # PR timing attack
│       └── SNYK_SECURITY_ISSUE.md          # Issue Snyk
└── ...
```

## Quick Start

Para empezar rápidamente con BCrypt:

```bash
# 1. Ejecutar script automatizado
./scripts/build-and-test-bcrypt.sh

# 2. Si todo pasa, probar login
./scripts/test-login.sh

# 3. Probar desde frontend
cd ../TFG_UNIR-angular
pnpm start
# Login: maria@localhost / 1234
```

## Documentación

Para más información, consulta:

- **Quick Start**: `docs/security/QUICK_START_BCRYPT.md`
- **Resumen Completo**: `docs/security/BCRYPT_MIGRATION_SUMMARY.md`
- **Guía Detallada**: `docs/security/BUILD_AND_TEST_BCRYPT.md`

## Troubleshooting

### Script falla con "Dockerfile-db no encontrado"

Ejecuta el script desde el directorio correcto:
```bash
cd TFG_UNIR-backend
./scripts/build-and-test-bcrypt.sh
```

### Usando Podman en lugar de Docker

Si usas Podman y tienes problemas con DNS (`UnknownHostException: maria_db`):

```bash
# Usa el script de Podman Pod en lugar de podman-compose
./scripts/podman-pod.sh start

# Ver guía completa
cat docs/PODMAN_GUIDE.md
```

### Login falla con credenciales correctas

Reinicia con volúmenes limpios:

**Docker:**
```bash
docker compose down -v
docker compose up -d
```

**Podman:**
```bash
./scripts/podman-pod.sh stop
podman volume rm tfg_unir-backend_data
./scripts/podman-pod.sh start
```

### Backend no inicia

Verifica logs:

**Docker:**
```bash
docker compose logs -f api_service
```

**Podman:**
```bash
./scripts/podman-pod.sh logs api
```

## Contribuir

Al agregar nuevos scripts:

1. Hazlos ejecutables: `chmod +x scripts/nuevo-script.sh`
2. Agrega documentación en este README
3. Incluye comentarios en el script
4. Maneja errores apropiadamente (`set -e`)
5. Usa colores para output (ver ejemplos existentes)
