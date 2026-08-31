FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /build
COPY pom.xml ./pom.xml
COPY session-service/pom.xml ./session-service/pom.xml
RUN mvn dependency:go-offline -pl session-service -am -q
COPY session-service/src ./session-service/src
RUN mvn clean package -pl session-service -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S smms && adduser -S smms -G smms
USER smms
COPY --from=build /build/session-service/target/session-service-*.jar app.jar
EXPOSE ${SESSION_PORT:-8085}
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]
