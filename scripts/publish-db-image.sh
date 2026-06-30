#!/bin/bash

# Script para construir y publicar la imagen de PostgreSQL en Docker Hub
# Uso: ./publish-db-image.sh [<version>] [--dry-run] [--skip-login]
#
# Ejemplos:
#   ./publish-db-image.sh              # usa versión por defecto (1.0)
#   ./publish-db-image.sh 1.1          # versión específica
#   ./publish-db-image.sh --dry-run    # solo mostrar qué haría

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

DOCKER_USER="isidromerayo"
DB_VERSION="1.0"
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
    --*)
      echo "Error: Argumento desconocido: $arg"
      echo "Uso: $0 [<version>] [--dry-run] [--skip-login]"
      exit 1
      ;;
    *)
      DB_VERSION="$arg"
      ;;
  esac
done

DOCKERFILE="Dockerfile-db-postgresql"
IMAGE_NAME="${DOCKER_USER}/postgres-tfg"

print_step() { echo -e "${YELLOW}>>> $1${NC}"; }
print_success() { echo -e "${GREEN}✓ $1${NC}"; }
print_error() { echo -e "${RED}✗ $1${NC}"; }
print_info() { echo -e "${BLUE}ℹ $1${NC}"; }

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Publicar imagen de PostgreSQL en Docker Hub${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

if [ ! -f "$DOCKERFILE" ]; then
    print_error "Archivo ${DOCKERFILE} no encontrado"
    print_error "Ejecuta este script desde TFG_UNIR-backend/"
    exit 1
fi

print_info "Versión: ${DB_VERSION}"
echo ""

if [ "$DRY_RUN" = true ]; then
    print_info "Modo DRY RUN — no se ejecutará ninguna acción real"
    echo ""
fi

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

# Construir
print_step "Construyendo imagen PostgreSQL v${DB_VERSION}..."
TAGS="-t ${IMAGE_NAME}:${DB_VERSION} -t ${IMAGE_NAME}:latest"
if [ "$DRY_RUN" = true ]; then
    print_info "  $CONTAINER_CMD build -f $DOCKERFILE $TAGS ."
else
    $CONTAINER_CMD build -f $DOCKERFILE $TAGS .
    print_success "Imagen PostgreSQL construida"
fi

# Login
if [ "$SKIP_LOGIN" = false ] && [ "$DRY_RUN" = false ]; then
    print_step "Verificando autenticación en Docker Hub..."
    if ! $CONTAINER_CMD login docker.io --get-login &>/dev/null; then
        print_info "Necesitas autenticarte en Docker Hub"
        $CONTAINER_CMD login docker.io
        if [ $? -ne 0 ]; then
            print_error "Autenticación fallida"
            exit 1
        fi
    fi
    print_success "Autenticado en Docker Hub"
fi

# Publicar
print_step "Publicando PostgreSQL v${DB_VERSION}..."
if [ "$DRY_RUN" = true ]; then
    print_info "  $CONTAINER_CMD push ${IMAGE_NAME}:${DB_VERSION}"
    print_info "  $CONTAINER_CMD push ${IMAGE_NAME}:latest"
else
    $CONTAINER_CMD push ${IMAGE_NAME}:${DB_VERSION}
    $CONTAINER_CMD push ${IMAGE_NAME}:latest
    print_success "PostgreSQL publicado"
fi

# Resumen
echo ""
echo -e "${BLUE}========================================${NC}"
if [ "$DRY_RUN" = true ]; then
    echo -e "${YELLOW}⚠  DRY RUN — NADA SE PUBLICÓ${NC}"
else
    echo -e "${GREEN}✓ IMAGEN PUBLICADA EXITOSAMENTE${NC}"
fi
echo -e "${BLUE}========================================${NC}"
echo ""
echo "Imagen:"
echo "   ${IMAGE_NAME}:${DB_VERSION}"
echo "   ${IMAGE_NAME}:latest"
echo ""
echo "Actualiza docker-compose.yml con la nueva versión si es necesario."
echo ""
