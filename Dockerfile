FROM maven:3.8.6-eclipse-temurin-17 AS builder

WORKDIR /workspace
COPY . .

RUN mvn -N dependency:go-offline -B

RUN mvn -f pom.xml -pl ./config-server clean package -DskipTests
RUN mvn -f pom.xml -pl ./discovery-service clean package -DskipTests
RUN mvn -f pom.xml -pl ./gateway-service clean package -DskipTests
RUN mvn -f pom.xml -pl ./notification-service clean package -DskipTests
RUN mvn -f pom.xml -pl ./user-service clean package -DskipTests

FROM eclipse-temurin:23-jdk as config-server
RUN apt-get update && apt-get install -y curl
COPY --from=builder /workspace/config-server/target/*.jar /app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:23-jdk as discovery-service
COPY --from=builder /workspace/discovery-service/target/*.jar /app.jar
EXPOSE 8671
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:23-jdk as notification-service
COPY --from=builder /workspace/notification-service/target/*.jar /app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:23-jdk as user-service
COPY --from=builder /workspace/user-service/target/*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:23-jdk as gateway-service
COPY --from=builder /workspace/gateway-service/target/*.jar /app.jar
EXPOSE 80
ENTRYPOINT ["java", "-jar", "/app.jar"]