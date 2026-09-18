# ─── Stage 1: Build ──────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /build

COPY pom.xml ./pom.xml
COPY report-service/pom.xml ./report-service/pom.xml

RUN mvn dependency:go-offline -pl report-service -am -q

COPY report-service/src ./report-service/src
RUN mvn clean package -pl report-service -am -DskipTests -q

# ─── Stage 2: Runtime ────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S smms && adduser -S smms -G smms
USER smms

COPY --from=build /build/report-service/target/report-service-*.jar app.jar

EXPOSE ${REPORT_PORT:-8086}

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
