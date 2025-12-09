#!/bin/bash

# Script simple para probar el login con diferentes usuarios
# Uso: ./test-login.sh [email] [password]

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# URL del backend
API_URL="http://localhost:8080/api/auth"

# Si se pasan parámetros, usarlos
if [ $# -eq 2 ]; then
    EMAIL="$1"
    PASSWORD="$2"
    
    echo -e "${YELLOW}Probando login...${NC}"
    echo "Email: $EMAIL"
    echo "Password: $PASSWORD"
    echo ""
    
    RESPONSE=$(curl -s -X POST "$API_URL" \
        -H "Content-Type: application/json" \
        -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}")
    
    if echo "$RESPONSE" | grep -q "token"; then
        echo -e "${GREEN}✓ Login exitoso${NC}"
        echo "$RESPONSE" | jq '.' 2>/dev/null || echo "$RESPONSE"
    else
        echo -e "${RED}✗ Login fallido${NC}"
        echo "$RESPONSE"
    fi
    
    exit 0
fi

# Si no se pasan parámetros, probar todos los usuarios
echo -e "${YELLOW}Probando login con todos los usuarios de prueba...${NC}"
echo ""

# Array de usuarios de prueba
declare -a USERS=(
    "maria@localhost:1234:Pendiente"
    "helena@localhost:1234:Activo"
    "carlos@localhost:1234:Activo"
    "ines@localhost:1234:Activo"
    "Alva_Streich@example.net:TFG_1234:Pendiente"
)

SUCCESS=0
FAILED=0

for USER_DATA in "${USERS[@]}"; do
    IFS=':' read -r EMAIL PASSWORD STATUS <<< "$USER_DATA"
    
    echo -e "${YELLOW}Testing: $EMAIL ($STATUS)${NC}"
    
    RESPONSE=$(curl -s -X POST "$API_URL" \
        -H "Content-Type: application/json" \
        -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}")
    
    if echo "$RESPONSE" | grep -q "token"; then
        echo -e "${GREEN}✓ Login exitoso${NC}"
        SUCCESS=$((SUCCESS + 1))
    else
        echo -e "${RED}✗ Login fallido${NC}"
        echo "  Respuesta: $RESPONSE"
        FAILED=$((FAILED + 1))
    fi
    echo ""
done

# Probar con credenciales incorrectas
echo -e "${YELLOW}Testing: maria@localhost (contraseña incorrecta)${NC}"
RESPONSE=$(curl -s -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -d '{"email":"maria@localhost","password":"wrong_password"}')

if echo "$RESPONSE" | grep -q "token"; then
    echo -e "${RED}✗ Login exitoso con contraseña incorrecta (FALLO DE SEGURIDAD)${NC}"
    FAILED=$((FAILED + 1))
else
    echo -e "${GREEN}✓ Login rechazado correctamente${NC}"
    SUCCESS=$((SUCCESS + 1))
fi
echo ""

# Resumen
echo "=========================================="
echo "Resumen:"
echo -e "  ${GREEN}Exitosos: $SUCCESS${NC}"
echo -e "  ${RED}Fallidos: $FAILED${NC}"
echo "=========================================="

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}✓ Todas las pruebas pasaron${NC}"
    exit 0
else
    echo -e "${RED}✗ Algunas pruebas fallaron${NC}"
    exit 1
fi
