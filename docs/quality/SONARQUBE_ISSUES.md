# SonarQube Issues - Análisis Detallado

**Fecha**: 2025-12-08  
**Total Issues**: 23  
**Proyecto**: isidromerayo_TFG_UNIR-backend

---

## 📊 Resumen por Severidad

| Severidad | Cantidad | Tipo |
|-----------|----------|------|
| 🔴 BLOCKER | 1 | CODE_SMELL |
| 🟠 CRITICAL | 4 | 1 BUG + 3 CODE_SMELL |
| 🟡 MAJOR | 2 | CODE_SMELL |
| 🔵 MINOR | 5 | CODE_SMELL |
| ⚪ INFO | 11 | CODE_SMELL |

---

## 🔴 BLOCKER (1) - Prioridad Máxima

### 1. Test sin assertions
**Archivo**: `src/test/java/eu/estilolibre/tfgunir/backend/security/TokenServiceTest.java`  
**Regla**: java:S2699  
**Mensaje**: Add at least one assertion to this test case.

**Problema**: Un test que no tiene ninguna aserción no está verificando nada.

**Solución**:
```java
// Añadir assertions como:
assertNotNull(result);
assertEquals(expected, actual);
assertTrue(condition);
```

---

## 🟠 CRITICAL (4) - Alta Prioridad

### 1. BUG: Comparación incorrecta en test
**Archivo**: `src/test/java/eu/estilolibre/tfgunir/backend/repository/ValoracionRepositoryTests.java`  
**Regla**: java:S5845  
**Mensaje**: Change the assertion arguments to not compare a primitive value with null.

**Problema**: Estás comparando un tipo primitivo (int, long, etc.) con null, lo cual es incorrecto.

**Solución**:
```java
// ❌ Incorrecto
assertNotNull(valoracion.getPuntuacion()); // si getPuntuacion() retorna int

// ✅ Correcto
assertTrue(valoracion.getPuntuacion() > 0);
// o cambiar el tipo a Integer en lugar de int
```

### 2-4. Métodos vacíos sin explicación
**Archivos**:
- `src/test/java/eu/estilolibre/tfgunir/backend/security/TokenServiceTest.java`
- ~~`src/main/java/eu/estilolibre/tfgunir/backend/controller/FormUser.java`~~ (eliminado)
- ~~`src/main/java/eu/estilolibre/tfgunir/backend/controller/User.java`~~ (eliminado)

**Regla**: java:S1186  
**Mensaje**: Add a nested comment explaining why this method is empty, throw an UnsupportedOperationException or complete the implementation.

**Solución**:
```java
// Opción 1: Añadir comentario explicativo
public void metodo() {
    // Método intencionalmente vacío - será implementado en versión futura
}

// Opción 2: Lanzar excepción
public void metodo() {
    throw new UnsupportedOperationException("Not implemented yet");
}

// Opción 3: Completar la implementación
```

---

## 🟡 MAJOR (2) - Prioridad Media

### 1. Logging ineficiente
**Archivo**: `src/main/java/eu/estilolibre/tfgunir/backend/security/TokenService.java`  
**Regla**: java:S2629  
**Mensaje**: Invoke method(s) only conditionally. Use the built-in formatting to construct this argument.

**Problema**: Concatenación de strings en logs que se ejecuta siempre, incluso si el log está deshabilitado.

**Solución**:
```java
// ❌ Incorrecto
log.debug("Token: " + token + " for user: " + username);

// ✅ Correcto - usa placeholders
log.debug("Token: {} for user: {}", token, username);

// ✅ Correcto - condicional
if (log.isDebugEnabled()) {
    log.debug("Token: " + token + " for user: " + username);
}
```

### 2. Field injection en lugar de constructor injection
**Archivo**: `src/main/java/eu/estilolibre/tfgunir/backend/controller/LoginController.java`  
**Regla**: java:S6813  
**Mensaje**: Remove this field injection and use constructor injection instead.

**Problema**: Usar `@Autowired` en campos dificulta el testing y viola principios de inmutabilidad.

**Solución**:
```java
// ❌ Incorrecto
@Autowired
private UserService userService;

// ✅ Correcto
private final UserService userService;

@Autowired
public LoginController(UserService userService) {
    this.userService = userService;
}

// O con Lombok
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;
}
```

---

## 🔵 MINOR (5) - Prioridad Baja

### 1-4. Usar operador diamante
**Archivos**:
- `src/main/java/eu/estilolibre/tfgunir/backend/model/Categoria.java`
- `src/main/java/eu/estilolibre/tfgunir/backend/model/Curso.java`
- `src/main/java/eu/estilolibre/tfgunir/backend/model/Instructor.java`
- `src/main/java/eu/estilolibre/tfgunir/backend/model/Usuario.java`

**Regla**: java:S2293  
**Mensaje**: Replace the type specification in this constructor call with the diamond operator ("<>").

**Solución**:
```java
// ❌ Incorrecto
List<String> list = new ArrayList<String>();

// ✅ Correcto
List<String> list = new ArrayList<>();
```

### 5. Orden de modificadores
**Archivo**: `src/test/java/eu/estilolibre/tfgunir/backend/rest/CategoriaControllerIT.java`  
**Regla**: java:S1124  
**Mensaje**: Reorder the modifiers to comply with the Java Language Specification.

**Solución**:
```java
// Orden correcto según JLS:
// public protected private abstract static final transient volatile synchronized native strictfp

// ❌ Incorrecto
static public final String CONSTANT = "value";

// ✅ Correcto
public static final String CONSTANT = "value";
```

---

## ⚪ INFO (11) - Información

### 1-10. Modificador 'public' redundante en tests
**Archivos**: Múltiples archivos de test  
**Regla**: java:S5786  
**Mensaje**: Remove this 'public' modifier.

**Problema**: En JUnit 5, los métodos de test no necesitan ser públicos.

**Solución**:
```java
// ❌ Incorrecto (JUnit 5)
@Test
public void testSomething() {
    // ...
}

// ✅ Correcto (JUnit 5)
@Test
void testSomething() {
    // ...
}
```

### 11. TODO sin completar
**Archivo**: `src/main/java/eu/estilolibre/tfgunir/backend/controller/LoginController.java`  
**Regla**: java:S1135  
**Mensaje**: Complete the task associated to this TODO comment.

**Solución**:
- ✅ Resuelto: se eliminó el TODO y se completó la refactorización del controlador.
- O crear un issue/ticket y referenciar: `// TODO: Issue #123 - Implementar validación`
- O eliminar el TODO si ya no es necesario

---

## 🎯 Plan de Acción Recomendado

### Fase 1: Issues Críticos (BLOCKER + CRITICAL)
1. ✅ Añadir assertions al test en `TokenServiceTest.java`
2. ✅ Corregir comparación con null en `ValoracionRepositoryTests.java`
3. ✅ Eliminar `FormUser.java` y `User.java` (sustituidos por DTOs/records en `dto/`)

### Fase 2: Issues Importantes (MAJOR)
4. ✅ Optimizar logging en `TokenService.java`
5. ✅ Cambiar a constructor injection en `LoginController.java`

### Fase 3: Mejoras de Código (MINOR + INFO)
6. ✅ Usar operador diamante en modelos
7. ✅ Remover modificadores `public` en tests JUnit 5
8. ✅ Completar o eliminar TODO
9. ✅ Corregir orden de modificadores

---

## 📈 Impacto en Quality Gate

Resolver estos issues mejorará:
- **Reliability Rating**: D → A (al corregir el BUG crítico)
- **Maintainability**: Mejor código, más fácil de mantener
- **Test Quality**: Tests más robustos y correctos

**Nota**: La cobertura (18.18%) y Security Hotspots (0%) requieren trabajo adicional más allá de estos issues.

---

## 🔗 Referencias

- [SonarQube Rules](https://rules.sonarsource.com/java/)
- Proyecto: https://sonarcloud.io/dashboard?id=isidromerayo_TFG_UNIR-backend
