FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /opt/app

COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline

COPY ./src ./src
COPY .env ./

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /opt/app
EXPOSE 8080

COPY --from=builder /opt/app/target/*.jar app.jar
COPY --from=builder /opt/app/.env ./

ENTRYPOINT ["java", "-jar", "app.jar"]