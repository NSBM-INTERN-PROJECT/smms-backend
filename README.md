# SMMS Backend Services ⚙️

This repository contains the backend microservices for the **Student Mentoring Management System (SMMS)**. This is part of a dual-repo architecture; the frontend React application is maintained in a separate repository.

We are utilizing an **API-First** development strategy. All backend contracts (endpoints, requests, and responses) must be finalized and documented here before frontend integration begins.

## 🏗️ Architecture & Stack

This project is structured as a Maven multi-module workspace utilizing the **Database-per-Service** pattern.

* **Language:** Java 21 LTS
* **Framework:** Spring Boot 3.3.x
* **Cloud Infrastructure:** Spring Cloud 2023.0.x (Eureka, API Gateway, Config Server)
* **Database:** MySQL 8 Community Edition
* **Security:** JWT (JSON Web Tokens) + Spring Security 6
* **Containerization:** Docker + Docker Compose

## 📂 Repository Structure

The project contains 9 dedicated modules:

* `discovery-server`: Netflix Eureka service registry
* `config-server`: Centralized configuration management
* `api-gateway`: Spring Cloud Gateway with JWT authentication filters
* `auth-service`: OTP-based login, JWT issuance, and audit logs
* `user-service`: Mentor and student profile management
* `allocation-service`: Manual and random mentor-student allocation engine
* `meeting-service`: Scheduling, attendance, and email notifications
* `session-service`: Session notes, progress tracking, and escalations
* `report-service`: Data aggregation for dashboards and CSV/PDF exports

## 🚀 Getting Started (Local Development)

### Prerequisites

* Java 21 installed
* Maven 3.9+ installed
* Docker and Docker Compose installed

### 1. Environment Variables Setup

Never commit sensitive credentials to version control.

1. Copy the example environment file:

   ```bash
   cp .env.example .env
   ```

2. Open `.env` and populate it with the database credentials and JWT secrets shared by the team lead.

### 2. Start the Infrastructure

Before running the microservices, you must start the MySQL database and the discovery/config servers. Ensure your Docker daemon is active (e.g., `sudo systemctl start docker` on Linux environments) and run:

```bash
docker compose up mysql discovery-server config-server -d
```

### 3. Run the Microservices

You can run individual services using your IDE (IntelliJ IDEA / VS Code) or via Maven. Ensure the API Gateway and the specific service you are working on are running.

```bash
# Example: Running the auth-service
mvn spring-boot:run -pl auth-service
```

## 🔐 Git & Branching Rules

* **No Direct Commits:** Direct pushes to `main` and `develop` are restricted.
* **Service Branches:** Create feature branches off your dedicated service branch (e.g., `service/auth-service`).
* **Pull Requests:** All code must be reviewed before merging into `develop`.
* **Commit Format:** `<type>(<service-name>): <description>` (e.g., `feat(meeting-service): add bulk slot creation endpoint`).

## 📚 API Documentation

Once the services are running locally, Swagger/OpenAPI documentation is available for each module to guide frontend integration.

* API Gateway routes all requests via `http://localhost:8080/api/`