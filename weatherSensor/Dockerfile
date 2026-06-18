FROM gradle:8.8-jdk21 AS build

WORKDIR /app

COPY . .

RUN ./gradlew :weatherSensor:installDist --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/weatherSensor/build/install/weatherSensor /app

ENTRYPOINT ["bin/weatherSensor"]