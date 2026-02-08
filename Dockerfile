# Optimized Dockerfile for TFG UNIR Backend - Uses pre-compiled JAR
# Build time in Render: ~30s (vs 5-10min with Maven build)
FROM docker.io/eclipse-temurin:21-jre

# Set metadata labels
LABEL org.opencontainers.image.title="TFG UNIR Backend"
LABEL org.opencontainers.image.version="0.5.1-SNAPSHOT"
LABEL org.opencontainers.image.description="Backend API for TFG UNIR"
LABEL org.opencontainers.image.vendor="TFG UNIR"
LABEL org.opencontainers.image.licenses="MIT"

# Build arguments
ARG USER=appuser
ARG UID=1001
ARG GID=1001

# Install curl for health check and create non-root user
RUN apt-get update && \
    apt-get install -y curl && \
    apt-get clean && \
    groupadd -g "$GID" "$USER" && \
    useradd -u "$UID" -g "$GID" -s /bin/false "$USER"

# Set working directory
WORKDIR /app

# JVM optimization flags for production
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseContainerSupport -XX:InitialRAMPercentage=50 -XX:MaxRAMPercentage=75"

# Production profile
ENV SPRING_PROFILES_ACTIVE=prod

# Expose application port
EXPOSE 8080

# Health check using curl (more reliable than wget in JRE images)
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Copy pre-compiled JAR (built locally with: ./mvnw clean package -DskipTests)
COPY target/backend.jar /app/app.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
