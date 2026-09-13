# Migración a BCrypt - Guía Rápida

## 🚀 Inicio Rápido

### Con Docker
```bash
# Ejecutar todo automáticamente
./scripts/build-and-test-bcrypt.sh

# Probar login
./scripts/test-login.sh
```

### Con Podman
```bash
# Construir imagen con BCrypt
podman build -f Dockerfile-db -t isidromerayo/mariadb-tfg:0.0.5-bcrypt .

# Actualizar versión en scripts/podman-pod.sh (línea 10)
# MARIA_DB_IMAGE="isidromerayo/mariadb-tfg:0.0.5-bcrypt"

# Iniciar servicios
./scripts/podman-pod.sh start

# Probar login
./scripts/test-login.sh
```

⚠️ **Nota para usuarios de Podman**: Ver [docs/PODMAN_GUIDE.md](docs/PODMAN_GUIDE.md) para más detalles.

## 📚 Documentación

### Para Empezar
- **[Quick Start](docs/security/QUICK_START_BCRYPT.md)** - Comandos básicos y pruebas rápidas

### Documentación Completa
- **[Resumen de Migración](docs/security/BCRYPT_MIGRATION_SUMMARY.md)** - Resumen ejecutivo completo
- **[Guía de Build y Test](docs/security/BUILD_AND_TEST_BCRYPT.md)** - Guía detallada paso a paso
- **[Índice de Seguridad](docs/security/README.md)** - Índice completo de documentación

### Scripts
- **[Scripts README](scripts/README.md)** - Documentación de scripts disponibles

### Base de Datos
- **[Passwords Info](recursos/db/PASSWORDS_INFO.md)** - Información de contraseñas
- **[Changelog](recursos/db/CHANGELOG_PASSWORDS.md)** - Registro de cambios

## 🔐 Usuarios de Prueba

| Email | Password | Estado |
|-------|----------|--------|
| maria@localhost | 1234 | Pendiente |
| helena@localhost | 1234 | Activo |
| carlos@localhost | 1234 | Activo |
| Alva_Streich@example.net | TFG_1234 | Pendiente |

## ✅ Estado

- [x] Contraseñas hasheadas con BCrypt
- [x] Backend configurado con BCryptPasswordEncoder
- [x] Prevención de timing attacks implementada
- [x] Scripts de testing automatizados
- [x] Documentación completa
- [x] Pruebas desde frontends
- [x] Imágenes Docker publicadas

## 📦 Versiones de Imágenes

### Actuales (con BCrypt)
- **MariaDB**: `isidromerayo/mariadb-tfg:0.1.0`
- **Backend**: `isidromerayo/spring-backend-tfg:0.3.0`

### Publicar Nuevas Versiones
```bash
# Compilar backend
./mvnw clean package -Dmaven.test.skip=true

# Publicar imágenes
./scripts/publish-images.sh
```

Ver [CHANGELOG_IMAGES.md](CHANGELOG_IMAGES.md) para detalles de versiones.

## 🆘 Ayuda

Si tienes problemas, consulta:
1. [Quick Start](docs/security/QUICK_START_BCRYPT.md) - Sección "Si Algo Falla"
2. [Resumen de Migración](docs/security/BCRYPT_MIGRATION_SUMMARY.md) - Sección "Troubleshooting"
3. [Guía Detallada](docs/security/BUILD_AND_TEST_BCRYPT.md) - Sección completa de problemas
4. [Guía de Podman](docs/PODMAN_GUIDE.md) - Si usas Podman en lugar de Docker

## 📁 Estructura

```
TFG_UNIR-backend/
├── SECURITY_BCRYPT.md              # Este archivo (índice principal)
├── scripts/
│   ├── README.md                   # Documentación de scripts
│   ├── build-and-test-bcrypt.sh    # Script principal de build y test
│   ├── test-login.sh               # Pruebas de login
│   └── podman-pod.sh               # Gestión de contenedores
├── docs/
│   └── security/
│       ├── README.md                      # Índice de seguridad
│       ├── QUICK_START_BCRYPT.md          # Inicio rápido
│       ├── BCRYPT_MIGRATION_SUMMARY.md    # Resumen completo
│       ├── BUILD_AND_TEST_BCRYPT.md       # Guía detallada
│       ├── PR_SNYK_TIMING_ATTACK.md       # PR timing attack
│       └── SNYK_SECURITY_ISSUE.md         # Issue Snyk
└── recursos/db/
    ├── PASSWORDS_INFO.md           # Info de contraseñas
    ├── CHANGELOG_PASSWORDS.md      # Changelog
    ├── verify-passwords.py         # Script de verificación
    ├── dump.mariadb.sql            # Datos con BCrypt
    └── create.mariadb.sql          # Esquema
```
