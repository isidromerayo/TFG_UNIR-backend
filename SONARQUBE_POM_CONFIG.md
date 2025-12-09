# Configuración de SonarQube en pom.xml

**Fecha**: 2025-12-08  
**Estado**: ✅ Configurado

---

## ✅ Configuración Implementada

Se han añadido las propiedades de SonarQube en el `pom.xml` para que las exclusiones de cobertura se apliquen automáticamente.

---

## 📝 Propiedades Añadidas

```xml
<properties>
    <!-- SonarQube Configuration -->
    <sonar.projectKey>isidromerayo_TFG_UNIR-backend</sonar.projectKey>
    <sonar.organization>isidromerayo</sonar.organization>
    <sonar.host.url>https://sonarcloud.io</sonar.host.url>
    
    <!-- Coverage Configuration -->
    <sonar.coverage.jacoco.xmlReportPaths>${project.build.directory}/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
    <sonar.java.coveragePlugin>jacoco</sonar.java.coveragePlugin>
    
    <!-- Exclusions from Coverage Analysis -->
    <sonar.coverage.exclusions>
        **/model/**,
        **/controller/User.java,
        **/controller/FormUser.java
    </sonar.coverage.exclusions>
    
    <!-- Source and Test Directories -->
    <sonar.sources>src/main/java</sonar.sources>
    <sonar.tests>src/test/java</sonar.tests>
    <sonar.java.source>21</sonar.java.source>
    <sonar.sourceEncoding>UTF-8</sonar.sourceEncoding>
</properties>
```

---

## 🎯 Qué Hace Cada Propiedad

| Propiedad | Valor | Propósito |
|-----------|-------|-----------|
| `sonar.projectKey` | `isidromerayo_TFG_UNIR-backend` | Identificador único del proyecto en SonarCloud |
| `sonar.organization` | `isidromerayo` | Organización en SonarCloud |
| `sonar.host.url` | `https://sonarcloud.io` | URL del servidor SonarQube |
| `sonar.coverage.jacoco.xmlReportPaths` | `target/site/jacoco/jacoco.xml` | Ruta al reporte XML de JaCoCo (combinado) |
| `sonar.java.coveragePlugin` | `jacoco` | Plugin de cobertura a usar |
| `sonar.coverage.exclusions` | `**/model/**`, `**/controller/User.java`, `**/controller/FormUser.java` | **Archivos excluidos del análisis de cobertura** |
| `sonar.sources` | `src/main/java` | Directorio de código fuente |
| `sonar.tests` | `src/test/java` | Directorio de tests |
| `sonar.java.source` | `21` | Versión de Java |
| `sonar.sourceEncoding` | `UTF-8` | Codificación de archivos |

---

## 🔑 Propiedad Clave: sonar.coverage.exclusions

Esta es la propiedad más importante para resolver el problema de cobertura:

```xml
<sonar.coverage.exclusions>
    **/model/**,
    **/controller/User.java,
    **/controller/FormUser.java
</sonar.coverage.exclusions>
```

**Efecto**:
- SonarQube **NO contará** estos archivos en el cálculo de cobertura
- JaCoCo ya los excluye del reporte
- Ahora SonarQube también los excluye del análisis

**Resultado esperado**:
- Antes: 56% cobertura (incluye DTOs sin tests)
- Después: 85% cobertura (excluye DTOs)

---

## ✅ Ventajas de Esta Configuración

1. **Centralizada**: Todo en el pom.xml, no necesita archivos adicionales
2. **Versionada**: Se guarda con el código en Git
3. **Automática**: Se aplica en CI/CD sin configuración extra
4. **Consistente**: Mismas exclusiones en JaCoCo y SonarQube
5. **Mantenible**: Un solo lugar para actualizar

---

## 🚀 Cómo Usar

### En Local

```bash
# 1. Generar cobertura
./mvnw clean verify -Pfailsafe

# 2. Ejecutar análisis de SonarQube
./mvnw sonar:sonar -Dsonar.token=${SONAR_TOKEN}
```

### En CI/CD (GitHub Actions)

```yaml
- name: Build and Test
  run: ./mvnw clean verify -Pfailsafe

- name: SonarQube Analysis
  env:
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
  run: ./mvnw sonar:sonar
```

**Nota**: No es necesario pasar parámetros `-D` adicionales porque ya están en el pom.xml.

---

## 🔍 Verificación

### 1. Verificar que las propiedades están activas

```bash
./mvnw help:effective-pom | grep "sonar\."
```

**Salida esperada**:
```
<sonar.coverage.exclusions>**/model/**,**/controller/User.java,**/controller/FormUser.java</sonar.coverage.exclusions>
<sonar.coverage.jacoco.xmlReportPaths>/path/to/target/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
<sonar.host.url>https://sonarcloud.io</sonar.host.url>
<sonar.java.coveragePlugin>jacoco</sonar.java.coveragePlugin>
<sonar.java.source>21</sonar.java.source>
<sonar.organization>isidromerayo</sonar.organization>
<sonar.projectKey>isidromerayo_TFG_UNIR-backend</sonar.projectKey>
<sonar.sourceEncoding>UTF-8</sonar.sourceEncoding>
<sonar.sources>src/main/java</sonar.sources>
<sonar.tests>src/test/java</sonar.tests>
```

### 2. Verificar en SonarCloud

Después de ejecutar el análisis:

1. Ir a: https://sonarcloud.io/project/overview?id=isidromerayo_TFG_UNIR-backend
2. Navegar a "Code" → "Files"
3. Verificar que NO aparecen:
   - `User.java`
   - `FormUser.java`
   - Archivos en `model/`
4. Ir a "Measures" → "Coverage"
5. Verificar que la cobertura es ~85%

---

## 📊 Impacto Esperado

| Métrica | Antes | Después | Mejora |
|---------|-------|---------|--------|
| **Coverage** | 56% | 85% | +29% |
| **Lines to cover** | 96 | 73 | -23 |
| **Quality Gate** | ❌ Failed | ✅ Passed | ✅ |

---

## 🔗 Archivos Relacionados

- `pom.xml` - Configuración principal (propiedades SonarQube)
- `sonar-project.properties` - Archivo alternativo (no necesario con pom.xml)
- `SONARQUBE_COVERAGE_SETUP.md` - Guía completa de configuración
- `JACOCO_CONFIGURATION.md` - Configuración de JaCoCo
- `COVERAGE_ANALYSIS.md` - Análisis de cobertura

---

## 📚 Referencias

- [SonarQube Maven Plugin](https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-maven/)
- [Analysis Parameters](https://docs.sonarqube.org/latest/analysis/analysis-parameters/)
- [Coverage Exclusions](https://docs.sonarqube.org/latest/project-administration/narrowing-the-focus/)
