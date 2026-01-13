# Digital Library Platform

A Java Spring Boot microservices-based Digital Library Platform to manage book inventory and lending workflows using real-time service communication and event-driven processing.

---

## Table of Contents
- [Architecture Overview](#architecture-overview)
- [Microservices](#microservices)
  - [API Gateway](#api-gateway)
  - [Borrowing Service](#borrowing-service)
  - [Inventory Service](#inventory-service)
- [Technology Stack](#technology-stack)
- [Prerequisites (Windows)](#prerequisites-windows)
- [Running Infrastructure on Windows](#running-infrastructure-on-windows)
- [Running the Microservices (Local)](#running-the-microservices-local)
- [Testing the System (Local)](#testing-the-system-local)
- [Troubleshooting & Notes](#troubleshooting--notes)

---

## Architecture Overview
- Microservices architecture with a centralized **API Gateway**.
- Client requests route through the gateway to backend services.
- **Borrowing Service** queries **Inventory Service** (via Spring WebClient) and publishes domain events to **Kafka**.
- Event-driven updates and caching (Redis) for performance.

---

## Microservices

### API Gateway 
**Responsibilities**
- Single entry point for client requests
- Path-based routing to backend services
- Client-side load balancing
- Redis-backed rate limiting
- Centralized health monitoring

**Port:** `8080`

---

### Borrowing Service 
**Responsibilities**
- Handle borrow, return, renewal
- Business rules:
  - Max 2 active books per user
  - 7-day borrowing period
  - One renewal per borrow
  - 30-day cooldown after return
- Validate availability via Inventory Service (WebClient)
- Publish Kafka events after successful DB transactions

**Database:** PostgreSQL  
**Port:** `8082`

---

### Inventory Service 
**Responsibilities**
- Manage inventory and availability
- Track total and available copies
- Consume Kafka events to update inventory
- Cache frequent reads with Redis

**Database:** MongoDB  
**Port:** `8081` (supports multiple instances)

---

## Technology Stack
- Java (recommended 11+)
- Spring Boot (Web, Data, Cloud Gateway, Actuator)
- Spring Data JPA
- Spring WebClient
- Apache Kafka
- PostgreSQL
- MongoDB
- Redis

---

## Prerequisites (Windows) 
Install and run:
- Java 11+ (JDK)
- Maven
- Apache Kafka (Windows setup — use Windows scripts or WSL)
- Redis (Windows build or WSL)
- PostgreSQL
- MongoDB


---

## Running Infrastructure on Windows

### Apache Kafka
(If using Kafka with Windows binaries or WSL)

- With Kafka Windows scripts (from Kafka `bin/windows`):

# Start Kafka broker
.\bin\windows\kafka-server-start.bat .\config\server.properties
```
- Or use KRaft-mode Kafka (no Zookeeper) per your Kafka version docs.

Kafka default: `localhost:9092`

---

### Redis (Local)
- If installed as service:

```powershell
# Start Redis (or run redis-server.exe from the install folder)
redis-server.exe
# Verify
redis-cli.exe ping
# Expected:
# PONG
```

- Or run via WSL: `redis-server` / `redis-cli ping`

---

### PostgreSQL (Borrowing Service)
- Create DB (psql):

```sql
CREATE DATABASE borrowing_db;
```
- Example `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/borrowing_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

---

### MongoDB (Inventory Service)
- Start mongod (Windows service or exe):

```powershell
# If installed as service:
net start MongoDB
# Or run directly
"C:\Program Files\MongoDB\Server\<version>\bin\mongod.exe"
```

MongoDB default URI: `mongodb://localhost:27017`

---

## Running the Microservices (Local)
Start in this order (recommended):

1. Inventory Service

```bash
mvn spring-boot:run
# To run another instance on a different port:
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8083
```

2. Borrowing Service

```bash
mvn spring-boot:run
```

3. API Gateway

```bash
mvn spring-boot:run
```

---

## Testing the System (Local)
- All requests go through the API Gateway (`8080`)

Borrow Book:
```
POST http://localhost:8080/borrow/issue
```

Get Inventory:
```
GET http://localhost:8080/inventory/books/{bookId}
```

### Rate Limiter behavior
- Excessive requests → `HTTP 429 Too Many Requests`

### Actuator
Health check:
```
GET http://localhost:8080/actuator/health
```

---

## Troubleshooting & Notes 
- Ensure Kafka is reachable at `localhost:9092` and topics exist or auto-create is enabled.
- On Windows, prefer WSL2 for smooth compatibility with Linux-style scripts.
- Keep DB credentials secure and configurable via environment variables.
- Consider adding Docker Compose for reproducible local environment.

---

