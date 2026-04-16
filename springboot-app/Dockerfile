FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

COPY springboot-app/gradlew .
COPY springboot-app/gradle ./gradle
COPY springboot-app/build.gradle .
COPY springboot-app/settings.gradle .
COPY springboot-app/gradle.properties ./
RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon || true

COPY springboot-app/src ./src

RUN ./gradlew clean bootJar -x test --no-daemon --stacktrace --info

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
