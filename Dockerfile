FROM amazoncorretto:21-alpine-jdk

COPY target/sarh-0.0.1-SNAPSHOT.war /api-v1.war

ENTRYPOINT ["java", "-jar", "/api-v1.war"]