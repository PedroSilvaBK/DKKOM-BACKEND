FROM gradle:8.10.2-jdk21

# Set the working directory
WORKDIR /opt/app

# Copy over your Spring Boot JAR (rename for convenience)
COPY ./build/libs/api_gateway-0.0.1-SNAPSHOT.jar ./api_gateway.jar

# Copy over additional resources (e.g., JWT certs)
COPY ./src/main/resources/jwt_certs ./jwt_certs

# Download the OpenTelemetry Java Agent (example: v1.26.0)
RUN wget -q https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.11.0/opentelemetry-javaagent.jar

# ---------------------------
#    OpenTelemetry Config
# ---------------------------

# Set environment variables for Google Cloud Exporter
# Adjust these as needed for your environment:
ENV OTEL_TRACES_EXPORTER=otlp
ENV OTEL_EXPORTER_OTLP_ENDPOINT="http://otel-collector:4317"
ENV OTEL_RESOURCE_ATTRIBUTES=none

# The GCP project ID (make sure this matches your actual project)
ENV OTEL_PROJECT_ID=dkkom-446515

# The service name you want to show up in Google Cloud Trace
ENV OTEL_RESOURCE_ATTRIBUTES=service.name=api-gateway

# If you're running outside Google Cloud, you may need a service account JSON file.
# By default, you wouldn't bake credentials into the image. Instead, you'd mount
# them or rely on environment config. For example:
# ENV GOOGLE_APPLICATION_CREDENTIALS=/opt/app/gcp-service-account.json
#
# (If running in GKE, Cloud Run, GCE, etc., you typically don't need to supply
# a separate key file, as the built-in service account is used.)

# Expose the application port
EXPOSE 8080

# Use ENTRYPOINT to run Java with the OTel agent
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} \
  -javaagent:/opt/app/opentelemetry-javaagent.jar \
  -jar /opt/app/api_gateway.jar \
  --spring.profiles.active=prod"]
