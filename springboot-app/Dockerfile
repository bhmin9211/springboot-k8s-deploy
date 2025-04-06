FROM openjdk:17-slim AS builder

RUN apt-get update && apt-get install -y wget unzip && \
    wget https://services.gradle.org/distributions/gradle-8.5-bin.zip && \
    unzip gradle-8.5-bin.zip -d /opt && \
    ln -s /opt/gradle-8.5/bin/gradle /usr/bin/gradle

WORKDIR /app

# 최소한의 파일만 복사
COPY springboot-app/build.gradle /app/
COPY springboot-app/settings.gradle /app/

# 의존성 캐시 시도 (없어도 실행됨)
RUN gradle dependencies || true

# 전체 복사 및 빌드
COPY springboot-app /app
RUN gradle build

FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]

