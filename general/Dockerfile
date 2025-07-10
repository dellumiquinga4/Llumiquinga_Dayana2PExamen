# Dockerfile multi-stage para aplicación Spring Boot

FROM maven:3.9.6-openjdk-21-slim AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
RUN mvn dependency:go-offline -B


COPY src ./src


RUN mvn clean package -DskipTests -B


FROM openjdk:21-jdk-slim


LABEL maintainer="banquito-team@banco.com"
LABEL description="BanQuito General Banking Microservice"
LABEL version="1.0.0"


RUN groupadd -r banquito && useradd -r -g banquito banquito

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*


WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

RUN chown -R banquito:banquito /app

USER banquito

EXPOSE 8080

ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    SPRING_PROFILES_ACTIVE=docker \
    SERVER_PORT=8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"] 