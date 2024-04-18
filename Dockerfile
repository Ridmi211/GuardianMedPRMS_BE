FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY ./target/PrescriptionSystem-0.0.1-SNAPSHOT.jar .
EXPOSE 8090
CMD ["java", "-jar", "PrescriptionSystem-0.0.1-SNAPSHOT.jar"]