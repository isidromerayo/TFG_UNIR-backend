# Plan de Construcción y Prueba - BCrypt en MariaDB

## Objetivo

Construir una nueva imagen de MariaDB con contraseñas hasheadas con BCrypt y verificar que el flujo de autenticación funciona correctamente desde el frontend hasta el backend.

## Pasos

### 1. Construir Nueva Imagen de MariaDB

```bash
cd TFG_UNIR-backend

# Construir nueva versión con contraseñas BCrypt
docker build -f Dockerfile-db -t isidromerayo/mariadb-tfg:0.0.5-bcrypt .

# Opcional: crear tag latest
docker tag isidromerayo/mariadb-tfg:0.0.5-bcrypt isidromerayo/mariadb-tfg:latest
```

### 2. Actualizar docker-compose.yml

Cambiar la versión de la imagen en `docker-compose.yml`:

```yaml
services:
  maria_db:
    image: "isidromerayo/mariadb-tfg:0.0.5-bcrypt"
```

### 3. Limpiar y Reiniciar Contenedores

```bash
# Detener y eliminar contenedores y volúmenes
docker compose down -v

# Iniciar con la nueva imagen
docker compose up -d

# Ver logs para verificar inicialización
docker compose logs -f maria_db
```

### 4. Verificar Base de Datos

```bash
# Conectar a MariaDB
docker exec -it maria_db mariadb -u user_tfg -ptfg_un1r_PWD tfg_unir

# Verificar que las contraseñas están hasheadas
SELECT id, nombre, email, LEFT(password, 20) as password_hash 
FROM usuarios 
LIMIT 5;
```

Resultado esperado:
```
+----+---------------+------------------------+----------------------+
| id | nombre        | email                  | password_hash        |
+----+---------------+------------------------+----------------------+
|  1 | María         | maria@localhost        | $2b$10$JKheLVrM5.jv |
|  2 | Juan Antonio  | juanantonio@localhost  | $2b$10$JKheLVrM5.jv |
|  3 | Marta         | marta@localhost        | $2b$10$JKheLVrM5.jv |
+----+---------------+------------------------+----------------------+
```

### 5. Verificar Backend

```bash
# Ver logs del backend
docker compose logs -f api_service

# Verificar que el backend está funcionando
curl http://localhost:8080/api/health
```

### 6. Probar Autenticación desde el Frontend

#### Opción A: Con curl

```bash
# Test de login con credenciales correctas
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "maria@localhost",
    "password": "1234"
  }'
```

Respuesta esperada (éxito):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "id": 1,
    "nombre": "María",
    "apellidos": "García Sánchez",
    "email": "maria@localhost",
    "estado": "P"
  }
}
```

```bash
# Test con contraseña incorrecta
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "maria@localhost",
    "password": "wrong_password"
  }'
```

Respuesta esperada (error):
```json
{
  "error": "Credenciales inválidas"
}
```

#### Opción B: Con Frontend Angular

```bash
cd TFG_UNIR-angular

# Instalar dependencias si es necesario
pnpm install

# Iniciar servidor de desarrollo
pnpm start
```

Abrir navegador en `http://localhost:4200` y probar login:
- Email: `maria@localhost`
- Password: `1234`

#### Opción C: Con Frontend React

```bash
cd TFG_UNIR-react

# Instalar dependencias si es necesario
pnpm install

# Iniciar servidor de desarrollo
pnpm start
```

Abrir navegador en `http://localhost:3000` y probar login.

#### Opción D: Con Frontend Vue

```bash
cd TFG_UNIR-vue3

# Instalar dependencias si es necesario
pnpm install

# Iniciar servidor de desarrollo
pnpm run dev
```

Abrir navegador en `http://localhost:5173` y probar login.

### 7. Verificar Logs del Backend

Durante el login, verificar en los logs del backend:

```bash
docker compose logs -f api_service
```

Buscar mensajes como:
- ✅ "Login successful for user: maria@localhost"
- ✅ "BCrypt password verification: true"
- ❌ "Invalid credentials for user: maria@localhost" (si la contraseña es incorrecta)

### 8. Pruebas de Seguridad

#### Test de Timing Attack Prevention

```bash
# Crear script de prueba
cat > test-timing.sh << 'EOF'
#!/bin/bash

echo "Testing timing attack prevention..."

# Usuario válido, contraseña incorrecta
time1=$(curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"maria@localhost","password":"wrong"}' \
  -w "%{time_total}" -o /dev/null -s)

# Usuario inválido
time2=$(curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"noexiste@localhost","password":"wrong"}' \
  -w "%{time_total}" -o /dev/null -s)

echo "Valid user, wrong password: ${time1}s"
echo "Invalid user: ${time2}s"
echo "Difference should be minimal (BCrypt constant-time comparison)"
EOF

chmod +x test-timing.sh
./test-timing.sh
```

### 9. Usuarios de Prueba

| Email | Password | Estado |
|-------|----------|--------|
| maria@localhost | 1234 | P (Pendiente) |
| helena@localhost | 1234 | A (Activo) |
| carlos@localhost | 1234 | A (Activo) |
| Alva_Streich@example.net | TFG_1234 | P (Pendiente) |

## Checklist de Verificación

- [ ] Nueva imagen de MariaDB construida (0.0.5-bcrypt)
- [ ] docker-compose.yml actualizado
- [ ] Contenedores reiniciados con volúmenes limpios
- [ ] Contraseñas en BD están hasheadas (verificado con SQL)
- [ ] Backend inicia correctamente
- [ ] Login exitoso con credenciales correctas (curl)
- [ ] Login falla con credenciales incorrectas (curl)
- [ ] Login funciona desde frontend Angular
- [ ] Login funciona desde frontend React
- [ ] Login funciona desde frontend Vue
- [ ] Logs del backend muestran verificación BCrypt
- [ ] Timing attack prevention verificado

## Troubleshooting

### Error: "Invalid credentials" con contraseña correcta

**Causa**: El backend no está usando BCrypt para verificar contraseñas.

**Solución**: Verificar que `LoginController.java` usa `passwordEncoder.matches()`:
```java
boolean passwordMatches = passwordEncoder.matches(
    login.getPassword(), 
    usuario.getPassword()
);
```

### Error: "Connection refused" al backend

**Causa**: El backend no ha iniciado correctamente.

**Solución**: 
```bash
docker compose logs api_service
# Verificar que no hay errores de conexión a la BD
```

### Error: Contraseñas siguen en texto plano en BD

**Causa**: Volumen antiguo de MariaDB no fue eliminado.

**Solución**:
```bash
docker compose down -v  # El -v es importante
docker volume ls  # Verificar que no quedan volúmenes
docker compose up -d
```

## Publicar Nueva Imagen (Opcional)

Si todo funciona correctamente:

```bash
# Login en Docker Hub
docker login

# Push de la nueva versión
docker push isidromerayo/mariadb-tfg:0.0.5-bcrypt

# Opcional: actualizar latest
docker push isidromerayo/mariadb-tfg:latest
```

## Notas

- Las contraseñas hasheadas están en `recursos/db/dump.mariadb.sql`
- La documentación completa está en `recursos/db/PASSWORDS_INFO.md`
- El script de verificación está en `recursos/db/verify-passwords.py`
- BCrypt usa 10 rounds (factor de trabajo estándar)
