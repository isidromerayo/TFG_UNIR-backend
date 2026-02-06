# Multi-stage Dockerfile for TFG UNIR Backend
# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Final image
FROM docker.io/eclipse-temurin:21-jre

# Set metadata labels
LABEL org.opencontainers.image.title="TFG UNIR Backend"
LABEL org.opencontainers.image.version="0.4.2"
LABEL org.opencontainers.image.description="Backend API for TFG UNIR"
LABEL org.opencontainers.image.vendor="TFG UNIR"
LABEL org.opencontainers.image.licenses="MIT"

# Build arguments
ARG USER=appuser
ARG UID=1001
ARG GID=1001

# Create non-root user
RUN groupadd -g $GID $USER && \
    useradd -u $UID -g $GID -s /bin/false $USER

# Set working directory
WORKDIR /app

# JVM optimization flags for production
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseContainerSupport -XX:InitialRAMPercentage=50 -XX:MaxRAMPercentage=75"

# Production profile
ENV SPRING_PROFILES_ACTIVE=prod

# Expose application port
EXPOSE 8080

# Health check (if wget is available in the base image, otherwise use curl or remove)
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application
COPY --from=build /build/target/backend.jar /app/app.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
