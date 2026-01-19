#!/bin/bash
# Script para gestionar el backend con Podman Pod
# Uso: ./podman-pod.sh [start|stop|restart|status|logs]

set -e

POD_NAME="backend-pod"
MARIA_DB_CONTAINER="maria_db"
API_SERVICE_CONTAINER="api_service"
MARIA_DB_IMAGE="docker.io/isidromerayo/mariadb-tfg:0.1.0"
API_SERVICE_IMAGE="docker.io/isidromerayo/spring-backend-tfg:0.4.0"
VOLUME_NAME="tfg_unir-backend_data"

# Cargar variables de entorno desde archivo .env
if [ -f ".env" ]; then
    export $(grep -v '^#' .env | xargs)
fi

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

function print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

function print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

function print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

function check_sql_files() {
    if [ ! -f "../recursos/db/create.mariadb.sql" ] || [ ! -f "../recursos/db/dump.mariadb.sql" ]; then
        print_error "Los archivos SQL no existen en ../recursos/db/"
        print_error "Asegúrate de que existen:"
        print_error "  - ../recursos/db/create.mariadb.sql"
        print_error "  - ../recursos/db/dump.mariadb.sql"
        exit 1
    fi
}

function start_pod() {
    print_info "Iniciando backend con Podman Pod..."
    
    check_sql_files
    
    # Verificar si el pod ya existe
    if podman pod exists $POD_NAME; then
        print_warn "El pod $POD_NAME ya existe. Eliminándolo..."
        podman pod rm -f $POD_NAME
    fi
    
    # Crear el pod
    print_info "Creando pod $POD_NAME..."
    podman pod create --name $POD_NAME -p 8080:8080 -p 3306:3306
    
    # Ejecutar MariaDB
    print_info "Iniciando MariaDB..."
    podman run -d --pod $POD_NAME --name $MARIA_DB_CONTAINER \
        -v $VOLUME_NAME:/var/lib/mysql \
        -v $(pwd)/../recursos/db/create.mariadb.sql:/docker-entrypoint-initdb.d/create.mariadb.sql \
        -v $(pwd)/../recursos/db/dump.mariadb.sql:/docker-entrypoint-initdb.d/dump.mariadb.sql \
        -e MARIADB_ROOT_PASSWORD=${MARIADB_ROOT_PASSWORD:-mypass} \
        -e MYSQL_DATABASE=${MYSQL_DATABASE:-tfg_unir} \
        -e MYSQL_USER=${MYSQL_USER:-user_tfg} \
        -e MYSQL_PASSWORD=${MYSQL_PASSWORD:-tfg_un1r_PWD} \
        $MARIA_DB_IMAGE
    
    # Esperar a que MariaDB esté listo
    print_info "Esperando a que MariaDB esté listo..."
    sleep 10
    
    # Ejecutar el backend
    print_info "Iniciando backend API..."
    podman run -d --pod $POD_NAME --name $API_SERVICE_CONTAINER \
        -e SPRING_DATASOURCE_URL=jdbc:mariadb://localhost:3306/tfg_unir \
        -e SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME:-user_tfg} \
        -e SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD:-tfg_un1r_PWD} \
        -e JWT_SECRET=${JWT_SECRET:-813cef5f-3459-4618-87a6-a69e2a1296d4_mySecretKey_mySecretKey_CHANGE_IN_PRODUCTION} \
        $API_SERVICE_IMAGE
    
    print_info "Esperando a que el backend esté listo..."
    sleep 15
    
    print_info "✅ Backend iniciado correctamente"
    print_info "API disponible en: http://localhost:8080/api"
    print_info "Swagger UI en: http://localhost:8080/swagger-ui.html"
}

function stop_pod() {
    print_info "Deteniendo backend..."
    
    if podman pod exists $POD_NAME; then
        podman pod stop $POD_NAME
        podman pod rm $POD_NAME
        print_info "✅ Backend detenido"
    else
        print_warn "El pod $POD_NAME no existe"
    fi
}

function restart_pod() {
    print_info "Reiniciando backend..."
    stop_pod
    start_pod
}

function status_pod() {
    print_info "Estado del backend:"
    echo ""
    
    if podman pod exists $POD_NAME; then
        echo "Pod:"
        podman pod ps --filter name=$POD_NAME
        echo ""
        echo "Contenedores:"
        podman ps --pod --filter pod=$POD_NAME
    else
        print_warn "El pod $POD_NAME no existe"
    fi
}

function logs_pod() {
    if [ -z "$2" ]; then
        print_info "Logs del backend API:"
        podman logs --tail 50 $API_SERVICE_CONTAINER
    else
        case $2 in
            api|backend)
                print_info "Logs del backend API:"
                podman logs --tail 50 $API_SERVICE_CONTAINER
                ;;
            db|mariadb)
                print_info "Logs de MariaDB:"
                podman logs --tail 50 $MARIA_DB_CONTAINER
                ;;
            *)
                print_error "Servicio desconocido: $2"
                print_info "Usa: logs [api|db]"
                exit 1
                ;;
        esac
    fi
}

function show_usage() {
    echo "Uso: $0 [comando]"
    echo ""
    echo "Comandos disponibles:"
    echo "  start    - Iniciar el backend con Podman Pod"
    echo "  stop     - Detener el backend"
    echo "  restart  - Reiniciar el backend"
    echo "  status   - Ver el estado del backend"
    echo "  logs     - Ver logs del backend API"
    echo "  logs api - Ver logs del backend API"
    echo "  logs db  - Ver logs de MariaDB"
    echo ""
    echo "Ejemplos:"
    echo "  $0 start"
    echo "  $0 status"
    echo "  $0 logs db"
}

# Main
case ${1:-} in
    start)
        start_pod
        ;;
    stop)
        stop_pod
        ;;
    restart)
        restart_pod
        ;;
    status)
        status_pod
        ;;
    logs)
        logs_pod "$@"
        ;;
    *)
        show_usage
        exit 1
        ;;
esac
