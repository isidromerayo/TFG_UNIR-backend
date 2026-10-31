# Release automatizado (GitHub Actions)

Guía del workflow [`.github/workflows/release.yml`](../../.github/workflows/release.yml): prepara la
versión con `maven-release-plugin`, publica `main` + tag, construye y publica la imagen Docker y crea
la GitHub Release.

## Requisitos (secretos del repositorio)

| Secret | Uso | ¿Obligatorio? |
|--------|-----|---------------|
| `RELEASE_TOKEN` | PAT para push de commits/tags a `main` y para crear la GitHub Release | Sí, en release real |
| `DOCKER_HUB_USERNAME` | Usuario de Docker Hub | Sí, en release real |
| `DOCKER_HUB_TOKEN` | Token/contraseña de Docker Hub | Sí, en release real |

`DOCKER_HUB_USERNAME` y `DOCKER_HUB_TOKEN` ya están configurados. Falta `RELEASE_TOKEN`.

## Configuración inicial (una sola vez)

### Por qué hace falta un PAT

`main` tiene **branch protection** (se requiere PR para integrar). El `GITHUB_TOKEN` que usa un
workflow de Actions **no puede** saltarse esa regla, así que el push de los commits de release
fallaría con `protected branch hook declined`. Como `enforce_admins = false`, un PAT de un admin sí
puede hacer push directo.

### Opción A — PAT fine-grained (recomendada)

1. GitHub → **Settings → Developer settings → Personal access tokens → Fine-grained tokens → Generate new token**.
2. *Repository access*: **Only select repositories** → `isidromerayo/TFG_UNIR-backend`.
3. *Permissions → Repository permissions →* **Contents: Read and write**.
4. Generar y copiar el token (`github_pat_...`).
5. Guardarlo como secret:

```bash
gh secret set RELEASE_TOKEN --repo isidromerayo/TFG_UNIR-backend
```

Alternativa con un classic PAT: scope `repo`.

### Opción B — bypass de GitHub Actions (sin PAT)

En **Settings → Branches → `main` → Edit**, activar *Allow specified actors to bypass required pull
requests* y añadir la app **GitHub Actions**. En ese caso el `GITHUB_TOKEN` puede hacer push y el
workflow funciona sin `RELEASE_TOKEN` (el step *Check RELEASE_TOKEN* se puede retirar o dejar que
use el fallback `GITHUB_TOKEN`).

> La opción B relaja la protección de `main` para cualquier workflow del repo; la opción A limita el
> bypass al token concreto. Por eso se recomienda la A.

## Cómo lanzar un release

### Desde la UI

**Actions → Release → Run workflow** con:

- `release_version`: versión a publicar, `X.Y.Z` **sin** `SNAPSHOT` (p. ej. `0.8.0`).
- `next_version`: siguiente versión de desarrollo, `X.Y.Z` (el workflow le añade `-SNAPSHOT`, p. ej. `0.8.1`).
- `dry_run`: `true` para simular la preparación **sin** push, sin Docker y sin GitHub Release.

### Desde la CLI

```bash
# Simulación (no requiere RELEASE_TOKEN)
gh workflow run "Release" --repo isidromerayo/TFG_UNIR-backend \
  -f release_version=0.8.0 -f next_version=0.8.1 -f dry_run=true

# Release real
gh workflow run "Release" --repo isidromerayo/TFG_UNIR-backend \
  -f release_version=0.8.0 -f next_version=0.8.1
```

Seguimiento:

```bash
gh run list --repo isidromerayo/TFG_UNIR-backend --workflow=release.yml --limit 3
gh run watch <run-id> --repo isidromerayo/TFG_UNIR-backend
```

## Qué hace el workflow

1. Valida que `release_version` y `next_version` son `X.Y.Z` y distintas.
2. Comprueba que existe `RELEASE_TOKEN` (solo en release real).
3. Checkout de `main` con historial y tags (`fetch-depth: 0`).
4. `mvn release:prepare -DreleaseVersion=… -DdevelopmentVersion=…-SNAPSHOT -DpushChanges=false`
   con tests saltados (el commit ya pasó CI). Crea dos commits y el tag `vX.Y.Z`.
5. Push de `main` y del tag.
6. Login en Docker Hub, checkout del tag, `./mvnw clean package -DskipTests` y
   `./scripts/publish-images.sh --skip-login` → publica `spring-backend-tfg:X.Y.Z` y `latest`.
7. `gh release create vX.Y.Z --generate-notes`.
8. `mvn release:clean` (siempre) para borrar `release.properties` y los backups.

Con `dry_run=true` solo se ejecuta el paso 4 con `-DdryRun=true`, y el resto se omite.

## Fallback manual

Si el workflow no está disponible (p. ej. sin `RELEASE_TOKEN`), el flujo manual sigue siendo válido:

```bash
mvn release:prepare
git push origin main
git push origin vX.Y.Z
git checkout vX.Y.Z && ./mvnw clean package -Dmaven.test.skip=true
./scripts/publish-images.sh
git checkout main
gh release create vX.Y.Z --generate-notes
```

## Problemas frecuentes

| Síntoma | Causa / solución |
|---------|------------------|
| `protected branch hook declined` | Falta `RELEASE_TOKEN` o el PAT no es de un admin / no tiene *Contents: Read and write*. |
| `Falta el secret RELEASE_TOKEN` | Crear el secret (opción A) o configurar el bypass (opción B). |
| `tag already exists` | El tag `vX.Y.Z` ya existe: elige otra versión o bórralo si fue un intento fallido. |
| `Version invalida` | Usa `X.Y.Z` sin `SNAPSHOT` en ambos inputs. |
| `denied: requested access to the resource is denied` | `DOCKER_HUB_TOKEN` caducado o sin permisos de escritura sobre `spring-backend-tfg`. |
| La imagen se publica pero no hay release | Revisar el paso *Create GitHub release*: `RELEASE_TOKEN` debe poder crear releases. |

## Historial

- **2026-10-31**: `v0.7.2` publicado manualmente (Boot 4.1.2 + springdoc 3.2.0 + swagger-ui 5.34.1);
  se añade este workflow para automatizarlo a partir de la siguiente release.
