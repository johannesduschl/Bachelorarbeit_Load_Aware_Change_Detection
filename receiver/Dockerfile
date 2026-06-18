FROM gradle:8.8-jdk21 AS build

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew :receiver:installDist --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/receiver/build/install/receiver /app

ENTRYPOINT ["bin/receiver"]