FROM gradle:8.10.2-jdk21

WORKDIR /opt/app

# Copy your Spring Boot JAR
COPY ./build/libs/api_gateway-0.0.1-SNAPSHOT.jar ./api_gateway.jar

# Copy additional resources (e.g., JWT certs)
COPY ./src/main/resources/jwt_certs ./jwt_certs

# Download the OpenTelemetry Java Agent (v2.11.0)
RUN wget -q https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.11.0/opentelemetry-javaagent.jar

# ---------------------------
#    OpenTelemetry Config
# ---------------------------
ENV OTEL_TRACES_EXPORTER=otlp
ENV OTEL_EXPORTER_OTLP_ENDPOINT="http://otel-collector:4318"
ENV OTEL_PROJECT_ID=dkkom-446515
ENV OTEL_RESOURCE_ATTRIBUTES=service.name=api-gateway
ENV OTEL_LOGS_EXPORTER=none
ENV OTEL_METRICS_EXPORTER=none

# Expose your app port
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} \
  -javaagent:/opt/app/opentelemetry-javaagent.jar \
  -jar /opt/app/api_gateway.jar \
  --spring.profiles.active=prod"]
