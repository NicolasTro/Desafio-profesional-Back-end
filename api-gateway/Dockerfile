FROM maven:3.9.4 AS builder
WORKDIR /app
# copy entire repository so parent POM is available
COPY . .
# build only this module (module path relative to repo root)
RUN mvn -pl ./api-gateway -am -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAVA_OPTS=""
# copy the module jar
COPY --from=builder /app/api-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
