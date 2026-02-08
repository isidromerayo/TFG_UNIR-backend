# Guía de Uso: MariaDB / MySQL (Legacy)

Esta guía explica cómo configurar el backend para utilizar MariaDB o MySQL en lugar de PostgreSQL.

## 1. Dependencias de Maven

Sustituye el driver de PostgreSQL por el de MariaDB en tu `pom.xml`:

```xml
<!-- MariaDB Connector -->
<dependency>
    <groupId>org.mariadb.jdbc</groupId>
    <artifactId>mariadb-java-client</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- O MySQL Connector si prefieres MySQL -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

## 2. Configuración de la Aplicación

Actualiza el archivo `src/main/resources/application.properties` con los valores correspondientes:

```properties
# MariaDB
spring.datasource.url=jdbc:mariadb://localhost:3306/tfg_unir
spring.datasource.username=user_tfg
spring.datasource.password=tfg_un1r_PWD
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# Dialecto de Hibernate (opcional, Spring Boot lo autodetecta)
spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect
```

## 3. Contenedores (Docker / Podman)

### MariaDB con Docker Compose

Si deseas volver a usar MariaDB en tu `docker-compose.yml`:

```yaml
services:
  maria_db:
    container_name: maria_db
    image: "mariadb:latest"
    ports:
      - "3306:3306"
    environment:
      - MARIADB_ROOT_PASSWORD=mypass
      - MARIADB_DATABASE=tfg_unir
      - MARIADB_USER=user_tfg
      - MARIADB_PASSWORD=tfg_un1r_PWD
    volumes:
      - maria-db-data:/var/lib/mysql
      - ../recursos/db/create.mariadb.sql:/docker-entrypoint-initdb.d/01-create.sql
      - ../recursos/db/dump.mariadb.sql:/docker-entrypoint-initdb.d/02-create.sql

volumes:
  maria-db-data:
```

### Ejecutar con Docker CLI

```bash
docker run --name maria_db \
  -e MARIADB_ROOT_PASSWORD=mypass \
  -e MARIADB_DATABASE=tfg_unir \
  -e MARIADB_USER=user_tfg \
  -e MARIADB_PASSWORD=tfg_un1r_PWD \
  -p 3306:3306 \
  -v $(pwd)/../recursos/db/create.mariadb.sql:/docker-entrypoint-initdb.d/01-create.sql \
  -v $(pwd)/../recursos/db/dump.mariadb.sql:/docker-entrypoint-initdb.d/02-create.sql \
  -d mariadb:latest
```

## 4. Scripts SQL Legacy

Los scripts de inicialización específicos para MariaDB se encuentran en:
- `../recursos/db/create.mariadb.sql`
- `../recursos/db/dump.mariadb.sql`
