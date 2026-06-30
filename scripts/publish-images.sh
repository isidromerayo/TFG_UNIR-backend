#!/bin/bash

# Script para publicar imágenes en Docker Hub
# Uso: ./publish-images.sh [--dry-run] [--skip-login]
#
# Solo publica versiones que NO sean SNAPSHOT.
# La versión se extrae automáticamente del pom.xml.

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Configuración
MARIADB_VERSION="0.1.0"
DOCKER_USER="isidromerayo"

# Flags
DRY_RUN=false
SKIP_LOGIN=false

# Procesar argumentos
for arg in "$@"; do
  case "$arg" in
    --dry-run)
      DRY_RUN=true
      ;;
    --skip-login)
      SKIP_LOGIN=true
      ;;
    *)
      print_error "Argumento desconocido: $arg"
      echo "Uso: $0 [--dry-run] [--skip-login]"
      exit 1
      ;;
  esac
done

# Extraer versión del pom.xml
BACKEND_VERSION=$(grep -oPm1 '(?<=<version>)[^<]+' pom.xml | head -1)

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

# Validar que estamos en el directorio correcto
if [ ! -f "Dockerfile-db" ] || [ ! -f "Dockerfile" ]; then
    print_error "Archivos Dockerfile no encontrados"
    print_error "Ejecuta este script desde TFG_UNIR-backend/"
    exit 1
fi

# Validar que el backend está compilado
if [ ! -f "target/backend.jar" ]; then
    print_error "Backend no compilado. Ejecuta: ./mvnw clean package"
    exit 1
fi

# Validar que la versión no es SNAPSHOT
if echo "$BACKEND_VERSION" | grep -qi "SNAPSHOT"; then
    print_error "No se publican imágenes SNAPSHOT (versión detectada: ${BACKEND_VERSION})"
    print_info "Ejecuta primero: mvn release:prepare"
    exit 1
fi

print_info "Versión del backend: ${BACKEND_VERSION}"
print_info "Versión de MariaDB: ${MARIADB_VERSION}"
echo ""

if [ "$DRY_RUN" = true ]; then
    print_info "Modo DRY RUN — no se ejecutará ninguna acción real"
    echo ""
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
MARIA_TAGS="-t ${DOCKER_USER}/mariadb-tfg:${MARIADB_VERSION} -t ${DOCKER_USER}/mariadb-tfg:latest"
if [ "$DRY_RUN" = true ]; then
    print_info "  $CONTAINER_CMD build -f Dockerfile-db $MARIA_TAGS ."
else
    $CONTAINER_CMD build -f Dockerfile-db $MARIA_TAGS .
    print_success "Imagen de MariaDB construida"
fi

# Paso 2: Construir imagen del Backend
print_step "Construyendo imagen del Backend v${BACKEND_VERSION}..."
BACKEND_TAGS="-t ${DOCKER_USER}/spring-backend-tfg:${BACKEND_VERSION} -t ${DOCKER_USER}/spring-backend-tfg:latest"
if [ "$DRY_RUN" = true ]; then
    print_info "  $CONTAINER_CMD build -f Dockerfile --build-arg VERSION=${BACKEND_VERSION} $BACKEND_TAGS ."
else
    $CONTAINER_CMD build -f Dockerfile --build-arg VERSION=${BACKEND_VERSION} $BACKEND_TAGS .
    print_success "Imagen del Backend construida"
fi

# Paso 3: Verificar login en Docker Hub
if [ "$SKIP_LOGIN" = false ] && [ "$DRY_RUN" = false ]; then
    print_step "Verificando autenticación en Docker Hub..."
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
fi

# Paso 4: Publicar imagen de MariaDB
print_step "Publicando MariaDB v${MARIADB_VERSION}..."
if [ "$DRY_RUN" = true ]; then
    print_info "  $CONTAINER_CMD push ${DOCKER_USER}/mariadb-tfg:${MARIADB_VERSION}"
    print_info "  $CONTAINER_CMD push ${DOCKER_USER}/mariadb-tfg:latest"
else
    $CONTAINER_CMD push ${DOCKER_USER}/mariadb-tfg:${MARIADB_VERSION}
    $CONTAINER_CMD push ${DOCKER_USER}/mariadb-tfg:latest
    print_success "MariaDB publicada"
fi

# Paso 5: Publicar imagen del Backend
print_step "Publicando Backend v${BACKEND_VERSION}..."
if [ "$DRY_RUN" = true ]; then
    print_info "  $CONTAINER_CMD push ${DOCKER_USER}/spring-backend-tfg:${BACKEND_VERSION}"
    print_info "  $CONTAINER_CMD push ${DOCKER_USER}/spring-backend-tfg:latest"
else
    $CONTAINER_CMD push ${DOCKER_USER}/spring-backend-tfg:${BACKEND_VERSION}
    $CONTAINER_CMD push ${DOCKER_USER}/spring-backend-tfg:latest
    print_success "Backend publicado"
fi

# Resumen
echo ""
echo -e "${BLUE}========================================${NC}"
if [ "$DRY_RUN" = true ]; then
    echo -e "${YELLOW}⚠  DRY RUN — NADA SE PUBLICÓ${NC}"
else
    echo -e "${GREEN}✓ IMÁGENES PUBLICADAS EXITOSAMENTE${NC}"
fi
echo -e "${BLUE}========================================${NC}"
echo ""
echo "Imágenes:"
echo "   ${DOCKER_USER}/mariadb-tfg:${MARIADB_VERSION}"
echo "   ${DOCKER_USER}/mariadb-tfg:latest"
echo "   ${DOCKER_USER}/spring-backend-tfg:${BACKEND_VERSION}"
echo "   ${DOCKER_USER}/spring-backend-tfg:latest"
echo ""
echo "Para usar las nuevas imágenes:"
echo "  docker compose pull"
echo "  docker compose up -d"
echo ""
