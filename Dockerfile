# Etapa 1: Construcción del JAR con Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final ligera
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Instalar curl y netcat para la espera activa
RUN apk add --no-cache curl netcat-openbsd

COPY --from=build /app/target/*.jar app.jar

# Buscador expone el puerto 8081
EXPOSE 8081

# Activar el perfil docker
ENV SPRING_PROFILES_ACTIVE=docker

# Esperar a que Elasticsearch responda antes de arrancar
ENTRYPOINT ["sh", "-c", "until curl -s http://elasticsearch:9200/_cluster/health | grep -E '\"status\":\"(yellow|green)\"'; do echo 'Esperando a cluster verde o amarillo...'; sleep 5; done; echo 'Cluster listo!'; java -jar app.jar"]
