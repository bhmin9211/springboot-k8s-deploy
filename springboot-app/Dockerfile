FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

# 권한 문제 방지 (gradle 유저 사용)
USER root

COPY springboot-app /app


RUN gradle clean bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
