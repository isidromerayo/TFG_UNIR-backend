# Snyk Security Issue: Unprotected Storage of Credentials

**Fecha**: 2025-12-09  
**Severidad**: HIGH (Score: 5.5)  
**CWE**: CWE-256 - Unprotected Storage of Credentials  
**Herramienta**: Snyk Code

---

## 📍 Ubicación

**Archivo**: `src/main/java/eu/estilolibre/tfgunir/backend/controller/LoginController.java`  
**Línea**: 48  
**Método**: `auth(LoginRequest login)`

---

## 🔴 Problema Detectado

### Código vulnerable:

```java
String resultPass = result.get(0).getPassword();
if (login.getPassword().equals(resultPass) && "A".equals(result.get(0).getEstado())) {
    // ... autenticación exitosa
}
```

### Vulnerabilidades identificadas:

#### 1. **Timing Attack** (Principal)

**Descripción**: El método `String.equals()` compara caracteres uno por uno y sale inmediatamente cuando encuentra una diferencia. Esto permite a un atacante:

- Medir el tiempo de respuesta
- Deducir cuántos caracteres son correctos
- Realizar un ataque de fuerza bruta optimizado

**Ejemplo de ataque**:
```
Password real: "admin123"

Intento 1: "aaaaaaaa" -> 1ms (falla en primer carácter)
Intento 2: "abaaaaaa" -> 2ms (falla en segundo carácter)
Intento 3: "adaaaaaa" -> 3ms (falla en tercer carácter)
...
Intento N: "admin123" -> 8ms (todos los caracteres coinciden)
```

**Impacto**: Un atacante puede reducir significativamente el espacio de búsqueda.

#### 2. **Passwords en texto plano**

**Descripción**: Las passwords se almacenan y comparan sin hashing.

**Problemas**:
- Si la BBDD es comprometida, todas las passwords son visibles
- No hay protección contra rainbow tables
- Viola OWASP Top 10 (A02:2021 – Cryptographic Failures)

#### 3. **Secret Key hardcoded**

**Descripción**: La clave JWT está hardcoded en el código (línea 64).

```java
String secretKey = "813cef5f-3459-4618-87a6-a69e2a1296d4_mySecretKey_mySecretKey";
```

**Problemas**:
- Visible en el repositorio Git
- No se puede rotar sin recompilar
- Compromete todos los tokens si se expone

---

## 🎯 Solución Recomendada

### Opción 1: Spring Security con BCrypt (Recomendado)

#### Paso 1: Configurar Password Encoder

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

#### Paso 2: Actualizar LoginController

```java
@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Autowired
    public LoginController(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("")
    public ResponseEntity<?> auth(@RequestBody LoginRequest login) {
        List<Usuario> result = repository.findByEmail(login.getEmail());

        if (result.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "no autorizado"));
        }

        Usuario usuario = result.get(0);
        
        // ✅ Comparación segura con BCrypt (constant-time)
        if (passwordEncoder.matches(login.getPassword(), usuario.getPassword()) 
                && "A".equals(usuario.getEstado())) {
            
            String token = tokenService.crearToken(usuario.getEmail());
            
            AuthResponse response = new AuthResponse(
                usuario.getId(),
                usuario.getEmail(),
                token,
                usuario.getNombre() + " " + usuario.getApellidos()
            );
            
            return ResponseEntity.ok(response);
        }

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("message", "no autorizado"));
    }
}
```

#### Paso 3: Actualizar TokenService

```java
@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:3600000}")
    private long expiration;

    public String crearToken(String username) {
        return crearToken(username, secretKey, 
            new Date(System.currentTimeMillis() + expiration));
    }

    // ... resto del código
}
```

#### Paso 4: Configurar application.properties

```properties
# JWT Configuration
jwt.secret=${JWT_SECRET:default-secret-key-change-in-production}
jwt.expiration=3600000
```

#### Paso 5: Migrar passwords existentes

```sql
-- Script de migración (ejecutar una vez)
-- Las passwords actuales en texto plano deben ser hasheadas

-- Opción 1: Forzar reset de passwords
UPDATE usuarios SET password = NULL;

-- Opción 2: Hashear passwords existentes (si son conocidas)
-- Usar BCryptPasswordEncoder en Java para generar hashes
```

### Opción 2: Comparación constant-time manual (No recomendado)

Si por alguna razón no puedes usar BCrypt:

```java
import java.security.MessageDigest;
import java.util.Arrays;

private boolean constantTimeEquals(String a, String b) {
    if (a == null || b == null) {
        return false;
    }
    
    byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
    byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
    
    return MessageDigest.isEqual(aBytes, bBytes);
}
```

**Nota**: Esta opción NO resuelve el problema de passwords en texto plano.

---

## 📊 Comparación de Soluciones

| Aspecto | String.equals() | MessageDigest.isEqual() | BCrypt |
|---------|----------------|------------------------|--------|
| **Timing attack** | ❌ Vulnerable | ✅ Protegido | ✅ Protegido |
| **Password hashing** | ❌ No | ❌ No | ✅ Sí |
| **Salt automático** | ❌ No | ❌ No | ✅ Sí |
| **Trabajo adaptativo** | ❌ No | ❌ No | ✅ Sí (cost factor) |
| **Estándar industria** | ❌ No | ⚠️ Parcial | ✅ Sí |
| **Recomendado por OWASP** | ❌ No | ❌ No | ✅ Sí |

---

## 🔧 Plan de Implementación

### Fase 1: Preparación (Sin breaking changes)

1. ✅ Añadir dependencia BCrypt (ya incluida en Spring Security)
2. ✅ Crear `PasswordEncoder` bean
3. ✅ Actualizar `TokenService` para usar configuración externa
4. ✅ Añadir propiedades JWT a `application.properties`

### Fase 2: Migración de código

1. ✅ Refactorizar `LoginController`
2. ✅ Inyectar `PasswordEncoder` y `TokenService`
3. ✅ Reemplazar `String.equals()` con `passwordEncoder.matches()`
4. ✅ Actualizar tests

### Fase 3: Migración de datos (CRÍTICO)

1. ⚠️ **Backup de BBDD**
2. ⚠️ Decidir estrategia:
   - Opción A: Forzar reset de passwords (más seguro)
   - Opción B: Migración gradual (más complejo)
3. ⚠️ Hashear passwords existentes
4. ⚠️ Verificar en entorno de test

### Fase 4: Despliegue

1. ✅ Configurar `JWT_SECRET` en variables de entorno
2. ✅ Desplegar nueva versión
3. ✅ Monitorear logs de autenticación
4. ✅ Verificar que no hay errores

---

## ⚠️ Consideraciones Importantes

### Breaking Changes

- **Passwords existentes**: Dejarán de funcionar si se hashean
- **Usuarios afectados**: Todos los usuarios del sistema
- **Solución**: Implementar flujo de "Olvidé mi contraseña"

### Compatibilidad hacia atrás

Para mantener compatibilidad temporal:

```java
if (passwordEncoder.matches(login.getPassword(), usuario.getPassword())) {
    // Nueva lógica con BCrypt
} else if (login.getPassword().equals(usuario.getPassword())) {
    // Lógica legacy (temporal)
    // Hashear password y actualizar en BBDD
    String hashedPassword = passwordEncoder.encode(login.getPassword());
    usuario.setPassword(hashedPassword);
    repository.save(usuario);
}
```

**Nota**: Eliminar lógica legacy después de migración completa.

---

## 📚 Referencias

### OWASP

- [OWASP Top 10 - A02:2021 Cryptographic Failures](https://owasp.org/Top10/A02_2021-Cryptographic_Failures/)
- [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html)
- [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)

### CWE

- [CWE-256: Unprotected Storage of Credentials](https://cwe.mitre.org/data/definitions/256.html)
- [CWE-208: Observable Timing Discrepancy](https://cwe.mitre.org/data/definitions/208.html)
- [CWE-327: Use of a Broken or Risky Cryptographic Algorithm](https://cwe.mitre.org/data/definitions/327.html)

### Spring Security

- [Spring Security Password Encoding](https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html)
- [BCryptPasswordEncoder](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/crypto/bcrypt/BCryptPasswordEncoder.html)

### Timing Attacks

- [Timing Attack Wikipedia](https://en.wikipedia.org/wiki/Timing_attack)
- [A Lesson In Timing Attacks](https://codahale.com/a-lesson-in-timing-attacks/)

---

## 🎯 Prioridad

**ALTA** - Este issue debe resolverse antes de producción.

**Justificación**:
- Afecta autenticación (componente crítico)
- Permite ataques de timing
- Passwords en texto plano (violación de compliance)
- Secret key expuesta en código

**Tiempo estimado**: 4-8 horas (incluyendo tests y migración)

---

## ✅ Criterios de Aceptación

- [ ] `PasswordEncoder` configurado y en uso
- [ ] `String.equals()` reemplazado por `passwordEncoder.matches()`
- [ ] Secret key JWT movida a configuración externa
- [ ] Tests actualizados y pasando
- [ ] Documentación actualizada
- [ ] Plan de migración de passwords definido
- [ ] Snyk no reporta el issue

---

**Última actualización**: 2025-12-09  
**Estado**: Pendiente de implementación
