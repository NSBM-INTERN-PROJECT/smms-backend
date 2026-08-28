# SMMS Report Service Starter

This is a clean Java 21 and Spring Boot 3.3.x starting point for the SMMS analytics and
exports backend. It intentionally contains only the parent project and the assigned
`report-service` module. The Team Lead can later add the remaining modules to the parent POM.

## Implemented milestone

- Owner dashboard aggregation through OpenFeign
- CSV, Excel, and PDF dashboard exports
- JWT parsing and `OWNER` role checks
- Standard `ApiError` responses
- Swagger/OpenAPI documentation
- Eureka and Config Server integration
- JUnit 5 and Mockito tests
- JaCoCo 60% service-layer coverage rule
- Postman collection

## 1. Prerequisites

- JDK 21
- Maven 3.9+
- Running Config Server on port 8888 (optional during early local work)
- Running Eureka Server on port 8761
- User, allocation, meeting, and session services implementing `API_CONTRACTS.md`

## 2. Configure environment variables

Copy `.env.example` to `.env` for Docker-based development. When running from IntelliJ,
add the variables to the Spring Boot run configuration. Never commit `.env`.

Required security value:

```text
JWT_SECRET=<the same 64-character hexadecimal secret used by auth-service and gateway>
```

## 3. Build and test

From the `smms-backend` directory:

```bash
mvn clean verify
```

## 4. Run locally

```bash
mvn -pl report-service spring-boot:run
```

The service starts on `http://localhost:8086`.

## 5. Open documentation

- Swagger UI: `http://localhost:8086/swagger-ui.html`
- Health: `http://localhost:8086/actuator/health`

## 6. Test endpoints

Use an access token containing the `OWNER` role:

```text
GET /api/reports/dashboard
GET /api/reports/dashboard/exports/csv
GET /api/reports/dashboard/exports/excel
GET /api/reports/dashboard/exports/pdf
```

Until the four required downstream services are running, dashboard requests correctly
return `503 DOWNSTREAM_SERVICE_UNAVAILABLE`.

## 7. Team integration

1. Confirm the endpoint shapes in `API_CONTRACTS.md` with the other service owners.
2. Place this `report-service` folder in the official `smms-backend` repository.
3. Ask the Team Lead to add `<module>report-service</module>` to the official parent POM.
4. Work on branch `service/report-service`.
5. Keep the Postman collection and Swagger documentation current.
