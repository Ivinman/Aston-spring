# FROM maven:3.8.6-eclipse-temurin-17 AS builder
# WORKDIR /workspace
# COPY gateway-service/pom.xml ./pom.xml
# RUN mvn dependency:go-offline -B
# COPY gateway-service/src ./src/
# RUN mvn package -DskipTests
#
# FROM eclipse-temurin:23-jdk
# COPY --from=builder /workspace/target/*.jar /app.jar
# ENTRYPOINT ["java", "-jar", "/app.jar"]