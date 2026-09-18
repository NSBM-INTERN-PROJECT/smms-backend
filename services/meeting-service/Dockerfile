FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /build
COPY pom.xml ./pom.xml
COPY meeting-service/pom.xml ./meeting-service/pom.xml
RUN mvn dependency:go-offline -pl meeting-service -am -q
COPY meeting-service/src ./meeting-service/src
RUN mvn clean package -pl meeting-service -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S smms && adduser -S smms -G smms
USER smms
COPY --from=build /build/meeting-service/target/meeting-service-*.jar app.jar
EXPOSE ${MEETING_PORT:-8084}
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]
