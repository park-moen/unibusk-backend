# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x gradlew && ./gradlew clean build -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache curl tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone

ENV TZ=Asia/Seoul

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=builder --chown=spring:spring /app/build/libs/app.jar app.jar

HEALTHCHECK --start-period=30s --interval=10s --timeout=5s --retries=5 \
  CMD sh -c 'curl -f http://localhost:8080/api/actuator/health || exit 1'

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
