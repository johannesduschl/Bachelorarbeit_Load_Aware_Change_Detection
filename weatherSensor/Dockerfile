FROM gradle:8.8-jdk21 AS build

WORKDIR /app

COPY src .

RUN gradle installDist --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/build/install/weatherSensor ./

CMD ["bin/weatherSensor"]