# Multi-stage Dockerfile for TFG UNIR Backend
# Stage 1: Build the application
FROM docker.io/eclipse-temurin:21-jdk AS builder

# Set metadata labels
LABEL org.opencontainers.image.title="TFG UNIR Backend"
LABEL org.opencontainers.image.version="0.4.2"
LABEL org.opencontainers.image.description="Backend API for TFG UNIR"
LABEL org.opencontainers.image.vendor="TFG UNIR"
LABEL org.opencontainers.image.licenses="MIT"

# Set working directory
WORKDIR /app

# Copy pom.xml and dependencies
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies (cache layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests -Dmaven.javadoc.skip=true

# Stage 2: Create production image
FROM docker.io/eclipse-temurin:21-jre

# Build arguments
ARG USER=appuser
ARG UID=1001
ARG GID=1001

# Create non-root user
RUN groupadd -g $GID $USER && \
    useradd -u $UID -g $GID -s /bin/false $USER

# Set working directory
WORKDIR /app

# Copy jar file from builder stage
COPY --from=builder /app/target/backend.jar app.jar

# Set ownership
RUN chown -R $USER:$USER /app

# Switch to non-root user
USER $USER

# JVM optimization flags for production
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseContainerSupport -XX:InitialRAMPercentage=50 -XX:MaxRAMPercentage=75"

# Production profile
ENV SPRING_PROFILES_ACTIVE=prod

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
