# Guía de Uso con Podman

## Introducción

Podman es una alternativa a Docker que no requiere daemon y puede ejecutar contenedores sin privilegios de root. Este proyecto soporta tanto Docker como Podman.

## Problema con podman-compose

⚠️ **IMPORTANTE**: `podman-compose` tiene problemas conocidos con la resolución DNS entre contenedores. El backend no puede resolver el hostname `maria_db` correctamente.

**Error típico:**
```
java.net.UnknownHostException: maria_db
Socket fail to connect to maria_db
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

# Ver logs de MariaDB
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
│  │   maria_db   │  │ api_service │ │
│  │  (MariaDB)   │  │  (Spring)   │ │
│  │              │  │             │ │
│  │ localhost:   │  │ localhost:  │ │
│  │   3306       │  │   8080      │ │
│  └──────────────┘  └─────────────┘ │
│                                     │
│  Shared Network Namespace           │
└─────────────────────────────────────┘
         │                    │
    Port 3306            Port 8080
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
SPRING_DATASOURCE_URL: jdbc:mariadb://maria_db:3306/tfg_unir
# ❌ maria_db no se resuelve
```

**Con Podman Pod (FUNCIONA):**
```bash
SPRING_DATASOURCE_URL: jdbc:mariadb://localhost:3306/tfg_unir
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
podman exec -it maria_db mariadb -u user_tfg -ptfg_un1r_PWD tfg_unir

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
sudo lsof -i :3306

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

### Problema: MariaDB no inicia

**Solución:**
```bash
# Ver logs de MariaDB
./scripts/podman-pod.sh logs db

# Eliminar volumen y reiniciar
podman volume rm tfg_unir-backend_data
./scripts/podman-pod.sh start
```

### Problema: Backend no conecta a BD

**Solución:**
```bash
# Verificar que MariaDB está corriendo
podman exec maria_db mariadb -u user_tfg -ptfg_un1r_PWD -e "SELECT 1"

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

## Uso con BCrypt

Para probar la migración a BCrypt con Podman:

```bash
# 1. Actualizar la versión de la imagen en el script
# Editar scripts/podman-pod.sh:
# MARIA_DB_IMAGE="isidromerayo/mariadb-tfg:0.0.5-bcrypt"

# 2. Construir la imagen
podman build -f Dockerfile-db -t isidromerayo/mariadb-tfg:0.0.5-bcrypt .

# 3. Iniciar con la nueva imagen
./scripts/podman-pod.sh start

# 4. Probar login
./scripts/test-login.sh
```

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
