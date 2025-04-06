FROM openjdk:17-slim AS builder

# Install Gradle
RUN apt-get update && apt-get install -y wget unzip && \
    wget https://services.gradle.org/distributions/gradle-8.5-bin.zip && \
    unzip gradle-8.5-bin.zip -d /opt && \
    ln -s /opt/gradle-8.5/bin/gradle /usr/bin/gradle

WORKDIR /app

# Copy only necessary files first for cache
COPY springboot-app/build.gradle /app/
COPY springboot-app/settings.gradle /app/
COPY springboot-app/gradle /app/gradle

# Cache dependencies
RUN gradle dependencies || true

# Copy entire project and build
COPY springboot-app /app
RUN gradle build

# Final runtime image
FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
