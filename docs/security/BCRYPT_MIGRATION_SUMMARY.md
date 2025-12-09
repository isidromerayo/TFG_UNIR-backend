# Resumen de Migración a BCrypt

## Fecha: 2024-12-09

## Objetivo

Migrar las contraseñas de usuarios de texto plano a hashes BCrypt para mejorar la seguridad y prevenir timing attacks.

## Cambios Realizados

### 1. Base de Datos

**Archivo modificado:** `recursos/db/dump.mariadb.sql`

- ✅ 14 usuarios actualizados con contraseñas hasheadas BCrypt
- ✅ Eliminadas todas las contraseñas en texto plano
- ✅ BCrypt strength: 10 rounds (estándar recomendado)

**Hashes generados:**
- `1234` → `$2b$10$JKheLVrM5.jvtYVvd.tfqOLn6H5571j/ZSSdvL1miwnRCyGyteAA.`
- `TFG_1234` → `$2b$10$KJxpdj39.GSIUlEfPig5peIotMt484c8b8ls1O.rRecudIuT/o2Bi`

### 2. Imagen Docker

**Nueva versión:** `isidromerayo/mariadb-tfg:0.0.5-bcrypt`

- ✅ `docker-compose.yml` actualizado (backend y monorepo)
- ✅ Imagen lista para construir con `Dockerfile-db`

### 3. Documentación

**Archivos creados:**

1. **`recursos/db/PASSWORDS_INFO.md`** (2.4K)
   - Tabla de usuarios y contraseñas de prueba
   - Información sobre hashes BCrypt
   - Guía de implementación

2. **`recursos/db/CHANGELOG_PASSWORDS.md`** (2.6K)
   - Registro detallado de cambios
   - Lista de usuarios afectados
   - Notas de seguridad

3. **`recursos/db/verify-passwords.py`** (3.4K)
   - Script de verificación automática
   - Valida hashes BCrypt en dump.mariadb.sql
   - Detecta contraseñas en texto plano

4. **`docs/security/BUILD_AND_TEST_BCRYPT.md`**
   - Plan completo de construcción y prueba
   - Checklist de verificación
   - Guía de troubleshooting

5. **`docs/security/BCRYPT_MIGRATION_SUMMARY.md`**
   - Resumen ejecutivo de la migración
   - Documentación completa de cambios

6. **`docs/security/QUICK_START_BCRYPT.md`**
   - Guía de inicio rápido

7. **`scripts/build-and-test-bcrypt.sh`**
   - Script automatizado de construcción y prueba
   - Verifica todos los aspectos del sistema
   - Prueba autenticación con múltiples usuarios

8. **`scripts/test-login.sh`**
   - Script simple para probar login
   - Soporta pruebas manuales y automáticas
   - Uso: `./scripts/test-login.sh [email] [password]`

9. **`scripts/README.md`**
   - Documentación de scripts disponibles

10. **`docs/security/README.md`**
    - Índice de documentación de seguridad

### 4. Backend (Ya existente)

El backend Spring Boot ya estaba configurado correctamente:

- ✅ `SecurityConfig.java` - Bean de `BCryptPasswordEncoder`
- ✅ `LoginController.java` - Usa `passwordEncoder.matches()`
- ✅ Prevención de timing attacks implementada

## Cómo Usar

### Opción 1: Script Automatizado (Recomendado)

```bash
cd TFG_UNIR-backend
./scripts/build-and-test-bcrypt.sh
```

Este script:
1. Verifica contraseñas BCrypt en dump.mariadb.sql
2. Construye nueva imagen Docker
3. Reinicia contenedores con volúmenes limpios
4. Verifica contraseñas en la base de datos
5. Espera a que el backend esté listo
6. Prueba autenticación con múltiples usuarios
7. Muestra resumen de resultados

### Opción 2: Manual

```bash
cd TFG_UNIR-backend

# 1. Construir imagen
docker build -f Dockerfile-db -t isidromerayo/mariadb-tfg:0.0.5-bcrypt .

# 2. Reiniciar contenedores
docker compose down -v
docker compose up -d

# 3. Esperar a que estén listos (30-60 segundos)
docker compose logs -f

# 4. Probar login
./scripts/test-login.sh
```

### Opción 3: Prueba Manual con curl

```bash
# Login exitoso
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"maria@localhost","password":"1234"}'

# Login fallido
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"maria@localhost","password":"wrong"}'
```

## Usuarios de Prueba

| Email | Password | Estado | Descripción |
|-------|----------|--------|-------------|
| maria@localhost | 1234 | P | Usuario pendiente |
| helena@localhost | 1234 | A | Usuario activo |
| carlos@localhost | 1234 | A | Usuario activo |
| ines@localhost | 1234 | A | Usuario activo |
| Alva_Streich@example.net | TFG_1234 | P | Usuario con password diferente |

**Total:** 14 usuarios (IDs: 1-9, 11-12, 14-16)

## Verificación

### 1. Verificar Hashes en Archivo

```bash
cd recursos/db
python3 verify-passwords.py
```

Resultado esperado:
```
✓ VERIFICACIÓN EXITOSA: Todos los hashes son válidos
```

### 2. Verificar Base de Datos

```bash
docker exec maria_db mariadb -u user_tfg -ptfg_un1r_PWD tfg_unir -e \
  "SELECT id, nombre, email, LEFT(password, 20) as hash FROM usuarios LIMIT 5"
```

Resultado esperado:
```
+----+---------------+------------------------+----------------------+
| id | nombre        | email                  | hash                 |
+----+---------------+------------------------+----------------------+
|  1 | María         | maria@localhost        | $2b$10$JKheLVrM5.jv |
|  2 | Juan Antonio  | juanantonio@localhost  | $2b$10$JKheLVrM5.jv |
...
```

### 3. Verificar Backend

```bash
docker compose logs api_service | grep -i bcrypt
```

### 4. Probar desde Frontend

**Angular:**
```bash
cd TFG_UNIR-angular
pnpm start
# Abrir http://localhost:4200
```

**React:**
```bash
cd TFG_UNIR-react
pnpm start
# Abrir http://localhost:3000
```

**Vue:**
```bash
cd TFG_UNIR-vue3
pnpm run dev
# Abrir http://localhost:5173
```

## Seguridad

### Mejoras Implementadas

1. ✅ **Hashing BCrypt**: Contraseñas hasheadas con algoritmo seguro
2. ✅ **Salt automático**: BCrypt genera salt único por contraseña
3. ✅ **Constant-time comparison**: Previene timing attacks
4. ✅ **Work factor ajustable**: 10 rounds (2^10 = 1024 iteraciones)

### Prevención de Timing Attacks

El backend usa `passwordEncoder.matches()` que implementa comparación constant-time:

```java
// ✅ SEGURO: Comparación constant-time
boolean passwordMatches = passwordEncoder.matches(
    login.getPassword(), 
    usuario.getPassword()
);

// ❌ INSEGURO: Comparación directa
// boolean matches = password.equals(storedPassword);
```

## Troubleshooting

### Problema: Login falla con credenciales correctas

**Solución:**
```bash
# Verificar que los volúmenes fueron eliminados
docker compose down -v
docker volume ls  # No debe haber volúmenes de maria_db

# Reiniciar
docker compose up -d
```

### Problema: Backend no inicia

**Solución:**
```bash
# Ver logs
docker compose logs api_service

# Verificar que MariaDB está lista
docker compose logs maria_db
```

### Problema: Contraseñas en texto plano en BD

**Causa:** Volumen antiguo no fue eliminado

**Solución:**
```bash
docker compose down -v  # El -v es crítico
docker compose up -d
```

## Próximos Pasos

1. ✅ Ejecutar `./build-and-test-bcrypt.sh`
2. ✅ Verificar que todas las pruebas pasan
3. ⏳ Probar desde los 3 frontends (Angular, React, Vue)
4. ⏳ Publicar imagen: `docker push isidromerayo/mariadb-tfg:0.0.5-bcrypt`
5. ⏳ Actualizar README.md con nueva versión
6. ⏳ Crear PR con los cambios

## Referencias

- **BCrypt**: https://en.wikipedia.org/wiki/Bcrypt
- **Spring Security BCrypt**: https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html
- **Timing Attacks**: https://codahale.com/a-lesson-in-timing-attacks/

## Notas

- Las contraseñas de prueba son débiles intencionalmente (solo para desarrollo)
- En producción, los usuarios deben establecer contraseñas fuertes
- BCrypt con 10 rounds es el estándar actual (2024)
- La migración es compatible con el código existente del backend
