FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /build
COPY pom.xml ./pom.xml
COPY report-service/pom.xml ./report-service/pom.xml
COPY report-service/src ./report-service/src
RUN mvn clean package -pl report-service -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /build/report-service/target/report-service-*.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]
