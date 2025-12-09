# Quick Start - BCrypt Migration

## TL;DR

### Con Docker
```bash
cd TFG_UNIR-backend
./scripts/build-and-test-bcrypt.sh
```

### Con Podman
```bash
cd TFG_UNIR-backend

# 1. Construir imagen
podman build -f Dockerfile-db -t isidromerayo/mariadb-tfg:0.0.5-bcrypt .

# 2. Actualizar scripts/podman-pod.sh (cambiar versión de imagen)
# MARIA_DB_IMAGE="isidromerayo/mariadb-tfg:0.0.5-bcrypt"

# 3. Iniciar
./scripts/podman-pod.sh start

# 4. Probar
./scripts/test-login.sh
```

⚠️ **Usuarios de Podman**: Ver [../PODMAN_GUIDE.md](../PODMAN_GUIDE.md) para más detalles.

## ¿Qué hace el script?

1. ✅ Verifica contraseñas BCrypt
2. ✅ Construye imagen Docker nueva
3. ✅ Reinicia contenedores
4. ✅ Verifica base de datos
5. ✅ Prueba autenticación
6. ✅ Muestra resultados

## Resultado Esperado

```
========================================
✓ TODAS LAS PRUEBAS PASARON
========================================

Imagen construida: isidromerayo/mariadb-tfg:0.0.5-bcrypt
Contraseñas BCrypt: ✓ Verificadas
Base de datos: ✓ Funcionando
Backend: ✓ Funcionando
Autenticación: ✓ Funcionando

Usuarios de prueba disponibles:
  - maria@localhost / 1234 (Pendiente)
  - helena@localhost / 1234 (Activo)
  - carlos@localhost / 1234 (Activo)
  - Alva_Streich@example.net / TFG_1234 (Pendiente)
```

## Probar Login Manualmente

```bash
# Probar todos los usuarios
./scripts/test-login.sh

# Probar un usuario específico
./scripts/test-login.sh maria@localhost 1234
```

## Probar desde Frontend

### Angular
```bash
cd ../TFG_UNIR-angular
pnpm start
# http://localhost:4200
```

### React
```bash
cd ../TFG_UNIR-react
pnpm start
# http://localhost:3000
```

### Vue
```bash
cd ../TFG_UNIR-vue3
pnpm run dev
# http://localhost:5173
```

**Credenciales:** maria@localhost / 1234

## Si Algo Falla

### Con Docker
```bash
# Reiniciar todo desde cero
docker compose down -v
docker compose up -d

# Ver logs
docker compose logs -f
```

### Con Podman
```bash
# Reiniciar todo desde cero
./scripts/podman-pod.sh stop
podman volume rm tfg_unir-backend_data
./scripts/podman-pod.sh start

# Ver logs
./scripts/podman-pod.sh logs api
./scripts/podman-pod.sh logs db
```

### Problema: DNS con Podman
Si ves errores como `UnknownHostException: maria_db`, usa el script de Podman Pod:
```bash
./scripts/podman-pod.sh start
```
Ver [../PODMAN_GUIDE.md](../PODMAN_GUIDE.md) para más detalles.

## Documentación Completa

- `docs/security/BCRYPT_MIGRATION_SUMMARY.md` - Resumen completo
- `docs/security/BUILD_AND_TEST_BCRYPT.md` - Guía detallada
- `docs/PODMAN_GUIDE.md` - Guía de uso con Podman
- `recursos/db/PASSWORDS_INFO.md` - Info de contraseñas
- `recursos/db/CHANGELOG_PASSWORDS.md` - Changelog
- `docs/security/README.md` - Índice de documentación de seguridad

## Archivos Modificados

- ✅ `recursos/db/dump.mariadb.sql` - Contraseñas hasheadas
- ✅ `docker-compose.yml` - Nueva versión de imagen
- ✅ Scripts de prueba creados

## ¿Necesitas Ayuda?

1. Lee `BCRYPT_MIGRATION_SUMMARY.md`
2. Revisa la sección Troubleshooting
3. Verifica logs: `docker compose logs`
