FROM docker.io/eclipse-temurin:21-jre

# Set metadata labels
LABEL org.opencontainers.image.title="TFG UNIR Backend"
LABEL org.opencontainers.image.version="0.4.0"
LABEL org.opencontainers.image.description="Backend API for TFG UNIR"
LABEL org.opencontainers.image.vendor="TFG UNIR"
LABEL org.opencontainers.image.licenses="MIT"

# Build arguments
ARG JAR_FILE=target/backend.jar
ARG USER=appuser
ARG UID=1001
ARG GID=1001

# Create non-root user
RUN groupadd -g $GID $USER && \
    useradd -u $UID -g $GID -s /bin/false $USER

# Set working directory
WORKDIR /app

# Copy application jar
COPY $JAR_FILE app.jar

# Set ownership
RUN chown -R $USER:$USER /app

# Switch to non-root user
USER $USER

# JVM optimization flags
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
