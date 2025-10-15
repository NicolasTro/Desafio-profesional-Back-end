FROM maven:3.9.4 AS builder
WORKDIR /app
COPY . .
RUN mvn -pl ./cards-service -am -DskipTests package spring-boot:repackage

FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAVA_OPTS=""
COPY --from=builder /app/cards-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
