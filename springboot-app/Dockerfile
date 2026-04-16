FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Gradle wrapper 및 설정 파일 먼저 복사
COPY springboot-app/gradlew /app/
COPY springboot-app/gradle /app/gradle
COPY springboot-app/build.gradle /app/
COPY springboot-app/settings.gradle /app/

RUN chmod +x gradlew

# 의존성 캐시 레이어
RUN ./gradlew dependencies --no-daemon || true

# 전체 소스 복사
COPY springboot-app /app

# 빌드
RUN ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
