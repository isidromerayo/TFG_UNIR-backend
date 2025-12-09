# Lecciones Aprendidas - Migración a BCrypt

## Fecha: 2024-12-09

## Resumen

Documentación de los problemas encontrados y soluciones aplicadas durante la migración de contraseñas en texto plano a BCrypt.

---

## 🔴 Problemas Encontrados

### 1. Contraseñas en Texto Plano en Base de Datos

**Problema:**
- Las contraseñas estaban almacenadas en texto plano en `dump.mariadb.sql`
- Vulnerabilidad de seguridad crítica
- Susceptible a timing attacks

**Solución:**
- Generar hashes BCrypt con Python
- Actualizar `dump.mariadb.sql` con los hashes
- Verificar con script `verify-passwords.py`

**Código usado:**
```python
import bcrypt
password = "1234"
salt = bcrypt.gensalt(rounds=10)
hashed = bcrypt.hashpw(password.encode('utf-8'), salt)
```

---

### 2. Problema de DNS con podman-compose

**Problema:**
```
java.net.UnknownHostException: maria_db
Socket fail to connect to maria_db
```

**Causa:**
- `podman-compose` tiene problemas de resolución DNS entre contenedores
- El backend no puede resolver el hostname `maria_db`

**Solución:**
- Usar **Podman Pod** en lugar de `podman-compose`
- Los contenedores en un Pod comparten el namespace de red
- Usar `localhost` en lugar de hostnames

**Configuración:**
```bash
# ❌ NO FUNCIONA con podman-compose
SPRING_DATASOURCE_URL: jdbc:mariadb://maria_db:3306/tfg_unir

# ✅ FUNCIONA con Podman Pod
SPRING_DATASOURCE_URL: jdbc:mariadb://localhost:3306/tfg_unir
```

**Script creado:** `scripts/podman-pod.sh`

---

### 3. Imágenes Docker sin Construir

**Problema:**
- Intentamos usar imágenes que no existían en Docker Hub
- Error: `short-name did not resolve to an alias`

**Solución:**
- Construir imágenes localmente con Podman
- Usar prefijo `localhost/` para imágenes locales
- Especificar `docker.io/` para imágenes de Docker Hub

**Comandos:**
```bash
# Construir imagen de MariaDB
podman build -f Dockerfile-db -t localhost/isidromerayo/mariadb-tfg:0.0.5-bcrypt .

# Construir imagen del backend
./mvnw clean package -DskipTests
podman build -t localhost/isidromerayo/spring-backend-tfg:0.2.3-bcrypt .
```

---

### 4. Backend con Código Antiguo

**Problema:**
- Usamos imagen del backend versión 0.2.2 (sin BCrypt)
- El código de BCrypt estaba en el repositorio pero no en la imagen
- Login fallaba: `Encoded password does not look like BCrypt`

**Solución:**
1. Compilar el código actualizado: `./mvnw clean package`
2. Construir nueva imagen: versión 0.2.3-bcrypt
3. Actualizar `scripts/podman-pod.sh` con la nueva versión

**Lección:** Siempre verificar que la imagen Docker contiene el código actualizado

---

### 5. Volumen de Base de Datos con Datos Antiguos

**Problema:**
- El volumen de MariaDB contenía datos antiguos (contraseñas en texto plano)
- Aunque la imagen tenía el `dump.mariadb.sql` actualizado, los datos no se recargaban
- Los scripts de inicialización solo se ejecutan si la BD está vacía

**Solución:**
```bash
# Detener servicios
./scripts/podman-pod.sh stop

# Eliminar volumen antiguo
podman volume rm tfg_unir-backend_data

# Reiniciar (se crea volumen nuevo con datos actualizados)
./scripts/podman-pod.sh start
```

**Lección:** Siempre eliminar volúmenes al cambiar datos de inicialización

---

### 6. Endpoint de Login Incorrecto

**Problema:**
- Script de test usaba `/api/login`
- El endpoint real era `/api/auth`
- Error 404: Not Found

**Solución:**
- Revisar el código del `LoginController.java`
- Actualizar script `test-login.sh` con la ruta correcta

**Código del controlador:**
```java
@RestController
@RequestMapping("/api/auth")
public class LoginController {
    @PostMapping("")  // Ruta completa: /api/auth
    public ResponseEntity<?> auth(@RequestBody FormUser login) {
        // ...
    }
}
```

---

## ✅ Soluciones Implementadas

### 1. Script Automatizado de Build y Test

**Archivo:** `scripts/build-and-test-bcrypt.sh`

**Características:**
- Detecta automáticamente Docker o Podman
- Verifica contraseñas BCrypt en dump.mariadb.sql
- Construye imágenes
- Reinicia contenedores
- Prueba autenticación
- Muestra resumen de resultados

**Limitación:** No funciona completamente con Podman debido a problemas de DNS

---

### 2. Script de Podman Pod

**Archivo:** `scripts/podman-pod.sh`

**Características:**
- Crea un Pod con MariaDB y Backend
- Comparten namespace de red (usan localhost)
- Comandos: start, stop, restart, status, logs
- Soluciona problemas de DNS de podman-compose

**Uso:**
```bash
./scripts/podman-pod.sh start
./scripts/podman-pod.sh logs api
./scripts/podman-pod.sh stop
```

---

### 3. Script de Test de Login

**Archivo:** `scripts/test-login.sh`

**Características:**
- Prueba login con usuarios específicos
- Prueba todos los usuarios de prueba
- Valida respuestas exitosas y fallidas
- Muestra resumen con colores

**Uso:**
```bash
# Probar un usuario
./scripts/test-login.sh helena@localhost 1234

# Probar todos
./scripts/test-login.sh
```

---

### 4. Documentación Completa

**Archivos creados:**

1. **`docs/PODMAN_GUIDE.md`** - Guía completa de Podman
2. **`docs/security/QUICK_START_BCRYPT.md`** - Inicio rápido
3. **`docs/security/BCRYPT_MIGRATION_SUMMARY.md`** - Resumen completo
4. **`docs/security/BUILD_AND_TEST_BCRYPT.md`** - Guía detallada
5. **`scripts/README.md`** - Documentación de scripts
6. **`SECURITY_BCRYPT.md`** - Punto de entrada principal
7. **`STRUCTURE.md`** - Mapa de navegación

---

## 📋 Checklist de Migración a BCrypt

### Preparación
- [ ] Generar hashes BCrypt para contraseñas de prueba
- [ ] Actualizar `dump.mariadb.sql` con hashes
- [ ] Verificar hashes con `verify-passwords.py`
- [ ] Confirmar que backend tiene código BCrypt

### Construcción
- [ ] Compilar backend: `./mvnw clean package`
- [ ] Construir imagen MariaDB con BCrypt
- [ ] Construir imagen Backend con BCrypt
- [ ] Actualizar versiones en scripts

### Despliegue
- [ ] Detener servicios existentes
- [ ] Eliminar volúmenes antiguos
- [ ] Iniciar con nuevas imágenes
- [ ] Verificar logs de inicio

### Verificación
- [ ] Verificar contraseñas hasheadas en BD
- [ ] Probar login con script de test
- [ ] Probar desde frontend
- [ ] Verificar logs del backend (no debe haber warnings de BCrypt)

---

## 🎓 Lecciones Clave

### 1. Volúmenes de Docker/Podman Persisten Datos

**Problema:** Los datos antiguos permanecen aunque cambies la imagen

**Solución:** Siempre eliminar volúmenes al cambiar datos de inicialización
```bash
docker compose down -v  # El -v es crítico
# o
podman volume rm nombre_volumen
```

### 2. Podman-compose vs Podman Pod

**Problema:** `podman-compose` tiene problemas de DNS

**Solución:** Usar Podman Pod para proyectos con múltiples contenedores
- Mejor resolución de red
- Más estable
- Similar a Kubernetes

### 3. Imágenes Locales vs Remotas

**Problema:** Confusión entre imágenes locales y de registries

**Solución:** Usar prefijos claros
- `localhost/` para imágenes locales
- `docker.io/` para Docker Hub
- Construir localmente durante desarrollo

### 4. Verificar Código en Imágenes

**Problema:** El código en el repositorio ≠ código en la imagen

**Solución:** 
- Siempre reconstruir imágenes después de cambios
- Usar tags de versión específicos
- Verificar logs para confirmar comportamiento

### 5. Scripts de Inicialización de BD

**Problema:** Scripts en `/docker-entrypoint-initdb.d/` solo se ejecutan si la BD está vacía

**Solución:**
- Eliminar volúmenes para forzar reinicialización
- O usar scripts de migración para BDs existentes

---

## 🔧 Comandos Útiles Aprendidos

### Podman

```bash
# Listar imágenes locales
podman images

# Construir imagen local
podman build -t localhost/nombre:tag .

# Listar pods
podman pod ps

# Ver contenedores en un pod
podman ps --pod --filter pod=nombre-pod

# Eliminar volumen
podman volume rm nombre-volumen

# Ejecutar comando en contenedor
podman exec -it contenedor comando

# Ver logs
podman logs -f contenedor
```

### Verificación de Base de Datos

```bash
# Conectar a MariaDB
podman exec -it maria_db mariadb -h localhost -u user_tfg -ptfg_un1r_PWD tfg_unir

# Verificar contraseñas hasheadas
podman exec maria_db mariadb -h localhost -u user_tfg -ptfg_un1r_PWD tfg_unir \
  -e "SELECT id, nombre, LEFT(password, 30) FROM usuarios LIMIT 5"
```

### Maven

```bash
# Compilar sin tests
./mvnw clean package -DskipTests

# Compilar con tests
./mvnw clean verify -Pfailsafe
```

---

## 📊 Métricas del Proyecto

### Tiempo Invertido
- Generación de hashes BCrypt: 10 min
- Actualización de dump.mariadb.sql: 5 min
- Resolución de problemas de DNS: 30 min
- Construcción de imágenes: 15 min
- Documentación: 60 min
- **Total:** ~2 horas

### Archivos Creados/Modificados
- **Modificados:** 3 (dump.mariadb.sql, docker-compose.yml, podman-pod.sh)
- **Creados:** 15 (documentación y scripts)
- **Líneas de documentación:** ~2000

### Cobertura de Documentación
- ✅ Quick Start
- ✅ Guías detalladas
- ✅ Troubleshooting
- ✅ Guía de Podman
- ✅ Scripts automatizados
- ✅ Lecciones aprendidas

---

## 🚀 Recomendaciones para Futuros Proyectos

### 1. Desde el Inicio
- Usar BCrypt desde el principio
- Nunca almacenar contraseñas en texto plano
- Configurar volúmenes nombrados explícitamente

### 2. Documentación
- Documentar problemas encontrados
- Crear scripts de automatización
- Mantener guías de troubleshooting

### 3. Testing
- Probar con volúmenes limpios
- Verificar datos en BD después de inicialización
- Probar login antes de desplegar

### 4. Contenedores
- Preferir Podman Pod sobre podman-compose
- Usar tags de versión específicos
- Mantener imágenes actualizadas

### 5. Seguridad
- Usar BCrypt con 10+ rounds
- Implementar comparación constant-time
- Validar estado de usuario (activo/inactivo)
- Usar tokens JWT con expiración

---

## 📚 Referencias

- [BCrypt Wikipedia](https://en.wikipedia.org/wiki/Bcrypt)
- [Spring Security BCrypt](https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html)
- [Podman Documentation](https://docs.podman.io/)
- [Podman Pods](https://docs.podman.io/en/latest/markdown/podman-pod.1.html)
- [Timing Attacks](https://codahale.com/a-lesson-in-timing-attacks/)

---

## 🎯 Conclusión

La migración a BCrypt fue exitosa pero reveló varios problemas de configuración y despliegue que fueron documentados y resueltos. Las lecciones aprendidas servirán para futuros proyectos y mejoras.

**Estado Final:** ✅ Sistema completamente funcional con BCrypt
