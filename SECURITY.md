# Security Policy

## � Contiexto del Proyecto

Este es un proyecto académico desarrollado como **Trabajo de Fin de Grado (TFG)** para la Universidad Internacional de La Rioja (UNIR), Escuela Superior de Ingeniería y Tecnología, Grado en Ingeniería Informática.

**Título**: Frameworks frontend JavaScript: Análisis y estudio práctico

Aunque es un proyecto educativo, tomamos la seguridad seriamente y agradecemos cualquier reporte de vulnerabilidades para mejorar la calidad del código y servir como referencia para otros estudiantes.

## 📋 Versiones Soportadas

Las siguientes versiones del proyecto reciben actualizaciones de seguridad:

| Versión | Soportada          | Notas |
| ------- | ------------------ | ----- |
| 0.5.x   | :white_check_mark: | Versión actual en desarrollo |
| < 0.5   | :x:                | Versiones legacy sin soporte |

## 🔒 Reportar una Vulnerabilidad

La seguridad de este proyecto es una prioridad. Si descubres una vulnerabilidad de seguridad, te agradecemos que nos lo comuniques de forma responsable.

### ⚠️ NO abras un issue público

Para proteger a los usuarios del proyecto, **por favor NO reportes vulnerabilidades de seguridad a través de issues públicos de GitHub**.

### 📧 Cómo reportar

Tienes dos opciones para reportar vulnerabilidades de forma privada:

#### Opción 1: GitHub Security Advisories (Recomendado)

1. Ve a la pestaña **[Security](https://github.com/isidromerayo/TFG_UNIR-backend/security)** del repositorio
2. Haz clic en **"Report a vulnerability"**
3. Completa el formulario con los detalles de la vulnerabilidad

#### Opción 2: Contacto directo

Si prefieres, puedes contactar directamente al mantenedor del proyecto a través de GitHub.

### 📝 Información a incluir en el reporte

Para ayudarnos a entender y resolver el problema rápidamente, por favor incluye:

- **Descripción detallada** de la vulnerabilidad
- **Pasos para reproducir** el problema
- **Impacto potencial** (qué puede hacer un atacante)
- **Versión afectada** del proyecto
- **Posible solución** (si tienes alguna idea)
- **Prueba de concepto** (PoC) si es posible

### ⏱️ Qué esperar después de reportar

- **Confirmación inicial**: Responderemos en **48-72 horas** para confirmar la recepción
- **Evaluación**: Analizaremos la vulnerabilidad y su impacto
- **Actualizaciones**: Te mantendremos informado del progreso cada 7 días
- **Resolución**: Trabajaremos en un fix y coordinaremos la divulgación pública
- **Crédito**: Te daremos crédito público por el descubrimiento (si lo deseas)

## 🤝 Política de Divulgación Responsable

Pedimos a los investigadores de seguridad que:

1. **No divulguen** la vulnerabilidad públicamente hasta que hayamos lanzado un fix
2. **No exploten** la vulnerabilidad más allá de lo necesario para demostrarla
3. **Nos den tiempo razonable** para resolver el problema antes de la divulgación pública
4. **Actúen de buena fe** para evitar violaciones de privacidad, destrucción de datos o interrupción del servicio

## 🛡️ Nuestro compromiso

Nos comprometemos a:

- Confirmar la recepción de tu reporte en 48-72 horas
- Mantener comunicación contigo sobre el progreso
- Trabajar en un fix de forma prioritaria
- Notificarte cuando el fix esté listo para ser publicado
- Darte crédito público por el descubrimiento (si lo deseas)
- No tomar acciones legales contra investigadores que actúen de buena fe

## 🔐 Prácticas de Seguridad del Proyecto

Este proyecto implementa múltiples capas de seguridad:

### Stack de Seguridad

- **Framework**: Spring Boot 4.0.2 con Spring Security 7.0.2
- **Autenticación**: JWT (JSON Web Tokens) v0.12.7
- **Java**: OpenJDK 17 (JRE)
- **Base de datos**: MariaDB con credenciales configurables

### Análisis Automatizado

- **Análisis estático**: SpotBugs 4.9.8.2 con plugins de seguridad (find-sec-bugs, fb-contrib)
- **Análisis de calidad**: SonarCloud con Quality Gate (99% cobertura)
- **Análisis de dependencias**: OWASP Dependency Check 12.2.0
- **CI/CD**: GitHub Actions con análisis automático en cada PR

### Áreas de Seguridad Críticas

Si encuentras vulnerabilidades, presta especial atención a:

1. **Autenticación JWT**: `TokenService.java` - Generación y validación de tokens
2. **Endpoints REST**: `LoginController.java` - Autenticación de usuarios
3. **Configuración Spring Security**: Políticas de acceso y CORS
4. **Entidades JPA**: Serialización y exposición de datos sensibles
5. **Dependencias**: Vulnerabilidades conocidas en librerías de terceros

## 📚 Recursos Adicionales

### Documentación del Proyecto

- [README.md](README.md) - Documentación principal
- [DOCS_INDEX.md](DOCS_INDEX.md) - Índice de toda la documentación
- [SONARQUBE_ISSUES.md](SONARQUBE_ISSUES.md) - Issues de calidad resueltos

### Referencias de Seguridad

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [GitHub Security Best Practices](https://docs.github.com/en/code-security)
- [OWASP Dependency Check](https://owasp.org/www-project-dependency-check/)

## 🎓 Propósito Educativo

Este proyecto tiene un propósito educativo y de investigación. Si eres estudiante o investigador y encuentras problemas de seguridad, te animamos a:

- Reportarlos siguiendo esta política
- Aprender del proceso de divulgación responsable
- Contribuir con mejoras al proyecto

## 📞 Contacto

- **Seguridad**: Utiliza los canales mencionados arriba (GitHub Security Advisories)
- **Otras consultas**: Abre un issue normal en el repositorio
- **Repositorio**: https://github.com/isidromerayo/TFG_UNIR-backend

---

**Última actualización**: 2025-12-09  
**Proyecto**: TFG UNIR - Backend (Spring Boot 3.4.12 + Java 21)
