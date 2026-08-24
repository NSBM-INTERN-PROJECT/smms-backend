FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /build
COPY pom.xml ./pom.xml
COPY allocation-service/pom.xml ./allocation-service/pom.xml
RUN mvn dependency:go-offline -pl allocation-service -am -q
COPY allocation-service/src ./allocation-service/src
RUN mvn clean package -pl allocation-service -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S smms && adduser -S smms -G smms
USER smms
COPY --from=build /build/allocation-service/target/allocation-service-*.jar app.jar
EXPOSE ${ALLOC_PORT:-8083}
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]
