FROM gradle:8.12-jdk21 AS build
ARG DOTENV_PATH=env/.env.example
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle/libs.versions.toml ./gradle/
COPY env/.env.example ./env/
RUN gradle --no-daemon dependencies --stacktrace --info
COPY ${DOTENV_PATH} ./env/.env.example

COPY . .

RUN gradle --no-daemon clean build -x test -x integrationTest

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]