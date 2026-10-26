FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean installDist -x test

EXPOSE 8080

CMD ["./build/install/logue-backend/bin/logue-backend"]