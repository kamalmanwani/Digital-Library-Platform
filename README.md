# Digital Library Platform

A Java Spring Boot microservices-based Digital Library Platform designed to manage book inventory and lending workflows using real-time service communication and event-driven processing.

---

## Architecture Overview

The system follows a microservices architecture with a centralized API Gateway.

Client requests are routed through the API Gateway to backend services.  
Borrowing Service communicates with Inventory Service using Spring WebClient and publishes domain events to Kafka.

API Gateway provides routing, load balancing, and rate limiting.

---

## Microservices

### API Gateway
Responsibilities:
- Single entry point for all client requests
- Path-based routing to backend services
- Client-side load balancing
- Redis-backed rate limiting
- Centralized health monitoring

Port: 8080

---

### Borrowing Service
Responsibilities:
- Handle book borrowing, returns, and renewals
- Enforce business rules:
  - Maximum 2 active books per user
  - 7-day borrowing period
  - One renewal per borrow
  - 30-day cooldown after return
- Validate book availability via Inventory Service using WebClient
- Publish Kafka events after successful database transactions

Database: PostgreSQL  
Port: 8082

---

### Inventory Service
Responsibilities:
- Manage book inventory and availability
- Maintain total and available copies
- Consume Kafka events to update inventory state
- Cache frequently accessed inventory data using Redis

Database: MongoDB  
Port: 8081 (multiple instances supported)

---

## Technology Stack

- Java
- Spring Boot
- Spring Data JPA
- Spring WebClient
- Spring Cloud Gateway
- Apache Kafka
- PostgreSQL
- MongoDB
- Redis
- Spring Boot Actuator

---

## Prerequisites (Windows)

Ensure the following software is installed and running on Windows:

- Java 8 or higher
- Maven
- Apache Kafka (Windows binaries)
- Redis (Windows build)
- PostgreSQL
- MongoDB

---

## Running Infrastructure on Windows

### Apache Kafka

Start Zookeeper:
```bat
bin\windows\zookeeper-server-start.bat config\zookeeper.properties
