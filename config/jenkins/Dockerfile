FROM eclipse-temurin:21-jdk-alpine AS jre-builder
WORKDIR /app

COPY build/libs/*.jar app.jar

RUN jar xf app.jar && \
    jdeps --ignore-missing-deps -q \
    --recursive \
    --multi-release 21 \
    --print-module-deps \
    --class-path 'BOOT-INF/lib/*' \
    app.jar > deps.info && \
    jlink \
    --add-modules $(cat deps.info) \
    --strip-debug \
    --compress zip-6 \
    --no-header-files \
    --no-man-pages \
    --output /custom-jre

FROM alpine:latest
WORKDIR /app
ENV JAVA_HOME=/opt/java
ENV PATH="${JAVA_HOME}/bin:${PATH}"
RUN apk add --no-cache gcompat libstdc++

COPY --from=jre-builder /custom-jre $JAVA_HOME
COPY --from=jre-builder /app/app.jar ./app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]