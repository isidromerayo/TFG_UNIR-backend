# Configuración de SonarQube con GitHub Actions

Este documento describe cómo configurar el análisis de SonarQube basado en CI para este proyecto.

## 📋 Pasos de Configuración

### 1. Deshabilitar Análisis Automático en SonarQube

⚠️ **IMPORTANTE**: Antes de activar el análisis basado en CI, debes deshabilitar el análisis automático en SonarQube.

1. Ve a tu proyecto en SonarQube Cloud: https://sonarcloud.io/project/overview?id=isidromerayo_TFG_UNIR-backend
2. Ve a **Administration** > **Analysis Method**
3. Desactiva **Automatic Analysis**

### 2. Crear GitHub Secret para SONAR_TOKEN

1. Ve a tu repositorio en GitHub: https://github.com/isidromerayo/TFG_UNIR-backend
2. Ve a **Settings** > **Secrets and variables** > **Actions**
3. Click en **New repository secret**
4. Configura el secret:
   - **Name**: `SONAR_TOKEN`
   - **Value**: `[Tu token de SonarQube]`
5. Click en **Add secret**

> 💡 **Nota**: El token de SonarQube se puede obtener desde:
> - SonarQube Cloud > My Account > Security > Generate Tokens
> - O desde: https://sonarcloud.io/account/security

### 3. Verificar Configuración en pom.xml

El `pom.xml` ya está configurado con la organización de SonarQube:

```xml
<properties>
    <sonar.organization>isidromerayo</sonar.organization>
</properties>
```

✅ **Ya configurado** - No requiere cambios.

### 4. Workflow de GitHub Actions

El workflow `.github/workflows/sonarqube.yml` ya está creado y configurado para:

- ✅ Ejecutarse en push a `main`
- ✅ Ejecutarse en Pull Requests
- ✅ Usar Java 21 (versión del proyecto)
- ✅ Cachear dependencias de Maven y SonarQube
- ✅ Ejecutar análisis con `mvn verify` + SonarQube scanner

## 🔍 Características del Análisis

### Triggers
- **Push a main**: Análisis completo del código en la rama principal
- **Pull Requests**: Análisis de los cambios en el PR

### Caché
- **Maven packages** (`.m2`): Acelera la descarga de dependencias
- **SonarQube packages** (`.sonar/cache`): Acelera el análisis

### Análisis
```bash
mvn -B verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
  -Dsonar.projectKey=isidromerayo_TFG_UNIR-backend
```

Esto ejecuta:
1. **verify**: Compila, ejecuta tests unitarios y de integración
2. **sonar:sonar**: Envía resultados a SonarQube Cloud

## 📊 Visualización de Resultados

Después de cada análisis, puedes ver los resultados en:

- **SonarQube Cloud**: https://sonarcloud.io/project/overview?id=isidromerayo_TFG_UNIR-backend
- **GitHub Actions**: En la pestaña "Actions" del repositorio
- **Pull Requests**: SonarQube añadirá un comentario con el resumen

## 🎯 Métricas Analizadas

SonarQube analiza:

- **Bugs**: Errores de código que pueden causar fallos
- **Vulnerabilities**: Problemas de seguridad
- **Code Smells**: Problemas de mantenibilidad
- **Coverage**: Cobertura de tests (con JaCoCo)
- **Duplications**: Código duplicado
- **Security Hotspots**: Áreas sensibles de seguridad

## 🔧 Configuración Avanzada (Opcional)

### Excluir Archivos del Análisis

Si necesitas excluir archivos, añade en `pom.xml`:

```xml
<properties>
    <sonar.organization>isidromerayo</sonar.organization>
    <sonar.exclusions>
        **/generated/**,
        **/test/**/*.java
    </sonar.exclusions>
</properties>
```

### Configurar Cobertura de Tests

El proyecto ya usa JaCoCo. Para asegurar que SonarQube lo detecta:

```xml
<properties>
    <sonar.organization>isidromerayo</sonar.organization>
    <sonar.java.coveragePlugin>jacoco</sonar.java.coveragePlugin>
    <sonar.coverage.jacoco.xmlReportPaths>
        ${project.build.directory}/site/jacoco/jacoco.xml
    </sonar.coverage.jacoco.xmlReportPaths>
</properties>
```

### Configurar Quality Gate

En SonarQube Cloud:
1. Ve a **Project Settings** > **Quality Gate**
2. Selecciona o crea un Quality Gate personalizado
3. Define umbrales para:
   - Coverage mínimo (ej: 80%)
   - Bugs máximos (ej: 0)
   - Vulnerabilities máximas (ej: 0)

## 🧪 Testing Local

Para probar el análisis localmente antes de hacer push:

```bash
# Asegúrate de tener el token configurado
export SONAR_TOKEN="tu-token-aqui"

# Ejecutar análisis
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=isidromerayo_TFG_UNIR-backend \
  -Dsonar.organization=isidromerayo \
  -Dsonar.host.url=https://sonarcloud.io
```

## 🚨 Troubleshooting

### Error: "SONAR_TOKEN not found"

**Causa**: El secret no está configurado en GitHub.

**Solución**: Sigue el paso 2 para crear el secret.

### Error: "Shallow clone detected"

**Causa**: El checkout no tiene suficiente historial.

**Solución**: Ya configurado con `fetch-depth: 0` en el workflow.

### Error: "Project not found"

**Causa**: El proyecto no existe en SonarQube Cloud o el projectKey es incorrecto.

**Solución**: 
1. Verifica que el proyecto existe en SonarQube Cloud
2. Verifica que el `projectKey` es correcto: `isidromerayo_TFG_UNIR-backend`

### Análisis no aparece en SonarQube

**Causa**: El análisis automático sigue activo.

**Solución**: Desactiva el análisis automático (paso 1).

## 📝 Checklist de Configuración

- [ ] Análisis automático desactivado en SonarQube Cloud
- [ ] Secret `SONAR_TOKEN` creado en GitHub
- [ ] Workflow `sonarqube.yml` commiteado y pusheado
- [ ] Propiedad `sonar.organization` en `pom.xml`
- [ ] Primer análisis ejecutado correctamente
- [ ] Resultados visibles en SonarQube Cloud
- [ ] Badge de SonarQube añadido al README (opcional)

## 🏆 Badge de SonarQube (Opcional)

Añade el badge de SonarQube al README.md:

```markdown
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=isidromerayo_TFG_UNIR-backend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=isidromerayo_TFG_UNIR-backend)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=isidromerayo_TFG_UNIR-backend&metric=bugs)](https://sonarcloud.io/summary/new_code?id=isidromerayo_TFG_UNIR-backend)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=isidromerayo_TFG_UNIR-backend&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=isidromerayo_TFG_UNIR-backend)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=isidromerayo_TFG_UNIR-backend&metric=coverage)](https://sonarcloud.io/summary/new_code?id=isidromerayo_TFG_UNIR-backend)
```

## 🔗 Referencias

- [SonarQube Cloud Documentation](https://docs.sonarcloud.io/)
- [SonarQube Maven Plugin](https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-maven/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)

---

**Última actualización**: 2025-12-06  
**Versión del workflow**: 1.0
