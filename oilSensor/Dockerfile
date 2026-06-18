FROM gradle:8.8-jdk21 AS build

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew :oilSensor:installDist --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/oilSensor/build/install/oilSensor /app

ENTRYPOINT ["bin/oilSensor"]