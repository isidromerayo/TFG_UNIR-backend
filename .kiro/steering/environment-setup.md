---
inclusion: always
---

# Configuración del Entorno de Desarrollo

## 🔧 Herramientas Requeridas

### Java Version Management
- **Herramienta**: vfox
- **Versión**: Java 21.0.2+13
- **Activación**: `vfox use java@21`
- **Verificación**: `java -version` debe mostrar "21.0.2"

### Base de Datos
- **Desarrollo**: H2 (en memoria para tests)
- **Producción**: MariaDB 10.3+
- **Configuración**: Ver `application.properties` y `application-test.properties`

### Docker (Opcional)
- **Imagen base**: `eclipse-temurin:21-jdk`
- **Compose**: `docker-compose.yml` disponible
- **Puertos**: 8080 (app), 3306 (MariaDB)

## 🚀 Comandos de Desarrollo Frecuentes

### 📋 Flujo según tipo de cambios

**🔧 Cambios de código (Java, configuración, etc.):**
```bash
# Flujo completo obligatorio
./mvnw -Pfailsafe verify
./mvnw compile spotbugs:check
# commit
./mvnw clean verify -Pfailsafe  # antes de push
```

**📝 Solo documentación (*.md, *.txt, comentarios):**
```bash
# No requiere tests ni SpotBugs
# commit directo
# push directo
```

### Desarrollo Local
```bash
# Lanzar aplicación
./mvnw spring-boot:run

# Con perfil específico
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Lanzar con base de datos externa
./mvnw spring-boot:run -Dspring.datasource.url=jdbc:mariadb://localhost:3306/tfg_unir
```

### Testing
```bash
# Tests unitarios solamente
./mvnw test

# Tests de integración (con perfil failsafe)
./mvnw -Pfailsafe verify

# Tests de integración solamente (sin unitarios)
./mvnw -DskipUTs -Pfailsafe verify

# Test específico
./mvnw test -Dtest=UsuarioRepositoryTests

# Con cobertura
./mvnw test jacoco:report
```

### Análisis y Calidad
```bash
# SpotBugs completo
./mvnw compile spotbugs:spotbugs

# OWASP Dependency Check
./mvnw -Pdependency-check verify

# Build completo con todos los checks
./mvnw clean verify -Pfailsafe -Pdependency-check

# Verificación completa (recomendado antes de push)
./mvnw clean verify -Pfailsafe
```

## 📊 URLs de Desarrollo

- **Aplicación**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console (solo en perfil test)
- **Actuator**: http://localhost:8080/actuator (si está habilitado)

## 🔍 Troubleshooting

### Problema: Tests fallan con Java version
```bash
# Solución
vfox use java@21
./mvnw clean compile
```

### Problema: Puerto 8080 ocupado
```bash
# Cambiar puerto
./mvnw spring-boot:run -Dserver.port=8081
```

### Problema: Base de datos no conecta
```bash
# Verificar MariaDB corriendo
docker ps | grep mariadb
# O usar H2 para desarrollo
./mvnw spring-boot:run -Dspring.profiles.active=test
```