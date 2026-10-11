# Etapa 1: Build con Maven y Java 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# Etapa 2: Imagen liviana de ejecución
FROM amazoncorretto:21-alpine-jdk
WORKDIR /app

COPY --from=builder /build/target/*.war /app/api-v1.war

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/api-v1.war"]