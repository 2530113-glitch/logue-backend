# Paso 1: Descargar Gradle y compilar el proyecto en la nube
FROM gradle:8.5-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew build --no-daemon -x test

# Paso 2: Usar una imagen oficial de Java para arrancar el servidor
FROM eclipse-temurin:17-jdk-jammy
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/backend.jar
ENTRYPOINT ["java", "-jar", "/app/backend.jar"]
