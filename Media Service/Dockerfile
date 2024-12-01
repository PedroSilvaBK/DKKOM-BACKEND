FROM gradle:8.10.2-jdk21
WORKDIR /opt/app
COPY ./build/libs/Media_Service-0.0.1-SNAPSHOT.jar ./

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar Media_Service-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod"]


