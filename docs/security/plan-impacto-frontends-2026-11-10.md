# Plan de impacto en los frontends — cambios de seguridad del backend

| Campo | Valor |
|---|---|
| **Origen** | PRs #183, #184 y #185 del backend, mergeadas en `main` el 2026-11-06 / 2026-11-10 |
| **Informe base** | [`informe-security-review-2026-11-06.md`](informe-security-review-2026-11-06.md) |
| **Repos afectados** | `TFG_UNIR-angular`, `TFG_UNIR-react`, `TFG_UNIR-vue3` |
| **Fecha del análisis** | 2026-11-10 |

---

## 1. Qué cambia en el contrato de la API

| Endpoint | Antes | Ahora | Afecta a |
|---|---|---|---|
| `POST /api/auth` | 200/401 sin límite | 200/400/401 + **429** con `Retry-After` tras 5 fallos por IP en 15 min | los tres (login) |
| `POST /api/usuarios` | contraseña ≥ 4 | contraseña **8-72** → `400 VALIDATION_ERROR` | los tres (registro) |
| `GET /api/usuarios/{id}/cursos` | 200 anónimo | **401** sin token · **403** si el `id` no es el del token | Angular, React |
| `POST /api/usuarios/{id}/misCursosComprados` | 201 anónimo | **401** sin token · **403** si el `id` no es el del token | Angular |
| `POST/PUT/PATCH/DELETE /api/cursos|categorias|valoraciones` | 2xx anónimo | **401** | ninguno hoy (solo lectura) |
| `GET /api/usuarios` | 404 | 401 anónimo · 404 con token | ninguno |

Notas de contrato:

- El token sigue viajando **en el body** de `/api/auth` (`AuthResponse.token`) y se envía en
  `Authorization: Bearer <jwt>`. El backend **no** devuelve la cabecera `Authorization` en la
  respuesta de login (sigue en `exposedHeaders` por compatibilidad).
- Los errores mantienen el formato `ApiError`: `{ status, code, message, errors }`; los nuevos
  `code` son `UNAUTHORIZED`, `FORBIDDEN` y `TOO_MANY_REQUESTS`.

---

## 2. Estado actual detectado en cada frontend

Ninguno de los tres adjunta hoy la cabecera `Authorization`. El token se guarda en `localStorage`
pero **nunca se envía**, así que los endpoints de usuario devuelven `401` desde el merge de #183.

### 2.1 Angular

| Punto | Fichero | Situación |
|---|---|---|
| Sin interceptor HTTP | `src/app/` | grep de `HttpInterceptor` sin resultados |
| Compra de cursos | `services/carrito.service.ts:66-68` | `new HttpHeaders().set('Content-Type','text/uri-list')` — sin token → **401** |
| Mis cursos | `services/usuario.service.ts:19` | `GET /usuarios/${id}/cursos` sin token → **401** |
| Origen del token | `services/auth.service.ts:20-30` | lo lee de la **cabecera de respuesta** (`headers.get('Authorization')`), que no existe → guarda `"undefined"`; se sobrescribe después en `acceso.component.ts:33` con el `token` del body ✅ |
| Validación de registro | `components/registro/registro.component.html:34-38` | `minlength="4"` y mensaje "al menos 4" |
| Manejo 401/403/429 | — | no existe |

### 2.2 React

| Punto | Fichero | Situación |
|---|---|---|
| Sin interceptor de auth | `utils/api.ts` | axios con `Accept`/`Content-Type`, sin `Authorization` |
| Mis cursos | `pages/mis-cursos.tsx:17` | `api.get('/usuarios/${usuario.id}/cursos')` sin token; comprueba `getToken()` pero no lo usa → **401** |
| Login | `pages/acceso.tsx:35-41` | guarda `response.data.token` ✅; mensaje de error genérico para 401 **y 429** |
| Registro | `pages/registro.tsx:28` | Yup `password.min(4)` |
| 429 | `utils/api.ts:27` | ya reintenta 429/5xx **solo en GET** (correcto para login, que es POST) |

### 2.3 Vue3

| Punto | Fichero | Situación |
|---|---|---|
| Sin interceptor de auth | `src/utils/api.ts` | igual que React |
| Mis cursos / compra | `MisCursosComponent.vue`, `CarritoComponent.vue` | **no** llaman a `/api/usuarios/...` (carrito en store local); `CarritoComponent` importa `getToken` sin usarlo |
| Login | `src/components/AccesoComponent.vue:39-50` | guarda `response.data.token` ✅; mensaje genérico |
| Registro | `src/components/RegistroComponent.vue:77` | Yup `password.min(4)` |

---

## 3. Prioridad del impacto

| Prio | Impacto | Repos |
|---|---|---|
| **P0** | Comprar cursos y "mis cursos" fallan con `401` (flujo roto) | Angular, React |
| **P1** | Registro con contraseña de 5-7 caracteres: lo valida el cliente pero el backend responde `400` | los tres |
| **P2** | Login bloqueado responde `429` y la UI dice "revise usuario/contraseña" | los tres |
| **P3** | `401/403` no limpian sesión ni redirigen: el usuario queda con la UI "logeada" y en error | Angular, React |
| **P4** | Escrituras de catálogo y `GET /api/usuarios` (no usadas hoy) | ninguno |

> ⚠️ Los entornos de desarrollo de los frontends apuntan a `http://localhost:8080`, así que el
> P0 ya está activo en cuanto se ejecuta el backend actualizado.

---

## 4. Trabajo por repositorio

### 4.1 Común a los tres frontends

1. **Interceptor de request** que añada `Authorization: Bearer <token>` cuando exista token.
   - No añadirlo en `POST /api/auth`, `POST /api/usuarios` ni en las rutas públicas de catálogo
     (es inocuo, pero evita ruido y preflight innecesario).
2. **Interceptor de response**:
   - `401` → limpiar token/usuario + redirigir a `/acceso` con mensaje "sesión caducada".
   - `403` → mensaje "sin permisos" (no cerrar sesión: indica que el `id` no casa con el token).
   - `429` → leer `Retry-After` (segundos) y mostrar "espera X" en lugar del error genérico de
     credenciales. Sin reintento automático en `POST`.
3. **Validación de registro**: `min 8`, `max 72`, mensaje alineado con el backend
   ("entre 8 y 72 caracteres"). Máximo 72 = límite de BCrypt.
4. **Tests**: cubrir el interceptor (añade cabecera, no la añade sin token, limpia sesión en 401),
   el mensaje de 429 y los límites de contraseña. Sin `sleep` en tests: usar `Retry-After` mockeado.

### 4.2 Angular — rama `fix/auth-token-and-password-policy`

- Añadir `provideHttpClient(withInterceptors([authInterceptor]))` en `app.config.ts` y crear
  `interceptors/auth.interceptor.ts` (funcional, Angular 21).
- Corregir `auth.service.ts`: tomar `token` de `response.body.token`, no de la cabecera (la lectura
  actual guarda `"undefined"` y solo se salva por la llamada posterior a `register()`).
- `registro.component.html`: `minlength="8"` + `maxlength="72"` y mensaje actualizado.
- Tests: `services/auth.service.spec.ts`, `services/carrito.service.spec.ts`,
  `services/usuario.service.spec.ts` (añadir `expectOne(...).request.headers.get('Authorization')`),
  `components/registro/registro.component.spec.ts` + nuevo `auth.interceptor.spec.ts`.
- Gate: `pnpm run test-headless && pnpm run build && pnpm audit`.

### 4.3 React — rama `fix/auth-token-and-password-policy`

- `utils/api.ts`: request interceptor que lea el token (`utils/session.ts` → `getToken()`) y añada
  la cabecera; response interceptor para 401 (limpiar sesión + `router.push('/acceso')` vía evento o
  helper) y 429 (`Retry-After`).
- `pages/mis-cursos.tsx`: dejar de comprobar `getToken()` sin enviarlo; el interceptor lo cubre.
- `pages/registro.tsx`: `Yup.string().required().min(8).max(72)`.
- `pages/acceso.tsx`: mensaje específico para `429` y para `401`.
- Tests: `__tests__/pages/registro.spec.tsx` (mensaje de longitud), `__tests__/pages/mis-cursos.spec.tsx`,
  `__tests__/pages/acceso.spec.tsx` (+429), `__tests__/utils/api.spec.ts` (nuevo).
- Gate: `pnpm lint && pnpm test-headless && pnpm cypress:component && pnpm build`.

### 4.4 Vue3 — rama `fix/auth-token-and-password-policy`

- `src/utils/api.ts`: mismo interceptor de request/response (reutilizar `services/session.ts`).
- `RegistroComponent.vue`: `Yup.string().required().min(8).max(72)`.
- `AccesoComponent.vue`: mensaje para `429`.
- `CarritoComponent.vue` / `MisCursosComponent.vue`: cuando se conecten a `/api/usuarios/...`,
  ya irán autenticados por el interceptor; dejar un `TODO` si siguen en local.
- Tests: `tests/unit/componentes/RegistroComponent.spec.ts`, `AccesoComponent.spec.ts`,
  `tests/unit/utils/api.spec.ts` (nuevo).
- Gate: `pnpm lint && pnpm type-check && pnpm test-headless && pnpm cypress:component && pnpm build`.

---

## 5. Orden de entrega

1. **Angular y React (P0)** primero: son los que hoy rompen funcionalidad.
2. **Vue3** después (P1/P2): no llama a los endpoints de usuario todavía.
3. Una rama y una PR por repo, con Conventional Commits (`fix(auth): …`), sin tocar `main`.
4. Desplegar frontends y backend **juntos**: los cambios son compatibles hacia atrás (enviar un token
   que antes se ignoraba no rompe nada), pero el `400` de contraseña y el `429` de login solo se
   ven con el backend nuevo.

## 6. Verificación manual (por frontend)

```bash
# 1. Login correcto y token en localStorage
curl -s -X POST localhost:8080/api/auth -H 'Content-Type: application/json' \
  -d '{"email":"helena@localhost","password":"1234"}' | jq -r .token

# 2. Sin token → 401
curl -s -o /dev/null -w '%{http_code}\n' localhost:8080/api/usuarios/5/cursos

# 3. Con token propio → 200; con token de otro usuario → 403
curl -s -o /dev/null -w '%{http_code}\n' -H "Authorization: Bearer $TOKEN" localhost:8080/api/usuarios/5/cursos
curl -s -o /dev/null -w '%{http_code}\n' -H "Authorization: Bearer $TOKEN" localhost:8080/api/usuarios/6/cursos

# 4. Contraseña corta → 400; bloqueo de login → 429 con Retry-After
curl -si -X POST localhost:8080/api/auth -H 'Content-Type: application/json' \
  -d '{"email":"nadie@example.com","password":"x"}' | head -1
```

En UI, por repo: login → comprar un curso → "mis cursos" (debe listar sin `401`), registro con 7
caracteres (mensaje del cliente, sin llamada), 6 intentos de login fallidos (mensaje de espera).

## 7. Decisiones y riesgos

| Tema | Decisión |
|---|---|
| Origen del token | Se mantiene el **body** (`AuthResponse.token`); no se añade `Authorization` a la respuesta del backend |
| Rate limit por IP | Un NAT compartido puede bloquear a varios usuarios; el mensaje de `429` debe mostrar `Retry-After` |
| Rate limit en memoria | Con varias réplicas el límite deja de ser global → mover a Redis (impacta a la semántica del `429`) |
| `403` vs `404` | El backend responde `403` para ids ajenos o inexistentes (anti-enumeración): el frontend no debe interpretarlo como "usuario no encontrado" |
| Usuarios de prueba | Los usuarios con contraseña `1234` siguen pudiendo **entrar**; la política 8-72 solo aplica al alta |

## 8. Estimación orientativa

| Repo | Trabajo | Tamaño |
|---|---|---|
| Angular | Interceptor + fix token + validación + 4 specs | M |
| React | Interceptor + validación + 3 specs | M |
| Vue3 | Interceptor + validación + 3 specs | S |

---

**Documento generado el 2026-11-10** a partir de la revisión de seguridad del backend y del código
actual de los tres frontends.
