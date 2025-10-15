FROM maven:3.9.4 AS builder
WORKDIR /app
COPY . .
RUN mvn -pl ./transactions-service -am -DskipTests package spring-boot:repackage

FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAVA_OPTS=""
COPY --from=builder /app/transactions-service/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
