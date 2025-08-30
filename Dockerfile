# Multi-stage build for production OCPP CSMS
FROM openjdk:17-jdk-slim as builder

WORKDIR /app

# Copy Maven wrapper and configuration
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies for better caching
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src/ src/

# Build application
RUN ./mvnw clean package -DskipTests -B

# Production stage
FROM openjdk:17-jre-slim

# Install required packages for production
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Create app user
RUN groupadd -r ocpp && useradd -r -g ocpp ocpp

# Set working directory
WORKDIR /app

# Copy application jar
COPY --from=builder /app/target/*.jar app.jar

# Copy additional resources if needed
COPY --from=builder /app/src/main/resources/db/migration/ /app/db/migration/

# Change ownership
RUN chown -R ocpp:ocpp /app

# Switch to non-root user
USER ocpp

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose ports
EXPOSE 8080

# Set JVM options for production
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -XX:+UseStringDeduplication -XX:+OptimizeStringConcat"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]