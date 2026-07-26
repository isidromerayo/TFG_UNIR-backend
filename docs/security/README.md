# Documentación de Seguridad

Documentación relacionada con seguridad, autenticación y migración a BCrypt.

## 📚 Índice de Documentos

### Quick Start

- **[QUICK_START_BCRYPT.md](QUICK_START_BCRYPT.md)** - Inicio rápido para migración BCrypt
  - Comando único para ejecutar todo
  - Pruebas básicas
  - Solución rápida de problemas

### Guías Completas

- **[BCRYPT_MIGRATION_SUMMARY.md](BCRYPT_MIGRATION_SUMMARY.md)** - Resumen ejecutivo de la migración
  - Cambios realizados
  - Archivos modificados y creados
  - Usuarios de prueba
  - Checklist de verificación
  - Próximos pasos

- **[BUILD_AND_TEST_BCRYPT.md](BUILD_AND_TEST_BCRYPT.md)** - Guía detallada paso a paso
  - Construcción de imagen Docker
  - Verificación de base de datos
  - Pruebas desde frontend
  - Troubleshooting completo

- **[LESSONS_LEARNED.md](LESSONS_LEARNED.md)** - Lecciones aprendidas durante la migración
  - Problemas encontrados y soluciones
  - Checklist de migración
  - Comandos útiles
  - Recomendaciones para futuros proyectos

### Issues y PRs

- **[PR_SNYK_TIMING_ATTACK.md](PR_SNYK_TIMING_ATTACK.md)** - Pull Request para fix de timing attack
  - Descripción del problema
  - Solución implementada
  - Cambios en el código
  - Testing

- **[SNYK_SECURITY_ISSUE.md](SNYK_SECURITY_ISSUE.md)** - Análisis del issue de Snyk
  - Detalles de la vulnerabilidad
  - Impacto
  - Recomendaciones

## 🚀 Flujo de Trabajo Recomendado

### Para Desarrolladores Nuevos

1. Lee **[QUICK_START_BCRYPT.md](QUICK_START_BCRYPT.md)**
2. Ejecuta `../../scripts/build-and-test-bcrypt.sh`
3. Prueba login con `../../scripts/test-login.sh`

### Para Revisión Completa

1. Lee **[BCRYPT_MIGRATION_SUMMARY.md](BCRYPT_MIGRATION_SUMMARY.md)**
2. Revisa **[BUILD_AND_TEST_BCRYPT.md](BUILD_AND_TEST_BCRYPT.md)**
3. Consulta **[PR_SNYK_TIMING_ATTACK.md](PR_SNYK_TIMING_ATTACK.md)**

### Para Troubleshooting

1. Consulta sección Troubleshooting en **[BCRYPT_MIGRATION_SUMMARY.md](BCRYPT_MIGRATION_SUMMARY.md)**
2. Revisa **[BUILD_AND_TEST_BCRYPT.md](BUILD_AND_TEST_BCRYPT.md)** sección de problemas comunes

## 🔐 Conceptos de Seguridad

### BCrypt

BCrypt es un algoritmo de hashing de contraseñas diseñado para ser lento y resistente a ataques de fuerza bruta.

**Características:**
- Salt automático único por contraseña
- Work factor ajustable (10 rounds = 2^10 iteraciones)
- Comparación constant-time (previene timing attacks)

**Implementación:**
```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hash = encoder.encode("password");
boolean matches = encoder.matches("password", hash);
```

### Timing Attacks

Un timing attack explota diferencias en el tiempo de ejecución para obtener información sobre datos secretos.

**Problema:**
```java
// ❌ VULNERABLE: Comparación directa
if (password.equals(storedPassword)) {
    // La comparación se detiene en el primer carácter diferente
}
```

**Solución:**
```java
// ✅ SEGURO: Comparación constant-time
boolean matches = passwordEncoder.matches(password, storedHash);
// Siempre toma el mismo tiempo, independientemente de dónde difiera
```

### Hashing vs Encriptación

| Aspecto | Hashing | Encriptación |
|---------|---------|--------------|
| Reversible | ❌ No | ✅ Sí |
| Uso | Contraseñas | Datos sensibles |
| Algoritmos | BCrypt, Argon2 | AES, RSA |
| Verificación | Comparar hashes | Desencriptar |

## 📊 Estado Actual

### ✅ Implementado

- [x] BCrypt en backend (SecurityConfig.java)
- [x] Comparación constant-time (LoginController.java)
- [x] Contraseñas hasheadas en BD (dump.mariadb.sql)
- [x] Scripts de testing automatizados
- [x] Documentación completa

### ⏳ Pendiente

- [ ] Probar desde los 3 frontends
- [ ] Publicar imagen Docker nueva
- [ ] Actualizar README principal
- [ ] Crear PR con cambios

## 🔗 Referencias Externas

- [BCrypt Wikipedia](https://en.wikipedia.org/wiki/Bcrypt)
- [Spring Security Password Storage](https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html)
- [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html)
- [A Lesson in Timing Attacks](https://codahale.com/a-lesson-in-timing-attacks/)

## 📁 Archivos Relacionados

### Código Fuente
- `src/main/java/eu/estilolibre/tfgunir/backend/config/SecurityConfig.java`
- `src/main/java/eu/estilolibre/tfgunir/backend/controller/LoginController.java`

### Base de Datos
- `../../recursos/db/dump.mariadb.sql` - Datos con contraseñas hasheadas
- `../../recursos/db/create.mariadb.sql` - Esquema de BD
- `../../recursos/db/PASSWORDS_INFO.md` - Info de contraseñas
- `../../recursos/db/CHANGELOG_PASSWORDS.md` - Changelog
- `../../recursos/db/verify-passwords.py` - Script de verificación

### Scripts
- `../../scripts/build-and-test-bcrypt.sh` - Build y test automatizado
- `../../scripts/test-login.sh` - Pruebas de login
- `../../scripts/podman-pod.sh` - Gestión de contenedores

### Configuración
- `../../docker-compose.yml` - Configuración Docker
- `../../Dockerfile-db-postgresql` - Imagen PostgreSQL

## 🆘 Soporte

Si encuentras problemas:

1. Revisa la sección Troubleshooting en los documentos
2. Verifica logs: `docker compose logs -f`
3. Ejecuta script de verificación: `../../recursos/db/verify-passwords.py`
4. Consulta el README de scripts: `../../scripts/README.md`

## 📝 Notas

- Las contraseñas de prueba son débiles intencionalmente (solo desarrollo)
- BCrypt con 10 rounds es el estándar actual (2024)
- En producción, usuarios deben establecer contraseñas fuertes
- La migración es compatible con código existente del backend
