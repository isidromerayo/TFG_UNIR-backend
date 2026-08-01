#!/bin/sh
# docker-entrypoint.sh
#
# Usa exec para reemplazar el proceso shell por la JVM directamente.
# Esto garantiza que las señales del sistema (SIGTERM, SIGINT) lleguen
# al proceso Java, permitiendo el graceful shutdown de Spring Boot.
#
# Sin exec, sh intercepta SIGTERM pero no lo reenvía a la JVM,
# lo que provoca que el contenedor espere el timeout antes de forzar la parada.

exec java $JAVA_OPTS -jar /app/app.jar "$@"
