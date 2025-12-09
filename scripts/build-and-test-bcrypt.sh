#!/bin/bash

# Script para construir y probar la nueva imagen de MariaDB con BCrypt
# Autor: Generado automáticamente
# Fecha: 2024-12-09

set -e  # Exit on error

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Variables
DB_IMAGE="isidromerayo/mariadb-tfg"
NEW_VERSION="0.0.5-bcrypt"
OLD_VERSION="0.0.4"

# Detectar Docker o Podman
if command -v docker &> /dev/null && docker info &> /dev/null; then
    CONTAINER_CMD="docker"
    COMPOSE_CMD="docker compose"
elif command -v podman &> /dev/null; then
    CONTAINER_CMD="podman"
    COMPOSE_CMD="podman-compose"
    # Si no hay podman-compose, usar el script de podman-pod
    if ! command -v podman-compose &> /dev/null; then
        print_error "Podman detectado pero podman-compose no está instalado"
        echo "Usa el script alternativo: ./scripts/podman-pod.sh"
        exit 1
    fi
else
    print_error "Ni Docker ni Podman están disponibles"
    exit 1
fi

echo "Usando: $CONTAINER_CMD"
echo ""

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Build and Test BCrypt MariaDB Image${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Función para imprimir mensajes
print_step() {
    echo -e "${YELLOW}>>> $1${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Paso 1: Verificar que estamos en el directorio correcto
print_step "Verificando directorio..."

# Detectar desde dónde se ejecuta el script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Ir al directorio raíz del proyecto
cd "$PROJECT_ROOT"

if [ ! -f "Dockerfile-db" ]; then
    print_error "Dockerfile-db no encontrado. Verifica que estás en TFG_UNIR-backend/"
    exit 1
fi
print_success "Directorio correcto: $PROJECT_ROOT"

# Paso 2: Verificar que dump.mariadb.sql tiene contraseñas BCrypt
print_step "Verificando contraseñas BCrypt en dump.mariadb.sql..."
BCRYPT_COUNT=$(grep -o '\$2b\$10\$' ../recursos/db/dump.mariadb.sql | wc -l)
if [ "$BCRYPT_COUNT" -lt 14 ]; then
    print_error "No se encontraron suficientes hashes BCrypt en dump.mariadb.sql"
    print_error "Encontrados: $BCRYPT_COUNT, esperados: 14"
    exit 1
fi
print_success "Contraseñas BCrypt verificadas ($BCRYPT_COUNT hashes)"

# Paso 3: Construir nueva imagen
print_step "Construyendo imagen $DB_IMAGE:$NEW_VERSION..."
$CONTAINER_CMD build -f Dockerfile-db -t "$DB_IMAGE:$NEW_VERSION" .
print_success "Imagen construida"

# Paso 4: Detener contenedores existentes
print_step "Deteniendo contenedores existentes..."
$COMPOSE_CMD down -v 2>/dev/null || true
print_success "Contenedores detenidos"

# Paso 5: Iniciar nuevos contenedores
print_step "Iniciando contenedores con nueva imagen..."
$COMPOSE_CMD up -d
print_success "Contenedores iniciados"

# Paso 6: Esperar a que MariaDB esté lista
print_step "Esperando a que MariaDB esté lista..."
MAX_ATTEMPTS=30
ATTEMPT=0
while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    if $CONTAINER_CMD exec maria_db mariadb -h maria_db -u user_tfg -ptfg_un1r_PWD -e "SELECT 1" tfg_unir &>/dev/null; then
        print_success "MariaDB está lista"
        break
    fi
    ATTEMPT=$((ATTEMPT + 1))
    echo -n "."
    sleep 2
done

if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
    print_error "Timeout esperando a MariaDB"
    $COMPOSE_CMD logs maria_db
    exit 1
fi

# Paso 7: Verificar contraseñas en la base de datos
print_step "Verificando contraseñas hasheadas en la base de datos..."
RESULT=$($CONTAINER_CMD exec maria_db mariadb -h maria_db -u user_tfg -ptfg_un1r_PWD tfg_unir -e \
    "SELECT COUNT(*) as count FROM usuarios WHERE password LIKE '\$2b\$10\$%'" -s -N)

if [ "$RESULT" -eq 14 ]; then
    print_success "Todas las contraseñas están hasheadas ($RESULT usuarios)"
else
    print_error "Contraseñas no hasheadas correctamente. Encontradas: $RESULT, esperadas: 14"
    exit 1
fi

# Paso 8: Esperar a que el backend esté listo
print_step "Esperando a que el backend esté listo..."
MAX_ATTEMPTS=60
ATTEMPT=0
while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    if curl -s http://localhost:8080/api/categorias &>/dev/null; then
        print_success "Backend está listo"
        break
    fi
    ATTEMPT=$((ATTEMPT + 1))
    echo -n "."
    sleep 2
done

if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
    print_error "Timeout esperando al backend"
    $COMPOSE_CMD logs api_service
    exit 1
fi

# Paso 9: Probar autenticación con credenciales correctas
print_step "Probando login con credenciales correctas..."
RESPONSE=$(curl -s -X POST http://localhost:8080/api/login \
    -H "Content-Type: application/json" \
    -d '{"email":"maria@localhost","password":"1234"}')

if echo "$RESPONSE" | grep -q "token"; then
    print_success "Login exitoso con credenciales correctas"
    echo "$RESPONSE" | jq '.' 2>/dev/null || echo "$RESPONSE"
else
    print_error "Login falló con credenciales correctas"
    echo "Respuesta: $RESPONSE"
    exit 1
fi

# Paso 10: Probar autenticación con credenciales incorrectas
print_step "Probando login con credenciales incorrectas..."
RESPONSE=$(curl -s -X POST http://localhost:8080/api/login \
    -H "Content-Type: application/json" \
    -d '{"email":"maria@localhost","password":"wrong_password"}')

if echo "$RESPONSE" | grep -q "token"; then
    print_error "Login exitoso con credenciales incorrectas (FALLO DE SEGURIDAD)"
    exit 1
else
    print_success "Login rechazado correctamente con credenciales incorrectas"
fi

# Paso 11: Probar con otro usuario
print_step "Probando login con usuario activo (helena@localhost)..."
RESPONSE=$(curl -s -X POST http://localhost:8080/api/login \
    -H "Content-Type: application/json" \
    -d '{"email":"helena@localhost","password":"1234"}')

if echo "$RESPONSE" | grep -q "token"; then
    print_success "Login exitoso con usuario activo"
else
    print_error "Login falló con usuario activo"
    echo "Respuesta: $RESPONSE"
    exit 1
fi

# Paso 12: Probar con TFG_1234
print_step "Probando login con contraseña TFG_1234..."
RESPONSE=$(curl -s -X POST http://localhost:8080/api/login \
    -H "Content-Type: application/json" \
    -d '{"email":"Alva_Streich@example.net","password":"TFG_1234"}')

if echo "$RESPONSE" | grep -q "token"; then
    print_success "Login exitoso con contraseña TFG_1234"
else
    print_error "Login falló con contraseña TFG_1234"
    echo "Respuesta: $RESPONSE"
    exit 1
fi

# Resumen final
echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}✓ TODAS LAS PRUEBAS PASARON${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo "Imagen construida: $DB_IMAGE:$NEW_VERSION"
echo "Contraseñas BCrypt: ✓ Verificadas"
echo "Base de datos: ✓ Funcionando"
echo "Backend: ✓ Funcionando"
echo "Autenticación: ✓ Funcionando"
echo ""
echo "Usuarios de prueba disponibles:"
echo "  - maria@localhost / 1234 (Pendiente)"
echo "  - helena@localhost / 1234 (Activo)"
echo "  - carlos@localhost / 1234 (Activo)"
echo "  - Alva_Streich@example.net / TFG_1234 (Pendiente)"
echo ""
echo "Próximos pasos:"
echo "  1. Probar desde el frontend (Angular/React/Vue)"
echo "  2. Publicar imagen: docker push $DB_IMAGE:$NEW_VERSION"
echo "  3. Actualizar documentación si es necesario"
echo ""
