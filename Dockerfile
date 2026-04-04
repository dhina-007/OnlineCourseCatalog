# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

COPY pom.xml .
COPY src ./src

RUN ./mvnw -B --no-transfer-progress clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN groupadd --system spring && useradd --system spring --gid spring

COPY --from=build /app/target/CourseCatlogApplication-0.0.1-SNAPSHOT.jar app.jar
RUN chown spring:spring app.jar

USER spring
EXPOSE 8080

ENV JAVA_OPTS=""
# Render/Heroku-style PORT; default 8080 for local docker run
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar --server.port=${PORT:-8080}"]
