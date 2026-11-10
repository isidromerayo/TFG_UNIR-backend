# Informe de Security Review — TFG UNIR Backend

| Campo | Valor |
|---|---|
| **Proyecto** | `TFG_UNIR-backend` (`eu.estilolibre.tfgunir.backend`) |
| **Fecha** | 2026-11-06 |
| **Herramienta** | skill `security-review` (revisión estática asistida por IA: data-flow + grep de patrones) |
| **Alcance** | `src/main`, `src/test`, `pom.xml`, `application.properties`, `Dockerfile`, `docker-compose.yml`, `.github/workflows/`, `scripts/`, `.env.example` |
| **Lenguajes / frameworks** | Java 21 · Spring Boot 4.1.2 · Spring Security 7.1.2 · Spring Data REST · JJWT 0.13.0 · Jackson 3.1.7 · Springdoc 3.2.0 |
| **Estado** | 🟢 CRITICAL/HIGH de autenticación y secretos en `main` (PRs #183, #184, #185) · pendientes H-3 y perfiles de producción |

```
┌────────────────────────────────────────────────┐
│           FINDINGS SUMMARY                     │
├──────────────┬─────────────────────────────────┤
│ 🔴 CRITICAL  │  3 findings                     │
│ 🟠 HIGH      │  3 findings                     │
│ 🟡 MEDIUM    │  4 findings                     │
│ 🔵 LOW       │  4 findings                     │
│ ⚪ INFO      │  4 findings                     │
├──────────────┼─────────────────────────────────┤
│ TOTAL        │  18 findings                    │
└──────────────┴─────────────────────────────────┘

Dependency Audit: 0 vulnerabilidades conocidas detectadas
Secrets Scan:     0 credenciales reales filtradas (1 default reutilizable)
```

---

## 1. Resumen ejecutivo

La aplicación **no tiene control de acceso efectivo**. No es un endpoint mal protegido:
`WebSecurityConfig` declara `anyRequest().permitAll()` y **no existe ningún filtro que valide
el JWT**. El login emite tokens que nadie comprueba (`TokenService.leerToken()` solo se invoca
desde `TokenServiceTest`). A eso se suma que Spring Data REST publica endpoints de **escritura**
sobre cursos, categorías y valoraciones, accesibles de forma anónima.

La base criptográfica, en cambio, es correcta:

- BCrypt (work factor 10) para contraseñas.
- `Usuario.password` con `@JsonIgnore`; los DTO de respuesta (`UsuarioResponse`, `AuthResponse`)
  no incluyen el hash.
- `.env` no está versionado ni aparece en el historial de git.
- `static/index.html` construye el DOM con `textContent` (sin XSS).
- Sin secretos reales en el código.

El trabajo pendiente es la **capa de autorización**, no la criptografía.

---

## 2. Hallazgos CRITICAL

### C-1. Broken Access Control: la API entera está abierta
**Severidad:** 🔴 CRITICAL · **Confianza:** HIGH
**Ubicación:** `src/main/java/eu/estilolibre/tfgunir/backend/security/WebSecurityConfig.java:26-28`

```java
.csrf(AbstractHttpConfigurer::disable)
.cors(withDefaults())
.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
```

**Riesgo.** No hay reglas de autorización y no hay filtro de autenticación:

```
$ grep -rn "OncePerRequestFilter\|leerToken" src/main/java
security/TokenService.java:38:    public String leerToken(String tokenReal) {
# única aparición: definición, sin llamadas desde producción
```

Cualquier cliente puede invocar todos los endpoints sin cabecera `Authorization`. El token
emitido por `/api/auth` es decorativo.

**Remediación.** Filtro `OncePerRequestFilter` que valide la firma y una política
*deny-by-default* con lista blanca explícita.

---

### C-2. Spring Data REST expone CRUD de escritura sin autenticación
**Severidad:** 🔴 CRITICAL · **Confianza:** HIGH
**Ubicación:**
- `repository/CategoriaRepository.java:12`
- `repository/CursoRepository.java:11`
- `repository/ValoracionRepository.java:14`

```java
@RepositoryRestResource(collectionResourceRel = "cursos", path = "cursos")
public interface CursoRepository extends PagingAndSortingRepository<Curso, Long>, CrudRepository<Curso, Long> {
```

**Riesgo.** Spring Data REST publica por defecto `POST`, `PUT`, `PATCH` y `DELETE` para
repositorios `CrudRepository`. Combinado con C-1:

```bash
curl -X DELETE http://localhost:8080/api/cursos/1                       # destruir catálogo
curl -X POST  http://localhost:8080/api/valoraciones -d '{...}'         # reseñas falsas
curl -X PATCH http://localhost:8080/api/cursos/1 -d '{"precio":0}'     # manipular precios
```

`UsuarioRepository` sí está protegido con `@RepositoryRestResource(exported = false)`; los otros
tres no.

**Remediación.** Exigir autenticación para métodos no idempotentes sobre `/api/**` y/o marcar
los mutadores como `@RestResource(exported = false)`.

---

### C-3. IDOR/BOLA en los cursos comprados del usuario
**Severidad:** 🔴 CRITICAL · **Confianza:** HIGH
**Ubicación:** `controller/UsuarioController.java:70-86`

```java
@GetMapping("/{id}/cursos")
public ResponseEntity<List<CursoResponse>> obtenerCursosComprados(@PathVariable long id) { ... }

@PostMapping("/{id}/misCursosComprados")
public ResponseEntity<Void> agregarMisCursos(@PathVariable long id, @RequestBody String body) { ... }
```

**Riesgo.** El `id` procede del path y nunca se contrasta con el usuario autenticado:

- `GET /api/usuarios/5/cursos` → historial de compras de cualquier usuario (ids secuenciales,
  enumerable en segundos).
- `POST /api/usuarios/5/misCursosComprados` → **cualquiera se auto-asigna todos los cursos**
  de la plataforma en la cuenta que elija.

**Remediación.** Derivar el id del token (no del path) o compararlo con el del usuario
autenticado y responder `403`.

---

## 3. Hallazgos HIGH

### H-1. Secreto JWT hardcodeado con fallback de producción
**Confianza:** HIGH · **Estado:** ✅ corregido y en `main` (PR #184)
**Ubicación:** `src/main/resources/application.properties:55`, `.env.example:12`

```properties
jwt.secret=${JWT_SECRET:813cef5f-3459-4618-87a6-a69e2a1296d4_mySecretKey_mySecretKey_CHANGE_IN_PRODUCTION}
jwt.expiration=3600000
```

El valor por defecto está en el repositorio y `docker-compose.yml` **no** propaga `JWT_SECRET`
al servicio `api_service`, así que un despliegue con compose usa el secreto público. Hoy queda
enmascarado por C-1; al activar la validación se convierte en *account takeover* total
(firma de tokens arbitrarios).

### H-2. Sin rate limiting ni política de contraseñas en el login
**Confianza:** HIGH · **Estado:** ✅ corregido el 2026-11-10 (`LoginRateLimiter`, política 8-72, hash señuelo)
**Ubicación:** `controller/LoginController.java:60-86`, `dto/UsuarioRegistroRequest.java:25`

- `/api/auth` acepta intentos ilimitados: fuerza bruta y *credential stuffing* sin fricción.
- Contraseñas de **4 caracteres** como mínimo.
- Enumeración por temporización: `passwordEncoder.matches()` solo se ejecuta cuando el email
  existe (`LoginController:64-69`), de modo que la latencia distingue cuentas reales.

### H-3. Actuator expuesto públicamente
**Confianza:** HIGH
**Ubicación:** `application.properties:83-87` + `permitAll()`

```properties
management.endpoints.web.exposure.include=health,info,metrics
management.info.env.enabled=true
```

`/actuator/metrics` y `/actuator/info` (enlazados desde `static/index.html`) publican versión de
JVM, commit, branch y métricas internas de JVM/DB. Reconocimiento gratuito para un atacante.

---

## 4. Hallazgos MEDIUM

| # | Hallazgo | Ubicación | Detalle |
|---|---|---|---|
| M-1 | Datos sensibles en logs | `application.properties:62-65` | `show-sql=true` + `BasicBinder=TRACE` vuelca parámetros enlazados: emails y hashes BCrypt en cada `INSERT`/`UPDATE`. |
| M-2 | `ddl-auto=update` en producción | `application.properties:23` | Hibernate altera el esquema en runtime. Usar Flyway/Liquibase + `validate`. |
| M-3 | Secreto por defecto de BD en compose | `docker-compose.yml:44,62` | `${POSTGRES_PASSWORD:-tfg_un1r_PWD}` arranca con credenciales conocidas si falta `.env`. ✅ PR #184 |
| M-4 | Password de BD hardcodeada como default | `application.properties:8` | `tfg_un1r_PWD` versionada; no debería ser el fallback del artefacto. ✅ PR #184 |

## 5. Hallazgos LOW

| # | Hallazgo | Ubicación |
|---|---|---|
| L-1 | Enumeración de emails por distinta latencia (BCrypt solo si existe) | `LoginController.java:64-69` — ✅ corregido el 2026-11-10 (hash señuelo) |
| L-2 | CORS duplicado y divergente (`PATCH` ausente en `WebConfig`) | `security/WebConfig.java:17` vs `WebSecurityConfig.java:40` |
| L-3 | Swagger UI / OpenAPI público | `/swagger-ui/**`, `/v3/api-docs` |
| L-4 | Sin cabeceras de seguridad (HSTS, CSP, `X-Content-Type-Options`) | global |

## 6. Observaciones INFO

- **CSRF deshabilitado**: aceptable mientras la API sea *stateless* y el token viaje en cabecera.
  Si se adopta cookie `HttpOnly`, hay que reactivarlo.
- `spring.jackson.deserialization.fail-on-unknown-properties=false`
  (`application.properties:29`): sin riesgo de *mass assignment* porque la entrada pasa por DTOs
  explícitos (`UsuarioRegistroRequest`, `LoginRequest`), no por entidades.
- `Usuario.password` con `@JsonIgnore` y `UsuarioResponse` sin el campo → no hay fuga de hashes.
- `static/index.html` usa `textContent` y `innerHTML = ''` (nunca con datos) → sin XSS.
- JWT sin `iss`/`aud` ni revocación; expiración de 1 h razonable.
- `parseCursoIds` puede lanzar `NumberFormatException` con un id desmesurado → `500` genérico,
  sin fuga de traza al cliente.

---

## 7. Auditoría de dependencias

Sin hallazgos accionables. El `pom.xml` está en el estado ya documentado en `AGENTS.md`:

| Dependencia | Versión | Nota |
|---|---|---|
| Spring Boot | 4.1.2 | gestiona Tomcat 11.0.27 y Jackson 3.1.7 |
| swagger-ui | 5.34.1 | DOMPurify 3.4.17 (parchea CVE-2026-116802/803/804) |
| JJWT | 0.13.0 | API/impl/jackson alineadas |
| PostgreSQL driver | gestionado | — |

Los gates `security-osv.yml`, `security-trivy.yml` y `owasp-dependency-check-maven.yml` cubren
esto con más precisión que un análisis estático manual. Ver
`docs/security/accepted-advisories.md` (0 advisories aceptados).

## 8. Escaneo de secretos

- `.env` **no versionado** (`.gitignore:26`) y `git log --all -- .env` vacío → nunca se commiteó.
- `.env.example` versionado **con valores funcionales** (`tfg_un1r_PWD`, el JWT por defecto).
  No es una fuga real, pero convierte el ejemplo en credencial pública.
- `full_app.log` / `retry_app.log` están ignorados por el `*.log` del `.gitignore` **global**
  (`~/.gitignore`), no del repo: en otra máquina un `git add .` los subiría. Añadir `*.log` al
  `.gitignore` del proyecto.

---

## 9. Plan de remediación

| Rama | Contenido | Estado |
|---|---|---|
| `security/enable-jwt-authorization` | C-1, C-2, C-3 (parches 1-3) + tests de autorización | ✅ mergeado en `main` el 2026-11-06 (PR #183) |
| `security/remove-hardcoded-secrets` | H-1, M-3, M-4 (parches 4 y 6) | ✅ mergeado en `main` el 2026-11-10 (PR #184) |
| `security/login-hardening` | H-2 (rate limiting + política de contraseñas) y L-1 (timing) | ✅ mergeado en `main` el 2026-11-10 (PR #185) |
| — | H-3 (Actuator), M-1, M-2 (perfiles de producción) | ⬜ pendiente |
| — | L-2…L-4 | ⬜ backlog |

### Cambios aplicados en `security/enable-jwt-authorization`

1. **`security/JwtAuthenticationFilter.java`** (nuevo): valida la firma con `TokenService`,
   comprueba que el usuario existe y está activo (`estado = "A"`), y publica la autenticación con
   principal = email y `ROLE_USER`. No es un `@Component`: lo instancia `WebSecurityConfig` y lo
   inserta solo en la cadena de seguridad, para que el contenedor no lo registre además como filtro
   de servlet y lo ejecute dos veces.
2. **`security/RestSecurityHandlers.java`** (nuevo): respuestas `401`/`403` en formato `ApiError`
   con mensajes constantes (sin reflejar el recurso solicitado).
3. **`security/WebSecurityConfig.java`**: sesión *stateless*, entrada/salida JSON para 401/403 y
   política *deny-by-default*:

   | Ruta | Acceso |
   |---|---|
   | `POST /api/auth`, `POST /api/usuarios` | público (login y registro) |
   | `GET /api/cursos/**`, `/api/categorias/**`, `/api/valoraciones/**` | público (catálogo) |
   | `GET /actuator/health`, `/swagger-ui/**`, `/v3/api-docs/**`, `/`, `/index.html`, `/error` | público |
   | `GET /actuator/**` (metrics, info) | autenticado |
   | `POST/PUT/PATCH/DELETE /api/**` | autenticado |
   | `GET /api/usuarios/{id}/cursos`, `POST /api/usuarios/{id}/misCursosComprados` | autenticado **y** propietario |

3. **`UsuarioController.java`**: las dos operaciones sobre cursos comprados verifican que el `id`
   del path coincide con el del usuario del token; en caso contrario `403`
   (`AccessDeniedException`). Se devuelve `403` también para ids inexistentes de terceros, para
   no permitir enumeración.
4. **`GlobalExceptionHandler`**: `AccessDeniedException` → `403 FORBIDDEN` en formato `ApiError`.
5. **Tests**:
   - `SecurityAuthorizationIT` (nuevo, 17 casos): catálogo público, 401 anónimo en escritura y en
     datos de usuario, 403 entre usuarios, token manipulado / firmado con otro secreto, login
     público, `actuator/health` público y `actuator/metrics` protegido.
   - `UsuarioControllerIT`: adaptado para autenticarse con tokens reales (helena/carlos).
   - `UsuarioControllerTest`: cubre propietario, tercero y ausencia de autenticación.
6. **Documentación**: `README.md` (sección *Política de acceso* y *Gaps conocidos*),
   `docs/security/README.md` (índice y estado) y este informe.

### Endpoints que cambian de comportamiento

| Endpoint | Antes | Ahora |
|---|---|---|
| `GET /api/usuarios/{id}/cursos` | 200 para cualquiera | 200 solo el propietario; 401 anónimo; 403 otro usuario |
| `POST /api/usuarios/{id}/misCursosComprados` | 201 para cualquiera | 201 solo el propietario; 401 anónimo; 403 otro usuario |
| `POST/PUT/PATCH/DELETE /api/{cursos,categorias,valoraciones}` | 2xx anónimo | 401 anónimo |
| `GET /actuator/metrics`, `/actuator/info` | 200 anónimo | 401 anónimo |

> Los frontends (Angular/React/Vue3) ya envían el `id` del usuario autenticado en estas rutas, por
> lo que el cambio es transparente para ellos; solo dejan de funcionar las llamadas anónimas o
> cruzadas.

---

## 10. Cobertura y método

```
Ficheros revisados:  ~65 (50 Java, 4 properties, 1 HTML, pom.xml,
                     Dockerfile, Dockerfile-db-postgresql, docker-compose.yml,
                     7 workflows, scripts)
Líneas analizadas:   3.336 (src) + configuración
Método:              revisión manual guiada por data-flow + grep de patrones
                     y secretos + self-verification por hallazgo
Ejecución:           ninguna (análisis estático, no DAST)
```

**Limitaciones.** Un análisis estático no sustituye a pruebas dinámicas (DAST) ni a los gates de
CI (`OSV`, `Trivy`, `OWASP Dependency-Check`). No se ha ejecutado la aplicación durante el escaneo.

---

**Generado por:** skill `security-review` · **Revisión:** 2026-11-06

---

## 11. Verificación de la remediación

Ejecutado sobre `security/enable-jwt-authorization` (2026-11-06):

| Comprobación | Comando | Resultado |
|---|---|---|
| Compilación | `./mvnw clean compile` | ✅ |
| Tests unitarios | `./mvnw test` | ✅ 54 tests, 0 fallos |
| Tests de integración | `./mvnw -Pintegration-tests verify` | ✅ 41 IT, 0 fallos |
| Análisis estático | `./mvnw compile spotbugs:check` | ✅ 0 bugs |

Estado final de `main` tras integrar PRs #183, #184 y #185 (2026-11-10): **72 tests unitarios**,
**44 tests de integración**, **0 bugs** de SpotBugs, y CI en verde (Maven, Sonar, OSV, Trivy).

TDD: `SecurityAuthorizationIT` se escribió **antes** del código y se verificó en rojo
(13 de 17 casos fallando, p. ej. `anonimoNoPuedeCrearCursos` recibía `201`), y después en verde.

### Riesgo residual tras esta rama

| Hallazgo | Estado (2026-11-10) |
|---|---|
| C-1, C-2, C-3 | ✅ remediados y en `main` (PR #183) |
| H-1 (secreto JWT por defecto) | ✅ remediado y en `main` (PR #184) |
| H-2 (rate limiting / contraseñas) | ✅ remediado el 2026-11-10 |
| H-3 (Actuator) | 🟡 mitigado: `metrics`/`info` exigen token |
| M-3, M-4 (password de BD) | ✅ remediados y en `main` (PR #184) |
| L-1 (timing) | ✅ remediado el 2026-11-10 (hash señuelo) |
| M-1, M-2, L-2…L-4, INFO | ⬜ backlog |

> La API ya no acepta escrituras ni datos de usuario sin token y los secretos no tienen valores por
> defecto: sin `JWT_SECRET` la aplicación **no arranca** (PR #184). El riesgo que queda es de
> despliegue: hay que provisionar `JWT_SECRET` y `SPRING_DATASOURCE_PASSWORD` en el entorno, y en
> cada release regenerar el JWT si se sospecha que pudo filtrarse.
