#!/bin/bash

# Script para publicar imágenes en Docker Hub
# Uso: ./publish-images.sh

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Versiones
MARIADB_VERSION="0.1.0"
BACKEND_VERSION="0.4.0"
DOCKER_USER="isidromerayo"

print_step() {
    echo -e "${YELLOW}>>> $1${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ $1${NC}"
}

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Publicar Imágenes en Docker Hub${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Verificar que estamos en el directorio correcto
if [ ! -f "Dockerfile-db" ] || [ ! -f "Dockerfile" ]; then
    print_error "Archivos Dockerfile no encontrados"
    print_error "Ejecuta este script desde TFG_UNIR-backend/"
    exit 1
fi

# Verificar que el backend está compilado
if [ ! -f "target/backend.jar" ]; then
    print_error "Backend no compilado. Ejecuta: ./mvnw clean package"
    exit 1
fi

# Detectar Docker o Podman
if command -v docker &> /dev/null && docker info &> /dev/null; then
    CONTAINER_CMD="docker"
elif command -v podman &> /dev/null; then
    CONTAINER_CMD="podman"
else
    print_error "Ni Docker ni Podman están disponibles"
    exit 1
fi

print_info "Usando: $CONTAINER_CMD"
echo ""

# Paso 1: Construir imagen de MariaDB
print_step "Construyendo imagen de MariaDB v${MARIADB_VERSION}..."
$CONTAINER_CMD build -f Dockerfile-db \
    -t ${DOCKER_USER}/mariadb-tfg:${MARIADB_VERSION} \
    -t ${DOCKER_USER}/mariadb-tfg:latest .
print_success "Imagen de MariaDB construida"

# Paso 2: Construir imagen del Backend
print_step "Construyendo imagen del Backend v${BACKEND_VERSION}..."
$CONTAINER_CMD build -f Dockerfile \
    -t ${DOCKER_USER}/spring-backend-tfg:${BACKEND_VERSION} \
    -t ${DOCKER_USER}/spring-backend-tfg:latest .
print_success "Imagen del Backend construida"

# Paso 3: Verificar login en Docker Hub
print_step "Verificando autenticación en Docker Hub..."

# Intentar login interactivo si no está autenticado
if ! $CONTAINER_CMD login docker.io --get-login &>/dev/null; then
    print_info "Necesitas autenticarte en Docker Hub"
    echo ""
    $CONTAINER_CMD login docker.io
    
    if [ $? -ne 0 ]; then
        print_error "Autenticación fallida"
        exit 1
    fi
fi

print_success "Autenticado en Docker Hub"

# Paso 4: Publicar imagen de MariaDB
print_step "Publicando MariaDB v${MARIADB_VERSION}..."
$CONTAINER_CMD push ${DOCKER_USER}/mariadb-tfg:${MARIADB_VERSION}
print_success "MariaDB v${MARIADB_VERSION} publicada"

print_step "Publicando MariaDB:latest..."
$CONTAINER_CMD push ${DOCKER_USER}/mariadb-tfg:latest
print_success "MariaDB:latest publicada"

# Paso 5: Publicar imagen del Backend
print_step "Publicando Backend v${BACKEND_VERSION}..."
$CONTAINER_CMD push ${DOCKER_USER}/spring-backend-tfg:${BACKEND_VERSION}
print_success "Backend v${BACKEND_VERSION} publicado"

print_step "Publicando Backend:latest..."
$CONTAINER_CMD push ${DOCKER_USER}/spring-backend-tfg:latest
print_success "Backend:latest publicado"

# Resumen
echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}✓ IMÁGENES PUBLICADAS EXITOSAMENTE${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo "Imágenes publicadas en Docker Hub:"
echo ""
echo "📦 MariaDB con BCrypt:"
echo "   - ${DOCKER_USER}/mariadb-tfg:${MARIADB_VERSION}"
echo "   - ${DOCKER_USER}/mariadb-tfg:latest"
echo ""
echo "📦 Backend con BCrypt:"
echo "   - ${DOCKER_USER}/spring-backend-tfg:${BACKEND_VERSION}"
echo "   - ${DOCKER_USER}/spring-backend-tfg:latest"
echo ""
echo "Características:"
echo "  ✅ Contraseñas hasheadas con BCrypt"
echo "  ✅ Comparación constant-time (previene timing attacks)"
echo "  ✅ SecurityConfig con BCryptPasswordEncoder"
echo "  ✅ LoginController con validación segura"
echo ""
echo "Para usar las nuevas imágenes:"
echo "  docker pull ${DOCKER_USER}/mariadb-tfg:${MARIADB_VERSION}"
echo "  docker pull ${DOCKER_USER}/spring-backend-tfg:${BACKEND_VERSION}"
echo ""
echo "O actualiza docker-compose.yml con las nuevas versiones."
echo ""
