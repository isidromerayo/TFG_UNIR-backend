#!/bin/bash

# Script para publicar la imagen del backend en Docker Hub
# Uso: ./publish-images.sh [--version X.Y.Z] [--dry-run] [--skip-login]
#
# Solo publica versiones que NO sean SNAPSHOT.
# Por defecto la versión se extrae automáticamente del pom.xml.

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Configuración
DOCKER_USER="isidromerayo"

# Flags
DRY_RUN=false
SKIP_LOGIN=false
CUSTOM_VERSION=""

# Procesar argumentos
while [ $# -gt 0 ]; do
  case "$1" in
    --dry-run)
      DRY_RUN=true
      shift
      ;;
    --skip-login)
      SKIP_LOGIN=true
      shift
      ;;
    --version)
      if [ -z "$2" ]; then
        echo "Error: --version requiere un valor"
        echo "Uso: $0 [--version X.Y.Z] [--dry-run] [--skip-login]"
        exit 1
      fi
      CUSTOM_VERSION="$2"
      shift 2
      ;;
    *)
      echo "Error: Argumento desconocido: $1"
      echo "Uso: $0 [--version X.Y.Z] [--dry-run] [--skip-login]"
      exit 1
      ;;
  esac
done

# Extraer versión del proyecto del pom.xml (segundo <version>, tras el del parent)
BACKEND_VERSION=$(grep -o '<version>[^<]*</version>' pom.xml | sed -n '2p' | sed 's|<version>||;s|</version>||')

# Sobrescribir si se especificó --version
if [ -n "$CUSTOM_VERSION" ]; then
    BACKEND_VERSION="$CUSTOM_VERSION"
fi

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
echo -e "${BLUE}Publicar imagen del Backend en Docker Hub${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Validar que estamos en el directorio correcto
if [ ! -f "Dockerfile" ]; then
    print_error "Archivo Dockerfile no encontrado"
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

# Paso 1: Construir imagen del Backend
print_step "Construyendo imagen del Backend v${BACKEND_VERSION}..."
BACKEND_TAGS="-t ${DOCKER_USER}/spring-backend-tfg:${BACKEND_VERSION} -t ${DOCKER_USER}/spring-backend-tfg:latest"
if [ "$DRY_RUN" = true ]; then
    print_info "  $CONTAINER_CMD build -f Dockerfile --build-arg VERSION=${BACKEND_VERSION} $BACKEND_TAGS ."
else
    $CONTAINER_CMD build -f Dockerfile --build-arg VERSION=${BACKEND_VERSION} $BACKEND_TAGS .
    print_success "Imagen del Backend construida"
fi

# Paso 2: Verificar login en Docker Hub
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

# Paso 3: Publicar imagen del Backend
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
    echo -e "${GREEN}✓ IMAGEN PUBLICADA EXITOSAMENTE${NC}"
fi
echo -e "${BLUE}========================================${NC}"
echo ""
echo "Imagen:"
echo "   ${DOCKER_USER}/spring-backend-tfg:${BACKEND_VERSION}"
echo "   ${DOCKER_USER}/spring-backend-tfg:latest"
echo ""
echo "Para usar la nueva imagen:"
echo "  docker compose pull"
echo "  docker compose up -d"
echo ""
