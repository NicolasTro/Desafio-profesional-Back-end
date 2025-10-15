FROM maven:3.9.4 AS builder
WORKDIR /app
COPY . .
RUN mvn -pl ./users-service -am -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAVA_OPTS=""
COPY --from=builder /app/users-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
