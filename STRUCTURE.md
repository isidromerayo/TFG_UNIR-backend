# Estructura del Proyecto - TFG UNIR Backend

## 📁 Estructura de Directorios

```
TFG_UNIR-backend/
│
├── 📄 README.md                    # Documentación principal
├── 📄 DOCS_INDEX.md                # Índice de toda la documentación
├── 📄 SECURITY_BCRYPT.md           # Guía rápida de BCrypt (punto de entrada)
├── 📄 SECURITY.md                  # Política de seguridad
├── 📄 AGENTS.md                    # Guía para desarrolladores
│
├── 📂 src/                         # Código fuente
│   ├── main/java/                  # Código principal
│   │   └── eu/estilolibre/tfgunir/backend/
│   │       ├── config/             # Configuración (SecurityConfig)
│   │       ├── controller/         # Controladores REST (LoginController)
│   │       ├── model/              # Entidades JPA
│   │       ├── repository/         # Repositorios
│   │       └── service/            # Servicios
│   ├── main/resources/             # Recursos
│   │   └── application.properties  # Configuración de la app
│   └── test/                       # Tests
│       ├── java/                   # Tests unitarios e integración
│       └── resources/              # Recursos de test
│
├── 📂 scripts/                     # Scripts ejecutables
│   ├── README.md                   # Documentación de scripts
│   ├── build-and-test-bcrypt.sh    # 🔐 Build y test BCrypt (principal)
│   ├── test-login.sh               # 🔐 Pruebas de login
│   └── podman-pod.sh               # 🐳 Gestión de contenedores Podman
│
├── 📂 docs/                        # Documentación
│   ├── README.md                   # Índice de documentación
│   ├── PODMAN_GUIDE.md             # 🐳 Guía completa de Podman
│   ├── security/                   # 🔐 Documentación de seguridad
│   │   ├── README.md                      # Índice de seguridad
│   │   ├── QUICK_START_BCRYPT.md          # Inicio rápido BCrypt
│   │   ├── BCRYPT_MIGRATION_SUMMARY.md    # Resumen completo
│   │   ├── BUILD_AND_TEST_BCRYPT.md       # Guía detallada
│   │   ├── PR_SNYK_TIMING_ATTACK.md       # PR timing attack
│   │   └── SNYK_SECURITY_ISSUE.md         # Issue Snyk
│   └── workflows/                  # Documentación de workflows
│
├── 📂 recursos/db/                 # Recursos de base de datos
│   ├── PASSWORDS_INFO.md           # 🔐 Info de contraseñas
│   ├── CHANGELOG_PASSWORDS.md      # 🔐 Changelog de contraseñas
│   ├── verify-passwords.py         # 🔐 Script de verificación
│   ├── dump.mariadb.sql            # Datos iniciales (con BCrypt)
│   └── create.mariadb.sql          # Esquema de BD
│
├── 📂 target/                      # Archivos compilados
│   ├── site/jacoco/                # Reportes de cobertura combinados
│   ├── site/jacoco-ut/             # Reportes de tests unitarios
│   └── site/jacoco-it/             # Reportes de tests integración
│
├── 🐳 docker-compose.yml           # Configuración Docker Compose
├── 🐳 Dockerfile                   # Imagen del backend
├── 🐳 Dockerfile-db                # Imagen de MariaDB
│
└── 📦 pom.xml                      # Configuración Maven
```

## 🗺️ Mapa de Navegación

### Para Empezar

1. **Nuevo en el proyecto**: Lee [README.md](README.md)
2. **Buscar documentación**: Consulta [DOCS_INDEX.md](DOCS_INDEX.md)
3. **Trabajar con BCrypt**: Empieza con [SECURITY_BCRYPT.md](SECURITY_BCRYPT.md)

### Desarrollo

```
AGENTS.md → Flujo de trabajo
    ↓
Hacer cambios en src/
    ↓
./mvnw test → Tests unitarios
    ↓
./mvnw verify -Pintegration-tests → Tests integración
    ↓
Commit y push
```

### Seguridad y BCrypt

```
SECURITY_BCRYPT.md → Guía rápida
    ↓
./scripts/build-and-test-bcrypt.sh → Build y test
    ↓
./scripts/test-login.sh → Probar login
    ↓
docs/security/ → Documentación completa
```

### Calidad de Código

```
COVERAGE_ANALYSIS.md → Estado actual
    ↓
./mvnw verify -Pintegration-tests → Generar reportes
    ↓
target/site/jacoco/ → Ver reportes
    ↓
SONARQUBE_ISSUES.md → Issues detectados
```

## 📚 Documentación por Tema

### 🔐 Seguridad
- **Punto de entrada**: [SECURITY_BCRYPT.md](SECURITY_BCRYPT.md)
- **Índice completo**: [docs/security/README.md](docs/security/README.md)
- **Quick Start**: [docs/security/QUICK_START_BCRYPT.md](docs/security/QUICK_START_BCRYPT.md)
- **Resumen**: [docs/security/BCRYPT_MIGRATION_SUMMARY.md](docs/security/BCRYPT_MIGRATION_SUMMARY.md)
- **Guía detallada**: [docs/security/BUILD_AND_TEST_BCRYPT.md](docs/security/BUILD_AND_TEST_BCRYPT.md)

### 🔧 Scripts
- **Índice**: [scripts/README.md](scripts/README.md)
- **BCrypt**: `scripts/build-and-test-bcrypt.sh`
- **Login**: `scripts/test-login.sh`
- **Podman Pod**: `scripts/podman-pod.sh`

### 🐳 Contenedores
- **Guía Podman**: [docs/PODMAN_GUIDE.md](docs/PODMAN_GUIDE.md)
- **Docker Compose**: `docker-compose.yml`
- **Dockerfile Backend**: `Dockerfile`
- **Dockerfile MariaDB**: `Dockerfile-db`

### 📊 Calidad
- **Cobertura**: [COVERAGE_ANALYSIS.md](COVERAGE_ANALYSIS.md)
- **JaCoCo**: [JACOCO_CONFIGURATION.md](JACOCO_CONFIGURATION.md)
- **SonarQube**: [SONARQUBE_POM_CONFIG.md](SONARQUBE_POM_CONFIG.md)
- **Issues**: [SONARQUBE_ISSUES.md](SONARQUBE_ISSUES.md)

### 🔄 CI/CD
- **Workflows**: [MANUAL_WORKFLOW_SETUP.md](MANUAL_WORKFLOW_SETUP.md)
- **Monorepo**: [MONOREPO_WORKFLOW_DISTRIBUTION.md](MONOREPO_WORKFLOW_DISTRIBUTION.md)
- **Sync**: [SETUP_MONOREPO_SYNC.md](SETUP_MONOREPO_SYNC.md)

### 🗄️ Base de Datos
- **Contraseñas**: [recursos/db/PASSWORDS_INFO.md](recursos/db/PASSWORDS_INFO.md)
- **Changelog**: [recursos/db/CHANGELOG_PASSWORDS.md](recursos/db/CHANGELOG_PASSWORDS.md)
- **Verificación**: `recursos/db/verify-passwords.py`
- **Datos**: `recursos/db/dump.mariadb.sql`
- **Esquema**: `recursos/db/create.mariadb.sql`

## 🎯 Casos de Uso Comunes

### "Quiero probar BCrypt"

**Con Docker:**
```bash
cd TFG_UNIR-backend
./scripts/build-and-test-bcrypt.sh
```

**Con Podman:**
```bash
cd TFG_UNIR-backend
./scripts/podman-pod.sh start
./scripts/test-login.sh
```

### "Quiero ver la cobertura de código"
```bash
./mvnw clean verify -Pintegration-tests
open target/site/jacoco/index.html
```

### "Quiero ejecutar tests"
```bash
# Solo unitarios
./mvnw test

# Solo integración
./mvnw -DskipUTs -Pintegration-tests verify

# Todos
./mvnw clean verify -Pintegration-tests
```

### "Quiero probar el login"
```bash
# Todos los usuarios
./scripts/test-login.sh

# Usuario específico
./scripts/test-login.sh maria@localhost 1234
```

### "Quiero entender la seguridad"
1. Lee [SECURITY_BCRYPT.md](SECURITY_BCRYPT.md)
2. Consulta [docs/security/README.md](docs/security/README.md)
3. Revisa [docs/security/BCRYPT_MIGRATION_SUMMARY.md](docs/security/BCRYPT_MIGRATION_SUMMARY.md)

### "Quiero contribuir código"
1. Lee [AGENTS.md](AGENTS.md)
2. Sigue el flujo de trabajo
3. Ejecuta tests antes de commit
4. Verifica calidad con SpotBugs

## 🔍 Búsqueda Rápida

| Busco... | Archivo |
|----------|---------|
| Documentación general | [README.md](README.md) |
| Índice de docs | [DOCS_INDEX.md](DOCS_INDEX.md) |
| Guía BCrypt | [SECURITY_BCRYPT.md](SECURITY_BCRYPT.md) |
| Guía Podman | [docs/PODMAN_GUIDE.md](docs/PODMAN_GUIDE.md) |
| Scripts disponibles | [scripts/README.md](scripts/README.md) |
| Docs de seguridad | [docs/security/README.md](docs/security/README.md) |
| Cobertura de código | [COVERAGE_ANALYSIS.md](COVERAGE_ANALYSIS.md) |
| Configuración SonarQube | [SONARQUBE_POM_CONFIG.md](SONARQUBE_POM_CONFIG.md) |
| Flujo de desarrollo | [AGENTS.md](AGENTS.md) |
| Info de contraseñas | [recursos/db/PASSWORDS_INFO.md](recursos/db/PASSWORDS_INFO.md) |

## 📝 Convenciones

### Iconos en Documentación
- 🔐 Seguridad y autenticación
- 🐳 Docker y contenedores
- 📊 Métricas y calidad
- 🔧 Desarrollo y herramientas
- 🔄 CI/CD y workflows
- 🗄️ Base de datos
- 📚 Documentación
- 🚀 Quick start y guías rápidas

### Nombres de Archivos
- `README.md` - Documentación principal de un directorio
- `*_BCRYPT.md` - Relacionado con BCrypt
- `*_ANALYSIS.md` - Análisis y reportes
- `*_CONFIG.md` - Configuración
- `*_SETUP.md` - Guías de instalación
- `*.sh` - Scripts ejecutables

## 🆘 Ayuda

Si no encuentras lo que buscas:
1. Consulta [DOCS_INDEX.md](DOCS_INDEX.md)
2. Busca en el directorio correspondiente
3. Revisa los README.md de cada carpeta
