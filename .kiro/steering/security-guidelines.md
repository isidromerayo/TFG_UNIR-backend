---
inclusion: always
---

# Directrices de Seguridad

## 🔐 Configuración de Seguridad

### JWT Configuration
- **Librería**: jjwt v0.13.0
- **Algoritmo**: HS256 (configurable)
- **Expiración**: Configurar según ambiente
- **Secret**: NUNCA hardcodear, usar variables de entorno

### Spring Security
- **Versión**: Incluida en Spring Boot 3.4.12
- **Configuración**: Ver `SecurityConfig.java`
- **Endpoints públicos**: `/api/auth/**`, `/swagger-ui/**`, `/h2-console/**` (solo test)

## 🚨 Reglas de Seguridad CRÍTICAS

### Secrets Management
```bash
# ❌ NUNCA hacer esto
String apiKey = "sk-1234567890abcdef";

# ✅ Usar variables de entorno
@Value("${app.api.key}")
private String apiKey;
```

### Validación de Input
- **SIEMPRE** validar entrada del usuario
- Usar `@Valid` y `@Validated`
- Sanitizar datos antes de persistir
- Validar tamaños máximos

### Base de Datos
- **NUNCA** usar concatenación de strings para SQL
- Usar JPA/Hibernate queries parametrizadas
- Validar permisos antes de operaciones CRUD
- Logs NO deben contener datos sensibles

### Dependencias
- Ejecutar OWASP Dependency Check regularmente
- Actualizar dependencias con vulnerabilidades conocidas
- Revisar CVEs en nuevas dependencias

## 🔍 Análisis de Seguridad

### OWASP Dependency Check
```bash
# Análisis básico
./mvnw -Pdependency-check verify

# Con API key (más completo)
./mvnw -Pdependency-check verify -Dnvd.api.key=$NVD_API_KEY
```

### SpotBugs Security
- **Plugin**: FindSecBugs activo
- **Comando**: `./mvnw spotbugs:check`
- **Reportes**: `target/spotbugsXml.xml`

## 📝 Checklist de Seguridad

### Antes de cada commit:
- [ ] No hay secrets hardcodeados
- [ ] Validación de input implementada
- [ ] Tests completos pasan (`./mvnw -Pfailsafe verify`)
- [ ] Tests de seguridad incluidos
- [ ] SpotBugs security checks pasan
- [ ] Logs no exponen datos sensibles

### Antes de cada release:
- [ ] OWASP Dependency Check ejecutado
- [ ] Vulnerabilidades críticas resueltas
- [ ] Configuración de producción revisada
- [ ] Secrets de producción configurados
- [ ] HTTPS configurado (en producción)