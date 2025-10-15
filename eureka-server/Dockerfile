# Multi-stage Dockerfile for eureka-server
FROM maven:3.9.4 AS builder
WORKDIR /app
# copy entire repo so parent POM is available
COPY . .
# build only this module
RUN mvn -pl ./eureka-server -am -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/eureka-server/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app/app.jar"]
