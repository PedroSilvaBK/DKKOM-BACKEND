FROM gradle:8.10.2-jdk21
WORKDIR /opt/app
COPY ./build/libs/Authentication_Service-0.0.1-SNAPSHOT.jar ./

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar Authentication_Service-0.0.1-SNAPSHOT.jar"]


