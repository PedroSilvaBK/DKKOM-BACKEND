FROM gradle:8.10.2-jdk21
WORKDIR /opt/app
COPY ./build/libs/Messaging_Service-0.0.1-SNAPSHOT.jar ./

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar Messaging_Service-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod"]