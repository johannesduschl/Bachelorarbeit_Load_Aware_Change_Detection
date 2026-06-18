FROM gradle:8.8-jdk21 AS build

WORKDIR /app

COPY src/main .

RUN gradle installDist --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/build/install/receiver ./

CMD ["bin/receiver"]