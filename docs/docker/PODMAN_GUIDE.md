# Guía de Uso con Podman

## Introducción

Podman es una alternativa a Docker que no requiere daemon y puede ejecutar contenedores sin privilegios de root. Este proyecto soporta tanto Docker como Podman.

## Problema con podman-compose

⚠️ **IMPORTANTE**: `podman-compose` tiene problemas conocidos con la resolución DNS entre contenedores. El backend no puede resolver el hostname `postgres_db` correctamente.

**Error típico:**
```
java.net.UnknownHostException: postgres_db
Socket fail to connect to postgres_db
```

## Solución: Usar Podman Pod

La solución recomendada es usar **Podman Pod** en lugar de `podman-compose`. Un Pod agrupa múltiples contenedores que comparten el mismo namespace de red, similar a los Pods de Kubernetes.

### Ventajas de Podman Pod

- ✅ Los contenedores comparten `localhost` (no necesitan DNS)
- ✅ Más estable que podman-compose
- ✅ Similar a la arquitectura de Kubernetes
- ✅ No requiere daemon
- ✅ Puede ejecutarse sin root

## Uso del Script podman-pod.sh

### Comandos Disponibles

```bash
# Iniciar el backend
./scripts/podman-pod.sh start

# Detener el backend
./scripts/podman-pod.sh stop

# Reiniciar el backend
./scripts/podman-pod.sh restart

# Ver estado
./scripts/podman-pod.sh status

# Ver logs del backend API
./scripts/podman-pod.sh logs
./scripts/podman-pod.sh logs api

# Ver logs de PostgreSQL
./scripts/podman-pod.sh logs db
```

### Flujo Completo

```bash
# 1. Ir al directorio del backend
cd TFG_UNIR-backend

# 2. Iniciar servicios
./scripts/podman-pod.sh start

# 3. Verificar que todo está corriendo
./scripts/podman-pod.sh status

# 4. Probar la API
curl http://localhost:8080/api/categorias

# 5. Ver logs si hay problemas
./scripts/podman-pod.sh logs api
./scripts/podman-pod.sh logs db

# 6. Detener cuando termines
./scripts/podman-pod.sh stop
```

## Cómo Funciona

### Arquitectura del Pod

```
┌─────────────────────────────────────┐
│         backend-pod                 │
│                                     │
│  ┌──────────────┐  ┌─────────────┐ │
│  │  postgres_db │  │ api_service │ │
│  │ (PostgreSQL) │  │  (Spring)   │ │
│  │              │  │             │ │
│  │ localhost:   │  │ localhost:  │ │
│  │   5432       │  │   8080      │ │
│  └──────────────┘  └─────────────┘ │
│                                     │
│  Shared Network Namespace           │
└─────────────────────────────────────┘
         │                    │
    Port 5432            Port 8080
         │                    │
    Host Network
```

### Diferencias Clave

| Aspecto | podman-compose | Podman Pod |
|---------|----------------|------------|
| DNS entre contenedores | ❌ Problemático | ✅ No necesario (localhost) |
| Namespace de red | Separado | Compartido |
| Complejidad | Media | Baja |
| Estabilidad | Baja | Alta |
| Similar a | Docker Compose | Kubernetes Pods |

### Configuración de Conexión

**Con podman-compose (NO FUNCIONA):**
```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres_db:5432/tfg_unir
# ❌ postgres_db no se resuelve
```

**Con Podman Pod (FUNCIONA):**
```bash
SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/tfg_unir
# ✅ localhost funciona porque comparten namespace
```

## Instalación de Podman

### Linux (Debian/Ubuntu)

```bash
sudo apt update
sudo apt install -y podman
```

### Linux (Fedora/RHEL)

```bash
sudo dnf install -y podman
```

### Verificar Instalación

```bash
podman --version
podman info
```

## Comandos Útiles de Podman

### Gestión de Pods

```bash
# Listar pods
podman pod ps

# Ver detalles de un pod
podman pod inspect backend-pod

# Eliminar un pod
podman pod rm -f backend-pod

# Ver logs de todos los contenedores en un pod
podman pod logs backend-pod
```

### Gestión de Contenedores

```bash
# Listar contenedores
podman ps

# Listar contenedores en un pod específico
podman ps --pod --filter pod=backend-pod

# Ejecutar comando en contenedor
podman exec -it postgres_db psql -U user_tfg -d tfg_unir

# Ver logs de un contenedor
podman logs -f api_service
```

### Gestión de Volúmenes

```bash
# Listar volúmenes
podman volume ls

# Inspeccionar volumen
podman volume inspect tfg_unir-backend_data

# Eliminar volumen (¡cuidado, borra datos!)
podman volume rm tfg_unir-backend_data
```

## Troubleshooting

### Problema: Pod ya existe

**Error:**
```
Error: pod backend-pod already exists
```

**Solución:**
```bash
./scripts/podman-pod.sh stop
./scripts/podman-pod.sh start
```

### Problema: Puerto ya en uso

**Error:**
```
Error: cannot listen on the TCP port: address already in use
```

**Solución:**
```bash
# Ver qué está usando el puerto
sudo lsof -i :8080
sudo lsof -i :5432

# Detener el proceso o cambiar el puerto en el script
```

### Problema: Archivos SQL no encontrados

**Error:**
```
[ERROR] Los archivos SQL no existen en ../recursos/db/
```

**Solución:**
```bash
# Asegúrate de ejecutar desde el directorio correcto
cd TFG_UNIR-backend
./scripts/podman-pod.sh start
```

### Problema: PostgreSQL no inicia

**Solución:**
```bash
# Ver logs de PostgreSQL
./scripts/podman-pod.sh logs db

# Eliminar volumen y reiniciar
podman volume rm tfg_unir-backend_pg_data
./scripts/podman-pod.sh start
```

### Problema: Backend no conecta a BD

**Solución:**
```bash
# Verificar que PostgreSQL está corriendo
podman exec postgres_db pg_isready -U user_tfg -d tfg_unir

# Ver logs del backend
./scripts/podman-pod.sh logs api

# Reiniciar todo
./scripts/podman-pod.sh restart
```

## Migración desde Docker

Si vienes de Docker, estos son los cambios principales:

| Docker | Podman |
|--------|--------|
| `docker` | `podman` |
| `docker-compose` | `podman-compose` (no recomendado) |
| `docker-compose up` | `./scripts/podman-pod.sh start` |
| `docker-compose down` | `./scripts/podman-pod.sh stop` |
| `docker-compose logs` | `./scripts/podman-pod.sh logs` |
| `docker ps` | `podman ps` |
| `docker exec` | `podman exec` |

## Probar login

```bash
# Probar autenticación con usuarios de prueba
./scripts/test-login.sh
./scripts/test-login.sh helena@localhost 1234
``````

## Referencias

- [Documentación oficial de Podman](https://docs.podman.io/)
- [Podman vs Docker](https://docs.podman.io/en/latest/Introduction.html)
- [Pods en Podman](https://docs.podman.io/en/latest/markdown/podman-pod.1.html)
- [Migración de Docker a Podman](https://podman.io/getting-started/migration)

## Notas

- Podman no requiere daemon, a diferencia de Docker
- Los contenedores pueden ejecutarse sin root (rootless)
- Compatible con imágenes de Docker Hub
- Los Pods de Podman son similares a los de Kubernetes
- Ideal para entornos de desarrollo y CI/CD
