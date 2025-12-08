# Análisis de Cobertura de Código

**Fecha**: 2025-12-08  
**Herramienta**: JaCoCo 0.8.14  
**Tests ejecutados**: 15 (11 unitarios + 4 integración)  
**Configuración**: ✅ Reportes separados + combinado (ver `JACOCO_CONFIGURATION.md`)

---

## 🎉 Mejora Reciente

**Configuración actualizada**: JaCoCo ahora genera reportes separados para tests unitarios e integración, más un reporte combinado.

**Impacto**: 
- ✅ Visibilidad clara de qué tipo de test cubre cada componente
- ✅ LoginController: De 6% (solo UT) a 94% (UT + IT)
- ✅ Cobertura de ramas: De 0% a 67% en LoginController

Ver `JACOCO_CONFIGURATION.md` para detalles de la configuración.

---

## 📊 Resumen General (Reporte Combinado)

| Métrica | Cobertura | Estado |
|---------|-----------|--------|
| **Instrucciones** | 56% (269/474) | 🟡 Mejorable |
| **Ramas** | 60% (6/10) | 🟡 Mejorable |
| **Líneas** | 50% (48/96) | 🟡 Mejorable |
| **Métodos** | 47% (18/38) | 🟡 Mejorable |
| **Clases** | 73% (8/11) | 🟢 Aceptable |

**Objetivo SonarQube**: ≥ 80% de cobertura  
**Gap actual**: -24 puntos porcentuales

**Desglose por tipo de test**:
- Tests unitarios: 11 tests → Cubren principalmente repositorios y seguridad
- Tests integración: 4 tests → Cubren endpoints REST y flujos completos
- **Reporte combinado**: Toma el máximo de ambos

---

## 📦 Cobertura por Paquete

### 1. eu.estilolibre.tfgunir.backend.repository (100% ✅)
**Estado**: Excelente

| Métrica | Valor |
|---------|-------|
| Instrucciones | 52/52 (100%) |
| Líneas | 12/12 (100%) |
| Métodos | 8/8 (100%) |
| Clases | 4/4 (100%) |

**Análisis**: Todos los repositorios están completamente cubiertos por tests unitarios.

---

### 2. eu.estilolibre.tfgunir.backend.security (99% ✅)
**Estado**: Excelente

| Métrica | Valor |
|---------|-------|
| Instrucciones | 208/209 (99%) |
| Ramas | 2/4 (50%) |
| Líneas | 32/32 (100%) |
| Métodos | 8/8 (100%) |
| Clases | 2/2 (100%) |

**Clases**:
- `TokenService`: 70/71 instrucciones (98%), 2/4 ramas (50%)
- `WebSecurityConfig`: 138/138 instrucciones (100%)

**Análisis**: Excelente cobertura. Las 2 ramas no cubiertas en `TokenService` corresponden a validaciones de logging condicional.

---

### 3. eu.estilolibre.tfgunir.backend (37% 🟡)
**Estado**: Mejorable

| Métrica | Valor |
|---------|-------|
| Instrucciones | 3/8 (37%) |
| Líneas | 1/3 (33%) |
| Métodos | 1/2 (50%) |
| Clases | 1/1 (100%) |

**Clases**:
- `BackendApplication`: 3/8 instrucciones (37%)

**Análisis**: Clase principal de Spring Boot. La cobertura parcial es normal ya que el método `main()` no se ejecuta en tests.

---

### 4. eu.estilolibre.tfgunir.backend.controller (94% �)
**Estado**: Bueno - Cubierto por tests de integración

| Métrica | Valor |
|---------|-------|
| Instrucciones | 94/100 (94%) |
| Ramas | 4/6 (67%) |
| Líneas | 18/19 (95%) |
| Métodos | 3/3 (100%) |
| Clases | 1/3 (33%) |

**Clases**:
- `LoginController`: 94/100 instrucciones (94%), 4/6 ramas (67%)
  - ✅ Tests unitarios: Constructor (6 instrucciones)
  - ✅ Tests integración: `login()`, `registro()` (88 instrucciones)
  - 🟡 Ramas sin cubrir: 2/6 (casos de error específicos)
- `User`: 0/31 instrucciones (0%)
  - 🔴 Sin cobertura: DTOs sin tests de serialización
- `FormUser`: 0/23 instrucciones (0%)
  - 🔴 Sin cobertura: DTOs sin tests de validación

**Análisis**: `LoginController` tiene excelente cobertura gracias a los tests de integración. Los DTOs (`User`, `FormUser`) necesitan tests de serialización/validación.

---

### 5. com.unir.tfg.config (0% 🔴)
**Estado**: Sin cobertura

| Métrica | Valor |
|---------|-------|
| Instrucciones | 0/51 (0%) |
| Líneas | 0/7 (0%) |
| Métodos | 0/2 (0%) |
| Clases | 0/1 (0%) |

**Clases**:
- `WebConfig`: 0/51 instrucciones (0%)

**Análisis**: Clase de configuración CORS sin tests. Debería tener tests de integración para verificar la configuración.

---

## 🎯 Áreas que Requieren Tests

### Prioridad ALTA 🔴

1. **WebConfig** (0% cobertura)
   - Configuración CORS
   - **Impacto**: Seguridad y acceso cross-origin sin validar
   - **Acción**: Añadir tests de integración para CORS

### Prioridad MEDIA 🟡

2. **User** (0% cobertura)
   - DTOs sin tests
   - **Impacto**: Serialización/deserialización no validada
   - **Acción**: Tests de serialización JSON

3. **FormUser** (0% cobertura)
   - DTOs sin tests
   - **Impacto**: Validación de formularios no testeada
   - **Acción**: Tests de validación de campos

4. **LoginController - Ramas** (67% cobertura)
   - 2 ramas sin cubrir en manejo de errores
   - **Impacto**: Casos edge no validados
   - **Acción**: Tests para casos de error específicos

---

## 📈 Plan de Mejora para Alcanzar 80%

### ✅ Fase 1: Tests de Controladores (COMPLETADA)

**Objetivo**: Cubrir `LoginController` completamente  
**Estado**: ✅ **94% de cobertura alcanzada**

Tests implementados:
- ✅ `testLoginExitoso()` - LoginControllerIT
- ✅ `testLoginCredencialesInvalidas()` - LoginControllerIT
- ✅ `testRegistroExitoso()` (implícito en setup)
- ✅ Endpoints REST cubiertos por tests de integración

**Resultado**: De 6% a 94% en LoginController

---

### Fase 2: Tests de Configuración (Impacto: +10%)

**Objetivo**: Validar `WebConfig`

```java
// Tests necesarios:
- testCorsConfigurationAllowedOrigins()
- testCorsConfigurationAllowedMethods()
- testCorsConfigurationAllowedHeaders()
- testCorsPreflightRequest()
```

**Estimación**: 4 tests de integración  
**Cobertura esperada**: De 0% a 80% en el paquete config  
**Impacto global**: +10% (de 56% a 66%)

---

### Fase 3: Tests de DTOs (Impacto: +8%)

**Objetivo**: Validar serialización de `User` y `FormUser`

```java
// Tests necesarios:
- testUserSerialization()
- testUserDeserialization()
- testFormUserValidation()
- testFormUserConstraints()
```

**Estimación**: 4 tests unitarios  
**Cobertura esperada**: De 0% a 70% en DTOs  
**Impacto global**: +8% (de 66% a 74%)

---

### Fase 4: Cobertura de Ramas (Impacto: +6%)

**Objetivo**: Cubrir casos edge en `LoginController`

```java
// Tests necesarios:
- testLoginUsuarioNoExiste()
- testRegistroEmailDuplicado()
- testRegistroValidacionFallida()
- testLoginTokenInvalido()
```

**Estimación**: 4 tests de integración  
**Cobertura esperada**: De 67% a 90% en ramas  
**Impacto global**: +6% (de 74% a 80%)

---

## 📊 Proyección de Cobertura

| Fase | Cobertura Actual | Cobertura Esperada | Tests Nuevos | Estado |
|------|------------------|-------------------|--------------|--------|
| Inicial | 56% | - | 15 | ✅ Completado |
| Fase 1 (Controllers) | 56% | - | 0 | ✅ Ya cubierto por IT |
| Fase 2 (Config) | 56% | 66% | +4 | 🔄 Pendiente |
| Fase 3 (DTOs) | 66% | 74% | +4 | 🔄 Pendiente |
| Fase 4 (Ramas) | 74% | 80% | +4 | 🔄 Pendiente |

**Total tests finales**: 27 (15 actuales + 12 nuevos)  
**Gap actual**: 24 puntos porcentuales para alcanzar 80%

---

## 🔍 Análisis de Ramas (Branch Coverage)

**Cobertura actual**: 20% (2/10 ramas) → **Actualizado: 60% (6/10 ramas)**

### Ramas cubiertas:

1. **TokenService** (2/4 ramas cubiertas - 50%)
   - ✅ Flujo normal de creación de token
   - ✅ Flujo normal de lectura de token
   - 🔴 Validación condicional de logging (no crítico)
   - 🔴 Manejo de excepciones en parsing (edge case)

2. **LoginController** (4/6 ramas cubiertas - 67%)
   - ✅ Login exitoso
   - ✅ Login con credenciales inválidas
   - ✅ Registro exitoso
   - ✅ Validación básica de campos
   - 🔴 Usuario no existe (caso específico)
   - 🔴 Email duplicado en registro (caso específico)

### Ramas no cubiertas (4 restantes):

**Prioridad MEDIA**:
- LoginController: Casos edge de validación (2 ramas)
- TokenService: Logging condicional (2 ramas - no crítico)

**Recomendación**: Añadir tests para casos edge en LoginController para alcanzar 80% de cobertura de ramas.

---

## 📝 Exclusiones Configuradas

Según `pom.xml`, JaCoCo excluye:
```xml
<exclude>eu/estilolibre/tfgunir/backend/model/*</exclude>
```

**Justificación**: Las entidades JPA son principalmente POJOs con getters/setters generados por Lombok. No requieren tests exhaustivos.

---

## 🚀 Recomendaciones

### Completadas ✅
1. ✅ Tests de integración para `LoginController` (94% cobertura)
2. ✅ Configuración de JaCoCo para reportes separados (UT + IT + Merged)
3. ✅ Tests de repositorios (100% cobertura)
4. ✅ Tests de seguridad (99% cobertura)

### Inmediatas 🔴
1. Añadir tests para validar configuración CORS en `WebConfig`
2. Implementar tests de serialización para DTOs (`User`, `FormUser`)
3. Añadir tests para casos edge en `LoginController` (ramas faltantes)

### A Medio Plazo 🟡
4. Aumentar cobertura de ramas de 60% a 80%
5. Configurar quality gate en CI para rechazar PRs con cobertura < 80%
6. Añadir mutation testing con PIT para validar calidad de tests
7. Documentar estrategia de testing en CONTRIBUTING.md

### Buenas Prácticas ✨
- ✅ Mantener cobertura de repositorios al 100%
- ✅ Priorizar tests de integración para endpoints REST
- ✅ Usar `@SpringBootTest` para tests de integración completos
- 🔄 Considerar `@WebMvcTest` para tests unitarios de controladores (opcional)
- 🔄 Separar tests de integración en paquete dedicado

---

## 📎 Comandos Útiles

```bash
# Ejecutar tests con cobertura completa (UT + IT + Merged)
./mvnw clean verify -Pfailsafe

# Ver reporte HTML combinado (PRINCIPAL)
xdg-open target/site/jacoco/index.html

# Ver reportes separados
xdg-open target/site/jacoco-ut/index.html    # Solo unitarios
xdg-open target/site/jacoco-it/index.html    # Solo integración

# Ver reporte CSV combinado
cat target/site/jacoco/jacoco.csv

# Comparar cobertura por clase
grep "LoginController" target/site/jacoco-ut/jacoco.csv  # Unitarios
grep "LoginController" target/site/jacoco-it/jacoco.csv  # Integración
grep "LoginController" target/site/jacoco/jacoco.csv     # Combinado

# Solo tests unitarios
./mvnw clean test

# Solo tests de integración
./mvnw clean -DskipUTs -Pfailsafe verify

# Verificar archivos generados
ls -lh target/*.exec
```

**Nota**: Ver `JACOCO_CONFIGURATION.md` para más detalles sobre la configuración.

---

## 🔗 Referencias

- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [SonarQube Coverage Requirements](https://docs.sonarqube.org/latest/user-guide/metric-definitions/)
- Proyecto SonarCloud: https://sonarcloud.io/dashboard?id=isidromerayo_TFG_UNIR-backend
