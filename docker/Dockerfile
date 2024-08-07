FROM gradle:8.9.0-jdk17 AS builder

WORKDIR /app

COPY build.gradle settings.gradle ./

COPY src ./src

RUN gradle build --no-daemon

FROM openjdk:17-jdk

WORKDIR /app

COPY --from=builder /app/build/libs/market-api*.jar ./app.jar

CMD ["java", "-jar", "app.jar"]
