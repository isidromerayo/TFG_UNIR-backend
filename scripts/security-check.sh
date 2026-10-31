#!/usr/bin/env bash
#
# security-check.sh — auditoría de seguridad rápida del backend (Maven).
#
# Equivalente a `pnpm security` de los frontends: ejecuta las herramientas rápidas
# disponibles localmente (OSV Scanner, Trivy) y el chequeo de advisories aceptados.
# OWASP Dependency-Check no se ejecuta aquí (es lento y depende de NVD); corre en
# el workflow `owasp-dependency-check-maven.yml` (schedule semanal + dispatch).
#
# Uso: ./scripts/security-check.sh
# Salida: 0 si no hay herramientas fallando por infraestructura; los hallazgos se
# reportan por pantalla (OSV/Trivy salen con código !=0 si encuentran vulnerabilidades).

set -uo pipefail

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║  🔒 Auditoría de Seguridad — TFG_UNIR-backend (Maven)      ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

print_section() {
    echo ""
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}  $1${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
}

# 1. OSV Scanner
print_section "1️⃣  OSV Scanner (pom.xml, transitivas)"

if command -v osv-scanner &> /dev/null; then
    if osv-scanner scan source --lockfile=pom.xml --format=table 2>&1; then
        echo -e "${GREEN}✅ OSV Scanner: sin vulnerabilidades${NC}"
    else
        echo -e "${RED}❌ OSV Scanner: vulnerabilidades detectadas (ver arriba)${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  osv-scanner no instalado. Instalación: https://google.github.io/osv-scanner/ (o vía 'go install github.com/google/osv-scanner/v2/cmd/osv-scanner@latest')${NC}"
fi

# 2. Trivy
print_section "2️⃣  Trivy filesystem scan (pom.xml + JARs)"

if command -v trivy &> /dev/null; then
    if trivy fs --scanners vuln --severity CRITICAL,HIGH --ignore-unfixed . 2>&1; then
        echo -e "${GREEN}✅ Trivy: sin vulnerabilidades CRITICAL/HIGH fijables${NC}"
    else
        echo -e "${RED}❌ Trivy: vulnerabilidades detectadas (ver arriba)${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  trivy no instalado. Instalación: https://aquasecurity.github.io/trivy/ (o './scripts/install-trivy.sh' desde el monorepo)${NC}"
fi

# 3. Advisories aceptados
print_section "3️⃣  Advisories aceptados (revisión de parche publicado)"

if [ -x ./scripts/check-accepted-advisories.sh ]; then
    ./scripts/check-accepted-advisories.sh
else
    echo -e "${YELLOW}⚠️  scripts/check-accepted-advisories.sh no disponible${NC}"
fi

echo ""
echo -e "${BLUE}════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}  Fin de la auditoría rápida. Para el escaneo profundo (OWASP):${NC}"
echo -e "${BLUE}  mvn org.owasp:dependency-check-maven:check -Dnvd.api.key=\$NVD_API_KEY${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════════════${NC}"
