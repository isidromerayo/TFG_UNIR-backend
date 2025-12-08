# Análisis de Cobertura de Código

**Fecha**: 2025-12-08  
**Herramienta**: JaCoCo 0.8.14  
**Tests ejecutados**: 15 (11 unitarios + 4 integración)

---

## 📊 Resumen General

| Métrica | Cobertura | Estado |
|---------|-----------|--------|
| **Instrucciones** | 56% (269/474) | 🟡 Mejorable |
| **Ramas** | 20% (2/10) | 🔴 Bajo |
| **Líneas** | 50% (48/96) | 🟡 Mejorable |
| **Métodos** | 47% (18/38) | 🟡 Mejorable |
| **Clases** | 73% (8/11) | 🟢 Aceptable |

**Objetivo SonarQube**: ≥ 80% de cobertura  
**Gap actual**: -30 puntos porcentuales

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

### 4. eu.estilolibre.tfgunir.backend.controller (3% 🔴)
**Estado**: Crítico - Requiere atención

| Métrica | Valor |
|---------|-------|
| Instrucciones | 6/154 (3%) |
| Ramas | 0/6 (0%) |
| Líneas | 3/42 (7%) |
| Métodos | 1/18 (5%) |
| Clases | 1/3 (33%) |

**Clases**:
- `LoginController`: 6/100 instrucciones (6%), 0/6 ramas (0%)
  - Métodos cubiertos: 1/3 (constructor)
  - Métodos sin cubrir: `login()`, `registro()`
- `User`: 0/31 instrucciones (0%)
  - Sin cobertura: getters, setters, constructores
- `FormUser`: 0/23 instrucciones (0%)
  - Sin cobertura: getters, setters, constructores

**Análisis**: Este es el paquete más crítico. Los controladores REST no tienen tests unitarios ni de integración que los ejerciten completamente.

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

## 🎯 Áreas Críticas que Requieren Tests

### Prioridad ALTA 🔴

1. **LoginController** (6% cobertura)
   - `login()`: Endpoint crítico de autenticación
   - `registro()`: Endpoint de registro de usuarios
   - **Impacto**: Funcionalidad core sin validación automática

2. **WebConfig** (0% cobertura)
   - Configuración CORS
   - **Impacto**: Seguridad y acceso cross-origin sin validar

### Prioridad MEDIA 🟡

3. **User** (0% cobertura)
   - DTOs sin tests
   - **Impacto**: Serialización/deserialización no validada

4. **FormUser** (0% cobertura)
   - DTOs sin tests
   - **Impacto**: Validación de formularios no testeada

---

## 📈 Plan de Mejora para Alcanzar 80%

### Fase 1: Tests de Controladores (Impacto: +35%)

**Objetivo**: Cubrir `LoginController` completamente

```java
// Tests necesarios:
- testLoginExitoso()
- testLoginCredencialesInvalidas()
- testLoginUsuarioNoExiste()
- testRegistroExitoso()
- testRegistroEmailDuplicado()
- testRegistroValidacionFallida()
```

**Estimación**: 6 tests de integración  
**Cobertura esperada**: De 3% a 85% en el paquete controller

---

### Fase 2: Tests de Configuración (Impacto: +5%)

**Objetivo**: Validar `WebConfig`

```java
// Tests necesarios:
- testCorsConfigurationAllowedOrigins()
- testCorsConfigurationAllowedMethods()
- testCorsConfigurationAllowedHeaders()
```

**Estimación**: 3 tests de integración  
**Cobertura esperada**: De 0% a 80% en el paquete config

---

### Fase 3: Tests de DTOs (Impacto: +5%)

**Objetivo**: Validar serialización de `User` y `FormUser`

```java
// Tests necesarios:
- testUserSerialization()
- testUserDeserialization()
- testFormUserValidation()
```

**Estimación**: 3 tests unitarios  
**Cobertura esperada**: De 0% a 70% en DTOs

---

## 📊 Proyección de Cobertura

| Fase | Cobertura Actual | Cobertura Esperada | Tests Nuevos |
|------|------------------|-------------------|--------------|
| Inicial | 56% | - | 15 |
| Fase 1 | 56% | 75% | +6 |
| Fase 2 | 75% | 80% | +3 |
| Fase 3 | 80% | 85% | +3 |

**Total tests finales**: 27 (15 actuales + 12 nuevos)

---

## 🔍 Análisis de Ramas (Branch Coverage)

**Cobertura actual**: 20% (2/10 ramas)

### Ramas no cubiertas:

1. **TokenService** (2/4 ramas cubiertas)
   - Validación condicional de logging
   - Manejo de excepciones en parsing de tokens

2. **LoginController** (0/6 ramas cubiertas)
   - Validación de credenciales
   - Manejo de errores de autenticación
   - Validación de registro

**Recomendación**: Priorizar tests que cubran flujos alternativos (errores, validaciones fallidas).

---

## 📝 Exclusiones Configuradas

Según `pom.xml`, JaCoCo excluye:
```xml
<exclude>eu/estilolibre/tfgunir/backend/model/*</exclude>
```

**Justificación**: Las entidades JPA son principalmente POJOs con getters/setters generados por Lombok. No requieren tests exhaustivos.

---

## 🚀 Recomendaciones

### Inmediatas
1. ✅ Crear tests de integración para `LoginController`
2. ✅ Añadir tests para validar configuración CORS
3. ✅ Implementar tests de serialización para DTOs

### A Medio Plazo
4. Aumentar cobertura de ramas al 60%
5. Configurar quality gate en CI para rechazar PRs con cobertura < 80%
6. Añadir mutation testing con PIT para validar calidad de tests

### Buenas Prácticas
- Mantener cobertura de repositorios al 100%
- Priorizar tests de integración para endpoints REST
- Usar `@WebMvcTest` para tests unitarios de controladores
- Usar `@SpringBootTest` para tests de integración completos

---

## 📎 Comandos Útiles

```bash
# Ejecutar tests con cobertura
./mvnw clean verify -Pfailsafe

# Ver reporte HTML
open target/site/jacoco/index.html

# Ver reporte CSV
cat target/site/jacoco/jacoco.csv

# Solo tests unitarios
./mvnw test

# Solo tests de integración
./mvnw -DskipUTs -Pfailsafe verify
```

---

## 🔗 Referencias

- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [SonarQube Coverage Requirements](https://docs.sonarqube.org/latest/user-guide/metric-definitions/)
- Proyecto SonarCloud: https://sonarcloud.io/dashboard?id=isidromerayo_TFG_UNIR-backend
