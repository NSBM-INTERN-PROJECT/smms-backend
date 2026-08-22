FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /build
COPY pom.xml ./pom.xml
COPY user-service/pom.xml ./user-service/pom.xml
RUN mvn dependency:go-offline -pl user-service -am -q
COPY user-service/src ./user-service/src
RUN mvn clean package -pl user-service -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S smms && adduser -S smms -G smms
USER smms
COPY --from=build /build/user-service/target/user-service-*.jar app.jar
EXPOSE ${USER_PORT:-8082}
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]
