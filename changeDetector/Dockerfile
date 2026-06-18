FROM gradle:8.8-jdk21 AS build

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew :changeDetector:installDist --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/changeDetector/build/install/changeDetector /app

ENTRYPOINT ["bin/changeDetector"]