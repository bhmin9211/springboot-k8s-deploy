# -------- Gradle 빌더 단계 --------
FROM openjdk:17-slim AS builder

# Gradle 설치
RUN apt-get update && apt-get install -y wget unzip && \
    wget https://services.gradle.org/distributions/gradle-8.5-bin.zip && \
    unzip gradle-8.5-bin.zip -d /opt && \
    ln -s /opt/gradle-8.5/bin/gradle /usr/bin/gradle

WORKDIR /app

# 빌드 관련 파일만 먼저 복사 (캐시 활용)
COPY springboot-app/gradle /app/gradle
COPY springboot-app/build.gradle /app/
COPY springboot-app/settings.gradle /app/
COPY springboot-app/gradlew /app/
COPY springboot-app/gradlew.bat /app/

# 의존성 캐시 생성
RUN ./gradlew build --dry-run || true

# 전체 복사 및 빌드
COPY springboot-app /app
RUN ./gradlew build

# -------- 실행 단계 --------
FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]

