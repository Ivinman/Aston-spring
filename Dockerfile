# Этап сборки
FROM maven:3.8.6-eclipse-temurin-17 AS builder

WORKDIR /workspace
COPY . .

RUN mvn -N dependency:go-offline -B

# 2. Собираем модули в правильном порядке (сначала discovery, потом gateway)
RUN mvn -f pom.xml -pl ./config-server clean package -DskipTests
RUN mvn -f pom.xml -pl ./discovery-service clean package -DskipTests
RUN mvn -f pom.xml -pl ./user-service clean package -DskipTests
RUN mvn -f pom.xml -pl ./notification-service clean package -DskipTests

RUN cd gateway-service && \
    mvn -f pom.xml clean package -DskipTests && \
    cp target/*.jar ../gateway-service.jar

# Образы для каждого сервиса (без изменений)
FROM eclipse-temurin:23-jdk as config-server
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
COPY --from=builder /workspace/gateway-service.jar /app.jar
EXPOSE 80
ENTRYPOINT ["java", "-jar", "/app.jar"]