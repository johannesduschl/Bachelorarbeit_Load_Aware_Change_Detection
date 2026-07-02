FROM gradle:8.8-jdk21 AS build

WORKDIR /app

COPY . .

RUN gradle :receiver:installDist --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/receiver/build/install/receiver ./

CMD ["bin/receiver"]