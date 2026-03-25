FROM gradle:8.12-jdk21 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle/libs.versions.toml ./gradle/
RUN gradle --no-daemon dependencies

COPY config ./config
COPY src ./src

RUN gradle --no-daemon clean build -x test

RUN jar xf /app/build/libs/*.jar

RUN jdeps --ignore-missing-deps -q \
	--recursive \
	--multi-release 21 \
	--print-module-deps \
	--class-path 'BOOT-INF/lib/*' \
	/app/build/libs/*.jar > deps.info

RUN jlink \
	--add-modules $(cat deps.info) \
	--strip-debug \
	--compress zip-6 \
	--no-header-files \
	--no-man-pages \
	--output /custom-jre

FROM alpine:3.23
WORKDIR /app

ENV JAVA_HOME=/opt/java
ENV PATH="${JAVA_HOME}/bin:${PATH}"

COPY --from=build /custom-jre $JAVA_HOME
COPY --from=build /app/build/libs/*.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
