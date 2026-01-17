# Guía de Ejecución del Plan de Migración a Spring Boot 3.5.9

**Fecha:** 2026-01-17
**Estado:** 🚧 En Ejecución Manual
**Rama:** `chore/upgrade-springboot-3-5`

---

## ✅ Completado

- [x] Actualización de Spring Boot a 3.5.9 en [`pom.xml`](../pom.xml)
- [x] Actualización de SpringDoc OpenAPI a 2.8.5
- [x] Verificación de dependencias con `dependency:tree`
- [x] Compilación exitosa: `./mvnw clean compile`

---

## 📋 Comandos a Ejecutar Manualmente

Ejecuta los siguientes comandos en tu terminal (donde tienes Java 21 configurado):

### 0. Preparar Base de Datos MariaDB (Prerequisito)

**IMPORTANTE**: Los tests de integración usan H2 (base de datos en memoria) y NO requieren MariaDB. Solo necesitas MariaDB para:
- Arrancar la aplicación con `./mvnw spring-boot:run`
- Pruebas manuales con Docker Compose

Si solo vas a ejecutar tests, **puedes saltar esta sección** e ir directamente a la sección 1.

#### ¿Cuándo necesitas MariaDB?

- ✅ **NO necesitas MariaDB** para: Tests unitarios y de integración
- ⚠️ **SÍ necesitas MariaDB** para: Arrancar la aplicación localmente o con Docker

---

Antes de arrancar la aplicación localmente, necesitas tener MariaDB corriendo. Tienes varias opciones:

#### Opción A: Usar Docker Compose (Recomendado)

```bash
cd TFG_UNIR-backend

# Levantar solo MariaDB
docker compose up -d maria_db

# Verificar que está corriendo
docker compose ps

# Ver logs
docker compose logs -f maria_db

# Verificar conexión
mariadb -h 127.0.0.1 -u user_tfg -ptfg_un1r_PWD tfg_unir
```

**Credenciales:**
- Host: `localhost` (o `127.0.0.1`)
- Puerto: `3306`
- Base de datos: `tfg_unir`
- Usuario: `user_tfg`
- Contraseña: `tfg_un1r_PWD`

**Detener MariaDB:**
```bash
docker compose stop maria_db
# O para eliminar completamente:
docker compose down
```

#### Opción B: Usar Podman Pod (Alternativa)

Si prefieres Podman, usa el script proporcionado:

```bash
cd TFG_UNIR-backend/scripts

# Iniciar solo MariaDB con Podman
podman run -d --name maria_db \
  -p 3306:3306 \
  -v tfg_unir-backend_data:/var/lib/mysql \
  -v $(pwd)/../recursos/db/create.mariadb.sql:/docker-entrypoint-initdb.d/create.mariadb.sql \
  -v $(pwd)/../recursos/db/dump.mariadb.sql:/docker-entrypoint-initdb.d/dump.mariadb.sql \
  docker.io/isidromerayo/mariadb-tfg:0.1.0

# Verificar que está corriendo
podman ps

# Ver logs
podman logs -f maria_db

# Detener
podman stop maria_db
podman rm maria_db
```

#### Opción C: MariaDB Local (Si ya lo tienes instalado)

Si tienes MariaDB instalado localmente:

```bash
# Crear base de datos y usuario (solo primera vez)
mariadb -u root -p
```

```sql
CREATE DATABASE tfg_unir;
CREATE USER 'user_tfg'@'%' IDENTIFIED BY 'tfg_un1r_PWD';
GRANT ALL ON tfg_unir.* TO 'user_tfg'@'%';
FLUSH PRIVILEGES;
EXIT;
```

```bash
# Cargar datos iniciales
cd TFG_UNIR-backend
mariadb -u user_tfg -ptfg_un1r_PWD tfg_unir < ../recursos/db/create.mariadb.sql
mariadb -u user_tfg -ptfg_un1r_PWD tfg_unir < ../recursos/db/dump.mariadb.sql
```

**Verificar que MariaDB está listo:**
```bash
# Probar conexión
mariadb -h 127.0.0.1 -u user_tfg -ptfg_un1r_PWD tfg_unir -e "SHOW TABLES;"

# Deberías ver las tablas: avance, categoria, contenido, curso, instructor, usuario, valoracion
```

**Troubleshooting: "Connection refused" al arrancar la aplicación**

Si ves el error `java.sql.SQLNonTransientConnectionException: Socket fail to connect to localhost`:

1. **Verifica que MariaDB está corriendo**:
   ```bash
   # Con Docker Compose
   docker compose ps
   # Deberías ver maria_db en estado "Up (healthy)"
   
   # Con Podman
   podman ps
   # Deberías ver maria_db corriendo
   ```

2. **Verifica que el puerto 3306 está expuesto**:
   ```bash
   # Ver qué está escuchando en el puerto 3306
   sudo lsof -i :3306
   # O con netstat
   sudo netstat -tlnp | grep 3306
   ```

3. **Prueba la conexión directamente**:
   ```bash
   mariadb -h 127.0.0.1 -P 3306 -u user_tfg -ptfg_un1r_PWD tfg_unir
   # Si esto funciona, MariaDB está accesible
   ```

4. **Verifica la configuración de Spring**:
   ```bash
   # Ver la configuración de datasource
   cat src/main/resources/application.properties | grep datasource
   
   # Debería ser:
   # spring.datasource.url=jdbc:mariadb://localhost:3306/tfg_unir
   ```

5. **Si usas Podman en lugar de Docker**, asegúrate de que el puerto está mapeado correctamente:
   ```bash
   podman port maria_db
   # Debería mostrar: 3306/tcp -> 0.0.0.0:3306
   ```

---

### 1. Tests Completos (Unitarios + Integración)

```bash
cd TFG_UNIR-backend
./mvnw -Pintegration-tests verify
```

**Criterio de Éxito:**
- ✅ 15/15 tests pasando (11 UT + 4 IT)
- ✅ BUILD SUCCESS

**Si fallan tests:**
```bash
# Ver detalles de los fallos
./mvnw -Pintegration-tests verify -X

# Ejecutar solo tests unitarios
./mvnw test

# Ejecutar solo tests de integración
./mvnw -DskipUTs -Pintegration-tests verify
```

### 2. Verificar Cobertura de Código

```bash
# Generar reportes de cobertura
./mvnw clean verify -Pintegration-tests

# Abrir reporte en navegador
xdg-open target/site/jacoco/index.html
# O en macOS: open target/site/jacoco/index.html
```

**Criterio de Éxito:**
- ✅ Cobertura ≥85%
- ✅ Reportes generados en `target/site/jacoco/`

### 3. Análisis Estático con SpotBugs

```bash
./mvnw compile spotbugs:check
```

**Criterio de Éxito:**
- ✅ Sin errores críticos
- ✅ BUILD SUCCESS

**Si hay errores:**
```bash
# Ver reporte detallado
./mvnw spotbugs:spotbugs
xdg-open target/spotbugsXml.xml
```

### 4. Análisis de Seguridad OWASP (Opcional)

```bash
# Sin API key (más lento, ~10-15 min)
./mvnw -Pdependency-check verify

# Con API key del NVD (recomendado, más rápido)
./mvnw -Pdependency-check verify -Dnvd.api.key=${NVD_API_KEY}
```

**Criterio de Éxito:**
- ✅ Sin vulnerabilidades críticas nuevas
- ✅ Reporte generado en `target/dependency-check-report.html`

**Nota:** Este paso puede omitirse si no tienes API key del NVD. Se ejecutará en CI/CD.

### 5. Pruebas Funcionales - Arranque de Aplicación

```bash
# Arrancar aplicación
./mvnw spring-boot:run
```

**Verificaciones mientras la aplicación está corriendo:**

#### a) Verificar logs de arranque
Buscar en los logs:
- ✅ Sin warnings de Hibernate
- ✅ Sin errores de validación de queries
- ✅ Mensaje: "Started BackendApplication in X seconds"

#### b) Probar Actuator Endpoints

En otra terminal:
```bash
# Health endpoint
curl http://localhost:8080/actuator/health | jq

# Info endpoint (debe mostrar git commit)
curl http://localhost:8080/actuator/info | jq

# Metrics endpoint
curl http://localhost:8080/actuator/metrics | jq
```

**Criterio de Éxito:**
- ✅ `/actuator/health` responde con status "UP"
- ✅ `/actuator/info` muestra información de Git
- ✅ `/actuator/metrics` lista métricas disponibles

#### c) Verificar Swagger UI

Abrir en navegador:
```
http://localhost:8080/swagger-ui.html
```

**Criterio de Éxito:**
- ✅ Swagger UI carga correctamente
- ✅ Endpoints documentados visibles
- ✅ Puede probar endpoints desde la UI

#### d) Probar Endpoints REST con HAL

```bash
# Endpoint de cursos (requiere autenticación)
curl -H "Accept: application/hal+json" http://localhost:8080/api/cursos | jq

# Si requiere autenticación, primero hacer login
curl -X POST http://localhost:8080/api/v1/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}' | jq

# Usar el token en requests subsecuentes
TOKEN="<token-obtenido>"
curl -H "Authorization: Bearer $TOKEN" \
     -H "Accept: application/hal+json" \
     http://localhost:8080/api/cursos | jq
```

**Criterio de Éxito:**
- ✅ Estructura JSON con `_links` y `_embedded` correcta
- ✅ Autenticación JWT funciona
- ✅ Endpoints REST responden correctamente

**Detener aplicación:** `Ctrl+C`

### 6. Pruebas con Docker Compose

El proyecto incluye un [`docker-compose.yml`](../docker-compose.yml) que levanta tanto MariaDB como el backend API.

#### Opción A: Docker Compose (Recomendado)

```bash
cd TFG_UNIR-backend

# 1. Construir imagen del backend (si has hecho cambios)
./mvnw clean package -DskipTests
docker compose build api_service

# 2. Levantar todos los servicios (MariaDB + Backend)
docker compose up -d

# 3. Verificar que están corriendo
docker compose ps

# Deberías ver:
# NAME                IMAGE                                    STATUS
# maria_db            isidromerayo/mariadb-tfg:0.1.0          Up (healthy)
# api_service         isidromerayo/spring-backend-tfg:0.4.0   Up (healthy)

# 4. Ver logs
docker compose logs -f api_service
docker compose logs -f maria_db

# 5. Verificar health de los servicios
curl http://localhost:8080/actuator/health | jq

# 6. Probar API
curl http://localhost:8080/api | jq

# 7. Detener servicios
docker compose stop

# 8. Eliminar servicios y volúmenes (limpieza completa)
docker compose down -v
```

**Configuración del docker-compose.yml:**

El archivo configura:
- **maria_db**: MariaDB con datos precargados
  - Puerto: 3306
  - Imagen: `isidromerayo/mariadb-tfg:0.1.0`
  - Volumen persistente: `maria-db-data`
  - Scripts SQL montados desde `../recursos/db/`
  
- **api_service**: Backend Spring Boot
  - Puerto: 8080
  - Imagen: `isidromerayo/spring-backend-tfg:0.4.0`
  - Depende de MariaDB (espera a que esté healthy)
  - Variables de entorno configuradas para conectar a MariaDB

**Healthchecks:**
- MariaDB: Verifica que InnoDB está inicializado
- Backend: Verifica que `/api` responde

#### Opción B: Podman Pod (Alternativa)

Si prefieres Podman, usa el script [`scripts/podman-pod.sh`](../scripts/podman-pod.sh):

```bash
cd TFG_UNIR-backend/scripts

# Iniciar pod completo (MariaDB + Backend)
./podman-pod.sh start

# Ver estado
./podman-pod.sh status

# Ver logs del backend
./podman-pod.sh logs api

# Ver logs de MariaDB
./podman-pod.sh logs db

# Detener pod
./podman-pod.sh stop

# Reiniciar pod
./podman-pod.sh restart
```

**El script crea:**
- Un pod llamado `backend-pod`
- Contenedor `maria_db` con MariaDB
- Contenedor `api_service` con el backend
- Puertos expuestos: 3306 (MariaDB) y 8080 (Backend)

**Ventajas de Podman Pod:**
- Mejor resolución DNS entre contenedores
- No requiere Docker Desktop
- Más ligero en recursos

#### Verificaciones Post-Despliegue

Una vez levantados los servicios con Docker Compose o Podman:

```bash
# 1. Verificar que MariaDB está accesible
mariadb -h 127.0.0.1 -u user_tfg -ptfg_un1r_PWD tfg_unir -e "SELECT COUNT(*) FROM usuario;"

# 2. Verificar endpoints del backend
curl http://localhost:8080/actuator/health | jq
curl http://localhost:8080/actuator/info | jq

# 3. Probar autenticación
curl -X POST http://localhost:8080/api/v1/login \
  -H "Content-Type: application/json" \
  -d '{"username":"c@example.com","password":"1234"}' | jq

# 4. Abrir Swagger UI en navegador
xdg-open http://localhost:8080/swagger-ui.html
```

**Criterio de Éxito:**
- ✅ Ambos contenedores en estado "Up (healthy)"
- ✅ MariaDB responde a queries
- ✅ Backend responde en puerto 8080
- ✅ Actuator endpoints funcionando
- ✅ Autenticación JWT operativa
- ✅ Swagger UI accesible

#### Troubleshooting Docker/Podman

**Problema: MariaDB no arranca**
```bash
# Ver logs detallados
docker compose logs maria_db

# Verificar que existen los scripts SQL
ls -la ../recursos/db/create.mariadb.sql
ls -la ../recursos/db/dump.mariadb.sql

# Eliminar volumen y reiniciar
docker compose down -v
docker compose up -d
```

**Problema: Backend no conecta a MariaDB**
```bash
# Verificar que MariaDB está healthy
docker compose ps

# Verificar variables de entorno del backend
docker compose exec api_service env | grep SPRING_DATASOURCE

# Ver logs del backend
docker compose logs api_service | grep -i "database\|mariadb\|connection"
```

**Problema: Puerto 3306 o 8080 ya en uso**
```bash
# Ver qué proceso usa el puerto
sudo lsof -i :3306
sudo lsof -i :8080

# Detener proceso o cambiar puerto en docker-compose.yml
```

---

## 📊 Checklist de Verificación

Marca cada item cuando lo completes:

### Tests y Calidad
- [ ] Tests completos ejecutados: `./mvnw -Pintegration-tests verify`
- [ ] 15/15 tests pasando
- [ ] Cobertura ≥85% verificada
- [ ] SpotBugs sin errores críticos
- [ ] OWASP ejecutado (opcional)

### Pruebas Funcionales
- [ ] Aplicación arranca sin errores
- [ ] Logs sin warnings de Hibernate
- [ ] Actuator endpoints funcionando
- [ ] Swagger UI accesible
- [ ] Autenticación JWT operativa
- [ ] Endpoints REST con HAL correctos

### Docker (Opcional)
- [ ] Imagen Docker construida
- [ ] Contenedor arranca correctamente

---

## 📝 Registro de Resultados

### Tests
```
# Pegar aquí el resultado de: ./mvnw -Pintegration-tests verify
[INFO] Tests run: X, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Cobertura
```
# Pegar aquí el porcentaje de cobertura del reporte
Cobertura total: XX%
```

### SpotBugs
```
# Pegar aquí el resultado de: ./mvnw compile spotbugs:check
[INFO] BUILD SUCCESS
```

### Problemas Encontrados
```
# Documentar aquí cualquier problema encontrado y su solución
```

---

## 🎯 Próximos Pasos

Una vez completadas todas las verificaciones:

### 1. Actualizar Documentación

Archivos a actualizar:

#### [`README.md`](../README.md)
```markdown
<!-- Actualizar badge de Spring Boot -->
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-brightgreen)

<!-- Actualizar sección de Stack Tecnológico -->
- **Spring Boot:** 3.5.9
- **Spring Framework:** 6.2.15
```

#### [`AGENTS.md`](../AGENTS.md)
```markdown
### Tecnologías Clave
- **Framework:** Spring Boot 3.5.9
```

#### [`docs/SPRING_BOOT_LIFECYCLE.md`](SPRING_BOOT_LIFECYCLE.md)
```markdown
## Versión Actual

**Spring Boot:** 3.5.9  
**Fecha de Actualización:** 2026-01-17  
**Estado:** ✅ Actualizado
```

### 2. Completar Documento de Migración

Actualizar [`docs/SPRING_BOOT_3.5_MIGRATION.md`](SPRING_BOOT_3.5_MIGRATION.md) con:
- Resultados de tests
- Problemas encontrados y soluciones
- Lecciones aprendidas adicionales

### 3. Commit y Push

```bash
# Añadir cambios
git add pom.xml
git add README.md AGENTS.md
git add docs/SPRING_BOOT_3.5_MIGRATION.md
git add docs/MIGRATION_EXECUTION_GUIDE.md
git add docs/SPRING_BOOT_LIFECYCLE.md

# Commit
git commit -m "chore: upgrade Spring Boot from 3.4.12 to 3.5.9

- Update spring-boot-starter-parent to 3.5.9
- Update SpringDoc OpenAPI to 2.8.5
- All tests passing (15/15)
- Code coverage maintained at 85%+
- Documentation updated

Closes #81"

# Push
git push -u origin chore/upgrade-springboot-3-5
```

### 4. Crear Pull Request

**Título:** `chore: Upgrade Spring Boot to 3.5.9`

**Descripción:**
```markdown
## 🎯 Objetivo

Migrar el proyecto de Spring Boot 3.4.12 a Spring Boot 3.5.9 para mantener soporte activo y recibir actualizaciones de seguridad.

## 📋 Cambios Realizados

- ✅ Actualizado `spring-boot-starter-parent` a 3.5.9
- ✅ Actualizado SpringDoc OpenAPI a 2.8.5
- ✅ Verificada compatibilidad de todas las dependencias
- ✅ Todos los tests pasando (15/15)
- ✅ Cobertura de código mantenida en 85%+
- ✅ Análisis de calidad ejecutado
- ✅ Documentación actualizada

## 🧪 Testing

### Tests Unitarios
- ✅ 11/11 tests pasando

### Tests de Integración
- ✅ 4/4 tests pasando

### Cobertura
- ✅ XX% (objetivo: ≥85%)

### Análisis de Calidad
- ✅ SpotBugs: Sin errores críticos
- ✅ Compilación exitosa con Java 21

## 🔍 Verificación Manual

- ✅ Aplicación arranca correctamente
- ✅ Endpoints API funcionando
- ✅ Autenticación JWT operativa
- ✅ Swagger UI accesible
- ✅ Actuator endpoints correctos

## 📚 Documentación

- ✅ README.md actualizado
- ✅ AGENTS.md actualizado
- ✅ SPRING_BOOT_LIFECYCLE.md actualizado
- ✅ Documento de migración creado

## 🔗 Referencias

- Issue: #81
- [Spring Boot 3.5 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.5-Release-Notes)
- [Documento de Migración](docs/SPRING_BOOT_3.5_MIGRATION.md)

Closes #81
```

---

## 📞 Soporte

Si encuentras algún problema durante la ejecución:

1. Verifica que estás usando Java 21: `java -version`
2. Revisa los logs detallados: `./mvnw -X <comando>`
3. Consulta el documento de migración: [`SPRING_BOOT_3.5_MIGRATION.md`](SPRING_BOOT_3.5_MIGRATION.md)
4. Revisa el plan original: [`../plans/SPRING_BOOT_3.5_MIGRATION_PLAN.md`](../plans/SPRING_BOOT_3.5_MIGRATION_PLAN.md)

---

**Última Actualización:** 2026-01-17  
**Estado:** 📋 Listo para Ejecución Manual
