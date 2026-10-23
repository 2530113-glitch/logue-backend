FROM eclipse-temurin:17-jdk-jammy
EXPOSE 8080
COPY backend-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

