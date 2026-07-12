# syntax=docker/dockerfile:1
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2,sharing=locked \
    mvn dependency:go-offline -B
COPY src ./src
RUN --mount=type=cache,target=/root/.m2,sharing=locked \
    mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENV JAVA_OPTS="-Xms128m -Xmx320m -XX:+UseSerialGC -XX:TieredStopAtLevel=1 -Dspring.jmx.enabled=false"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
