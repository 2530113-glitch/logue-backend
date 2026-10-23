FROM openjdk:17-jdk-slim
EXPOSE 8080
COPY build/libs/logue-backend-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
