# Configuración de JaCoCo - Cobertura de Tests

**Fecha**: 2025-12-08  
**Estado**: ✅ Configurado correctamente

---

## ✅ Confirmación de Configuración

La configuración de JaCoCo está **correctamente configurada** para capturar:

1. ✅ **Cobertura de tests unitarios** (fase `test`)
2. ✅ **Cobertura de tests de integración** (fase `integration-test`)
3. ✅ **Reporte combinado** (merge de ambos)

---

## 📊 Archivos Generados

### Archivos de Datos (.exec)

```bash
target/jacoco-ut.exec       # 621 KB - Datos de tests unitarios
target/jacoco-it.exec       # 712 KB - Datos de tests de integración
target/jacoco-merged.exec   # 730 KB - Merge de ambos
```

### Reportes HTML

```bash
target/site/jacoco-ut/      # Reporte solo tests unitarios
target/site/jacoco-it/      # Reporte solo tests de integración
target/site/jacoco/         # Reporte combinado (PRINCIPAL)
```

---

## 🔧 Configuración en pom.xml

### Executions Configuradas

1. **prepare-agent-ut** (fase: `initialize`)
   - Prepara agente para tests unitarios
   - Genera: `jacoco-ut.exec`

2. **report-ut** (fase: `test`)
   - Genera reporte de tests unitarios
   - Salida: `target/site/jacoco-ut/`

3. **prepare-agent-it** (fase: `pre-integration-test`)
   - Prepara agente para tests de integración
   - Genera: `jacoco-it.exec`

4. **report-it** (fase: `post-integration-test`)
   - Genera reporte de tests de integración
   - Salida: `target/site/jacoco-it/`

5. **merge-results** (fase: `verify`)
   - Combina ambos archivos .exec
   - Genera: `jacoco-merged.exec`

6. **report-merged** (fase: `verify`)
   - Genera reporte combinado final
   - Salida: `target/site/jacoco/` ⭐ **REPORTE PRINCIPAL**

---

## 📈 Ejemplo de Cobertura: LoginController

### Comparación de Reportes

| Métrica | Tests Unitarios | Tests Integración | **Combinado** |
|---------|-----------------|-------------------|---------------|
| **Instrucciones** | 6/100 (6%) | 94/100 (94%) | **94/100 (94%)** ✅ |
| **Ramas** | 0/6 (0%) | 4/6 (67%) | **4/6 (67%)** ✅ |
| **Líneas** | 3/19 (16%) | 18/19 (95%) | **18/19 (95%)** ✅ |
| **Métodos** | 1/3 (33%) | 3/3 (100%) | **3/3 (100%)** ✅ |

**Conclusión**: El reporte combinado toma el **máximo** de cobertura de ambos tipos de tests.

---

## 🎯 Cobertura Global Actual

Según el reporte combinado (`target/site/jacoco/index.html`):

| Métrica | Valor | Objetivo SonarQube | Estado |
|---------|-------|-------------------|--------|
| Instrucciones | **85%** | ≥ 80% | ✅ **ALCANZADO** |
| Ramas | 60% | - | 🟡 Mejorable |
| Líneas | 88% | - | ✅ Excelente |
| Métodos | 87% | - | ✅ Excelente |
| Clases | 89% | - | ✅ Excelente |

**Clases analizadas**: 9 (excluidas 2 DTOs + entidades model)

---

## 📝 Comandos para Generar Reportes

### Reporte Completo (Unitarios + Integración)
```bash
./mvnw clean verify -Pintegration-tests
```

Este comando ejecuta:
1. Tests unitarios (11 tests)
2. Tests de integración (4 tests)
3. Genera los 3 reportes (UT, IT, Merged)

### Solo Tests Unitarios
```bash
./mvnw clean test
```

Genera solo: `target/site/jacoco-ut/`

### Solo Tests de Integración
```bash
./mvnw clean -DskipUTs -Pintegration-tests verify
```

Genera solo: `target/site/jacoco-it/`

---

## 🔍 Verificar Reportes

### Ver Reporte HTML Principal (Combinado)
```bash
# Linux/Mac
xdg-open target/site/jacoco/index.html

# Windows
start target/site/jacoco/index.html
```

### Ver Datos CSV
```bash
# Reporte combinado
cat target/site/jacoco/jacoco.csv

# Solo unitarios
cat target/site/jacoco-ut/jacoco.csv

# Solo integración
cat target/site/jacoco-it/jacoco.csv
```

### Comparar Cobertura por Clase
```bash
echo "=== Tests Unitarios ==="
grep "LoginController" target/site/jacoco-ut/jacoco.csv

echo "=== Tests Integración ==="
grep "LoginController" target/site/jacoco-it/jacoco.csv

echo "=== Combinado ==="
grep "LoginController" target/site/jacoco/jacoco.csv
```

---

## 🚀 Integración con SonarQube

### Configuración para CI

SonarQube debe apuntar al archivo **merged**:

```properties
# sonar-project.properties
sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
sonar.jacoco.reportPaths=target/jacoco-merged.exec
```

### Workflow de GitHub Actions

```yaml
- name: Run tests with coverage
  run: ./mvnw clean verify -Pintegration-tests

- name: SonarQube Scan
  env:
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
  run: |
    ./mvnw sonar:sonar \
      -Dsonar.projectKey=isidromerayo_TFG_UNIR-backend \
      -Dsonar.organization=isidromerayo \
      -Dsonar.host.url=https://sonarcloud.io \
      -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
```

---

## ✅ Checklist de Verificación

- [x] Tests unitarios generan `jacoco-ut.exec`
- [x] Tests de integración generan `jacoco-it.exec`
- [x] Merge genera `jacoco-merged.exec`
- [x] Reporte HTML unitarios en `target/site/jacoco-ut/`
- [x] Reporte HTML integración en `target/site/jacoco-it/`
- [x] Reporte HTML combinado en `target/site/jacoco/`
- [x] Reporte combinado muestra cobertura máxima de ambos
- [x] Exclusiones configuradas para paquete `model`
- [x] Exclusiones configuradas para DTOs (`User`, `FormUser`)
- [x] **Objetivo de cobertura alcanzado: 85% > 80%**

---

## 📚 Referencias

- [JaCoCo Maven Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
- [JaCoCo Report Aggregation](https://www.jacoco.org/jacoco/trunk/doc/merge-mojo.html)
- [SonarQube Java Coverage](https://docs.sonarqube.org/latest/analysis/coverage/)

---

## 🎓 Notas Importantes

1. **El reporte principal es el combinado**: `target/site/jacoco/index.html`
2. **SonarQube debe usar**: `jacoco-merged.exec` o el XML del reporte combinado
3. **Los reportes separados** son útiles para debugging y análisis detallado
4. **El merge es automático**: Se ejecuta en la fase `verify` con `-Pintegration-tests`
5. **Exclusiones**: El paquete `model` está excluido (entidades JPA con Lombok)
