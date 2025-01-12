FROM gradle:8.10.2-jdk21
WORKDIR /opt/app
COPY ./build/libs/PermissionsService-0.0.1-SNAPSHOT.jar ./

# Download the OpenTelemetry Java Agent (v2.11.0)
RUN wget -q https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.11.0/opentelemetry-javaagent.jar

# Download the Cloud Profiler native library (example link, check the docs for the latest)
# Download the Cloud Profiler agent (.tar.gz) and extract the .so file
RUN wget -q https://storage.googleapis.com/cloud-profiler/java/latest/profiler_java_agent.tar.gz \
    && tar -xf profiler_java_agent.tar.gz \
    && rm profiler_java_agent.tar.gz



# ---------------------------
#    OpenTelemetry Config
# ---------------------------
ENV OTEL_TRACES_EXPORTER=otlp
ENV OTEL_EXPORTER_OTLP_ENDPOINT="http://otel-collector:4318"
ENV OTEL_PROJECT_ID=dkkom-446515
ENV OTEL_RESOURCE_ATTRIBUTES=service.name=permission-service
ENV OTEL_LOGS_EXPORTER=none
ENV OTEL_METRICS_EXPORTER=none


ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} \
  -agentpath:/opt/app/profiler_java_agent.so=-cprof_service=permission-service,-cprof_project_id=dkkom-446515,-cprof_enable_heap_sampling=true,--logtostderr=1 \
  -javaagent:/opt/app/opentelemetry-javaagent.jar \
  -jar PermissionsService-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod"]