# 📌 Advisories aceptados (sin parche upstream)

**Regla**: un CVE sólo se "acepta" cuando **no existe** versión parcheada publicada. Se registra aquí
y se **vuelve a revisar en cada iteración** (cualquier PR que toque dependencias y el workflow
`owasp-dependency-check-maven.yml`). En cuanto haya parche, se sube la dependencia (o el override de
Spring Boot) y se retira el silencio **en la misma PR**.

**Chequeo automático**: `./scripts/check-accepted-advisories.sh` (paso *Accepted advisories recheck*
de `owasp-dependency-check-maven.yml`). Consulta OSV por cada entrada y **falla** si ya existe una
versión parcheada para el paquete Maven afectado, o si el advisory ya no aplica a la versión
registrada.

**No confundir con los falsos positivos de CPE** (ver `AGENTS.md` → *Dependency-Check False
Positives*): ahí no hay nada que parchear porque el CPE matcher se equivoca de artefacto.

## Cómo se acepta (checklist)

1. Comprobar que es *unfixable*: dependency-check/OSV no ofrecen versión parcheada y la última
   publicada sigue afectada.
2. Documentar el motivo y el alcance en `AGENTS.md` → *Known Vulnerabilities*, sin ocultar el CVE.
3. Añadir la entrada al bloque JSON de abajo con la ruta de dependencia completa.
4. Registrar la decisión en `docs/security/informe-vulnerabilidades-<fecha>.md`.

## Cómo se revisa (cada iteración)

```bash
./scripts/check-accepted-advisories.sh
```

- ✅ Si sigue sin parche: nada que hacer.
- ❌ Si ya hay parche, o si ya no aplica a la versión registrada: subir la dependencia, retirar el
  silencio y **borrar la entrada de este registro**.

## Registro

```json accepted-advisories
{
  "ecosystem": "Maven",
  "advisories": []
}
```

Estado a 2026-10-16: **sin advisories aceptados**. El classpath no tiene vulnerabilidades reales
pendientes (última revisión: 2026-10-15, `docs/security/informe-vulnerabilidades-2026-10-15.md`).
