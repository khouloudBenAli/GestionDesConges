# Leave Management — Backend Microservices

## Architecture Overview

```
Frontend (Vite :3000)
    │
    ├── /api/employees, /api/departments  ──► Employee Service (:8081)
    │                                              │
    └── /api/leave-*                    ──► Leave Service    (:8082)
                                                   │
                                        (both register with)
                                        Eureka Server (:8761)
                                        (optional) API Gateway (:8085)

Infrastructure (Docker)
    ├── employee-db  PostgreSQL (:5432)  ◄── Employee Service
    ├── leave-db     PostgreSQL (:5433)  ◄── Leave Service
    ├── kafka                   (:9092)  ◄── Employee & Leave Services
    ├── zookeeper               (:2181)  ◄── Kafka
    └── kafka-ui                (:8090)      (browser dashboard)
```

## Port Reference

| Component       | Port | Technology              |
|----------------|------|-------------------------|
| Eureka Server  | 8761 | Spring Boot             |
| Employee Service | 8081 | Spring Boot + JPA + Kafka |
| Leave Service  | 8082 | Spring Boot + JPA + Kafka |
| API Gateway    | 8085 | Spring Cloud Gateway    |
| employee-db    | 5432 | PostgreSQL 16           |
| leave-db       | 5433 | PostgreSQL 16           |
| Kafka          | 9092 | Confluent Kafka 7.6     |
| Zookeeper      | 2181 | Confluent Zookeeper 7.6 |
| Kafka UI       | 8090 | provectuslabs/kafka-ui  |

## Prerequisites

| Tool           | Minimum version | Check command         |
|---------------|-----------------|-----------------------|
| Docker Desktop | 24+             | `docker --version`    |
| Java JDK       | 17              | `java -version`       |
| Maven          | 3.9+            | `mvn --version`       |

## Launch Order

> **The order matters.** Infrastructure must be up before Java services start, and Eureka must be up before the other services register.

### Step 1 — Start Docker infrastructure

```bash
cd leave-management-microservices
docker compose up -d
```

Wait until all containers are healthy:

```bash
docker ps
```

Expected output:

```
NAMES         STATUS
kafka-ui      Up X seconds
kafka         Up X seconds (healthy)
employee-db   Up X seconds (healthy)
leave-db      Up X seconds (healthy)
zookeeper     Up X seconds
```

> Kafka takes ~30 seconds to become healthy after its container starts. Wait for `(healthy)` before moving on.

---

### Step 2 — Start Eureka Server

Open a **new terminal**:

```bash
cd leave-management-microservices/eureka-server
mvn spring-boot:run
```

Wait for this line in the logs:

```
Started EurekaServerApplication in X.XXX seconds
```

Verify: open [http://localhost:8761](http://localhost:8761) — you should see the Eureka dashboard.

---

### Step 3 — Start Employee Service

Open a **new terminal**:

```bash
cd leave-management-microservices/employee-service
mvn spring-boot:run
```

Wait for:

```
Started EmployeeServiceApplication in X.XXX seconds
```

Verify: `curl http://localhost:8081/actuator/health` → `{"status":"UP"}`

---

### Step 4 — Start Leave Service

Open a **new terminal**:

```bash
cd leave-management-microservices/leave-service
mvn spring-boot:run
```

Wait for:

```
Started LeaveServiceApplication in X.XXX seconds
```

Verify: `curl http://localhost:8082/actuator/health` → `{"status":"UP"}`

---

### Step 5 — (Optional) Start API Gateway

The frontend Vite proxy connects directly to employee-service (:8081) and leave-service (:8082), so the gateway is not required for local development. Start it only if you need to test gateway-level routing or JWT auth.

Open a **new terminal**:

```bash
cd leave-management-microservices/api-gateway
mvn spring-boot:run
```

Verify: `curl http://localhost:8085/actuator/health` → `{"status":"UP"}`

---

### Step 6 — Start the Frontend

```bash
cd Leave-Management-frontend-main
npm install        # first time only
npm run dev
```

Open [http://localhost:3000](http://localhost:3000).

---

## Verify Everything is Running

```bash
# Infrastructure
docker ps

# Java services
curl http://localhost:8761/actuator/health   # Eureka
curl http://localhost:8081/actuator/health   # Employee Service
curl http://localhost:8082/actuator/health   # Leave Service

# Data endpoints
curl http://localhost:8081/api/employees
curl http://localhost:8082/api/leave-types
curl http://localhost:8082/api/leave-requests
```

All should return HTTP 200.

---

## Stopping Everything

### Stop Java services

Press `Ctrl+C` in each terminal where a service is running.

### Stop Docker containers

```bash
cd leave-management-microservices
docker compose down
```

To also delete the database volumes (wipes all data):

```bash
docker compose down -v
```

---

## Troubleshooting

### 500 errors from the frontend

The Java services cannot reach their database or Kafka. Check that Docker containers are running and healthy:

```bash
docker ps
docker compose logs leave-db
docker compose logs kafka
```

### "Port already in use" on startup

A previous instance of the service is still running. Find and stop it:

```bash
# Windows PowerShell
netstat -ano | findstr :8081
taskkill /PID <pid> /F

# Linux / Git Bash
lsof -ti:8081 | xargs kill -9
```

### Eureka shows services as DOWN

The services take up to 30 seconds after startup to appear as UP in Eureka. Refresh the dashboard at [http://localhost:8761](http://localhost:8761).

### Kafka consumer errors on startup

Kafka must be fully started before the Java services. Check Kafka health:

```bash
docker inspect kafka --format "{{.State.Health.Status}}"
# must show: healthy
```

If it shows `starting`, wait a few more seconds and try again.

---

## Kafka UI

Browse Kafka topics and messages at [http://localhost:8090](http://localhost:8090).

Topics used by this project:

| Topic            | Producer         | Consumer       |
|-----------------|------------------|----------------|
| `employee-events` | Employee Service | Leave Service  |
| `leave-events`    | Leave Service    | (notifications)|
