FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/order-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]

#
## ===========================
## Build Stage
## ===========================
#FROM maven:3.9.9-eclipse-temurin-21 AS builder
#
#WORKDIR /app
#
## Copy pom.xml first to leverage Docker cache
#COPY . .
#
## Download dependencies
#RUN mvn dependency:go-offline
#
## Copy source code
#COPY src ./src
#
## Build application
#RUN mvn clean package -DskipTests
#
## ===========================
## Runtime Stage
## ===========================
#FROM eclipse-temurin:21-jre
#
#WORKDIR /app
#
#COPY --from=builder /app/target/*.jar app.jar
#
#EXPOSE 8081
#
#ENTRYPOINT ["java","-jar","app.jar"]