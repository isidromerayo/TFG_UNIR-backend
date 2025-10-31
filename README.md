# TFG_UNIR Backend

[![Java CI with Maven](https://github.com/isidromerayo/TFG_UNIR-backend/actions/workflows/maven.yml/badge.svg)](https://github.com/isidromerayo/TFG_UNIR-backend/actions/workflows/maven.yml)
[![Pull Request CI](https://github.com/isidromerayo/TFG_UNIR-backend/actions/workflows/pull-request.yml/badge.svg)](https://github.com/isidromerayo/TFG_UNIR-backend/actions/workflows/pull-request.yml)

### Universidad Internacional de La Rioja

### Escuela Superior de Ingeniería y Tecnología 

#### Grado en Ingeniería Informática

## Frameworks frontend JavaScript: Análisis y estudio práctico

### Backend

## 🚀 Desarrollo y Contribución

Este proyecto sigue un flujo de trabajo basado en Pull Requests para mantener la calidad del código.

### 📋 Flujo de trabajo recomendado

```bash
# 1. Crear nueva rama para tu feature/fix
git checkout main
git pull origin main
git checkout -b feature/nombre-descriptivo

# 2. Realizar cambios y commits
git add .
git commit -m "feat: descripción del cambio"

# 3. Push y crear Pull Request
git push -u origin feature/nombre-descriptivo
# Crear PR desde GitHub web

# 4. Después del merge, limpiar
git checkout main
git pull origin main
git branch -d feature/nombre-descriptivo
```

### 🧪 Tests

```bash
# Tests unitarios
mvn test

# Tests de integración
mvn -DskipUTs -Pfailsafe verify

# Todos los tests
mvn verify -Pfailsafe
```

Es necesaria una versión de Java 17, para utilizar Spring Boot 3.0.x

```
cd backend
./mvnw clean install
```

#### BBDD: MariaDB para producción

```
$ mariadb --version
mariadb  Ver 15.1 Distrib 10.3.38-MariaDB, for debian-linux-gnu (x86_64) using readline 5.2
```

##### Crear base de datos y usuario

```
MariaDB [(none)]> create database tfg_unir; -- create NEW database
MariaDB [(none)]> create user 'user_tfg'@'%' identified by 'tfg_un1r_PWD'; -- create user
MariaDB [(none)]> grant all on tfg_unir.* to 'user_tfg'@'%'; -- give all privileges to the user
```

##### Carga inicial de datos

Los recursos están el proyecto [TFG UNIR](https://github.com/isidromerayo/TFG_UNIR)

```
mariadb -u user_tfg -ptfg_un1r_PWD tfg_unir < recursos/db/dump.mariadb.sql 
```

Backup con mysql de datos

```
mysqldump -u user_tfg -ptfg_un1r_PWD tfg_unir > recursos/db/dump.mariadb.sql 
```

#### Docker MariaDB

Carga inicial para utilizar por el backend con Spring Boot con datos de pruebas del directorio recursos/db

`docker run --name mariadb-tfg -p 3306:3306 -d isidromerayo/mariadb-tfg`

Para publicar en docker hub `docker push isidromerayo/mariadb-tfg:X.Y.Z`

#### BBDD: H2 para test


#### Lanzar aplicación con Spring Boot 3

Es necesario disponer de BBDD

Lanzar aplicación desde consola

```
cd backend
./mvnw spring-boot:run
```

Visualizar API expuesta

http://localhost:8080/api


Swagger

http://localhost:8080/swagger-ui.html

#### Docker Spring Boot 

Construir imagen de aplicación con el jar generado del backend (con el `spring.datasource.url=jdbc:mariadb://app_db:3306/tfg_unir` en el application.properties) hay que ejecutar un maven para generar


```
cd backend
./mvnw clean install
docker build -t isidromerayo/spring-backend-tfg:VERSION-X.Y.Z .
```

https://spring.io/guides/topicals/spring-boot-docker/
https://javatodev.com/docker-compose-for-spring-boot-with-mariadb/

#### docker compose

Con docker compose se montará un contendor con MariaDB (datos precargados) y otro con la aplicación de Spring Boot 3 con el API 

Nota: application.properties debe apuntar al alias de docker 'app_db' y no a localhost en `spring.datasource.url=jdbc:mariadb://app_db:3306/tfg_unir`


```
cd backend
docker compose up
```

MariaDB correra en el puerto por defecto *3306* y Spring Boot 3 en el *8080*, así no tendremos montado lo necesario para tener el backend y probar la aplicación con los diferentes frameworks.

Con `docker compose up -d` corre en segundo plano y liberamos la terminal

```
[+] Running 2/2
 ✔ Container backend-maria_db-1     Started     0.4s 
 ✔ Container backend-api_service-1  Started     0.6s 

```

Para detener las instancias de los contenedores `docker compose stop`.

```
[+] Stopping 2/2
 ✔ Container backend-api_service-1  Stopped     0.3s 
 ✔ Container backend-maria_db-1     Stopped     0.5s 
```

##### Publicar imagen en docker

Deberemos estar logeados en nuestra cuenta de docker

`docker push isidromerayo/spring-backend-tfg:X.Y.Z`

#### OWASP Dependency Check

`mvn org.owasp:dependency-check-maven:check`

Si tenemos un API KEY del servicio
 
`mvn org.owasp:dependency-check-maven:check -Dnvd.api.key=XXXX`

#### Maven release

Preparar el release

`mvn release:prepare`

modo batch (no pregunta)

`mvn release:prepare -B`

Este comando realiza varias acciones:

* Verifica que no haya cambios sin commitear.
* Actualiza la versión en el pom.xml (por ejemplo, de 1.0-SNAPSHOT a 1.0).
* Hace commit de los cambios.
* Crea un tag en el sistema de control de versiones (Git, SVN, etc.).
* Actualiza el pom.xml a la siguiente versión de desarrollo (por ejemplo, 1.1-SNAPSHOT).

Hacer el release (deploy)

`mvn release:perform -Dmaven.javadoc.skip=true`

Este comando:

* Clona el proyecto desde el tag creado.
* Compila y despliega el artefacto al repositorio definido en <distributionManagement>.