FROM openjdk:17-slim

# Gradle 설치
RUN apt-get update && \
    apt-get install -y wget unzip && \
    wget https://services.gradle.org/distributions/gradle-8.5-bin.zip && \
    unzip gradle-8.5-bin.zip -d /opt && \
    ln -s /opt/gradle-8.5/bin/gradle /usr/bin/gradle

COPY . /app
WORKDIR /app
RUN gradle build

CMD ["java", "-jar", "build/libs/springboot-gradle-template-0.0.1-SNAPSHOT.jar"]
