\# Microservices E-Commerce System



A production-grade microservices architecture built with Spring Boot, demonstrating modern distributed systems patterns including service discovery, API gateway, circuit breakers, and containerization.



\## 🏗️ Architecture

```

┌─────────────────┐

│   API Gateway   │  Port 8080 - Main entry point

└────────┬────────┘

&#x20;        │

&#x20;   ┌────┴────┐

&#x20;   ↓         ↓

┌─────────┐ ┌──────────────┐

│  User   │ │   Product    │

│ Service │ │   Service    │

│  :8081  │ │    :8082     │

└────┬────┘ └──────┬───────┘

&#x20;    │             │

&#x20;    ↓             ↓

┌─────────┐   ┌──────────┐

│Postgres │   │Postgres  │

│ userdb  │   │productdb │

└─────────┘   └──────────┘

&#x20;        │

&#x20;        ↓

&#x20; ┌─────────────┐

&#x20; │   Eureka    │  Port 8761 - Service Discovery

&#x20; │   Server    │

&#x20; └─────────────┘

```



\## ✨ Features



\### Microservices Patterns

\- \*\*Service Discovery\*\*: Eureka Server for dynamic service registration

\- \*\*API Gateway\*\*: Centralized routing and load balancing

\- \*\*Circuit Breaker\*\*: Resilience4j for fault tolerance

\- \*\*Retry Pattern\*\*: Automatic retry with exponential backoff

\- \*\*Cache Fallback\*\*: Graceful degradation when services fail



\### Technology Stack

\- \*\*Framework\*\*: Spring Boot 4.0, Spring Cloud 2025.0

\- \*\*Service Discovery\*\*: Netflix Eureka

\- \*\*API Gateway\*\*: Spring Cloud Gateway

\- \*\*Resilience\*\*: Resilience4j (Circuit Breaker, Retry)

\- \*\*Database\*\*: PostgreSQL 15

\- \*\*Containerization\*\*: Docker, Docker Compose

\- \*\*Build Tool\*\*: Maven



\## 🚀 Quick Start



\### Prerequisites

\- Docker \& Docker Compose

\- Java 17+ (for local development)

\- Maven 3.9+ (for local development)



\### Run with Docker (Recommended)

```bash

\# Clone the repository

git clone https://github.com/YOUR-USERNAME/microservices-project.git

cd microservices-project



\# Start all services

docker-compose up



\# Access the application

\# API Gateway: http://localhost:8080

\# Eureka Dashboard: http://localhost:8761

```



That's it! The entire stack runs with one command.



\### Run Locally (Development)

```bash

\# Start Eureka Server

cd eureka-server

mvn spring-boot:run



\# Start User Service

cd user-service

mvn spring-boot:run



\# Start Product Service

cd product-service

mvn spring-boot:run



\# Start API Gateway

cd api-gateway

mvn spring-boot:run

```



\## 📡 API Endpoints



\### User Service

```

GET    /users          - Get all users

GET    /users/{id}     - Get user by ID

POST   /users          - Create new user

PUT    /users/{id}     - Update user

DELETE /users/{id}     - Delete user

```



\### Product Service

```

GET    /products       - Get all products

GET    /products/{id}  - Get product by ID (with user details)

POST   /products       - Create new product

PUT    /products/{id}  - Update product

DELETE /products/{id}  - Delete product

```



All endpoints accessible via API Gateway at `http://localhost:8080`



\## 🛠️ Technical Highlights



\### Circuit Breaker Configuration

```yaml

resilience4j:

&#x20; circuitbreaker:

&#x20;   instances:

&#x20;     userservice:

&#x20;       sliding-window-size: 10

&#x20;       failure-rate-threshold: 50%

&#x20;       wait-duration-in-open-state: 10s

```



When User Service fails:

1\. Circuit opens after 50% failure rate

2\. Returns cached user data (fallback)

3\. Auto-recovery after 10 seconds



\### Docker Multi-Stage Builds

```dockerfile

\# Stage 1: Build

FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests



\# Stage 2: Run

FROM eclipse-temurin:17-jre

COPY --from=build /app/target/\*.jar app.jar

ENTRYPOINT \["java", "-jar", "app.jar"]

```



\*\*Result\*\*: 36% smaller images (360MB → 230MB)



\## 🧪 Testing



\### Insert Test Data

```bash

\# Connect to User database

docker exec -it postgres-userdb psql -U postgres -d userdb



\# Insert users

INSERT INTO users (id, name, email, active, created\_at) 

VALUES (1, 'John Doe', 'john@example.com', true, NOW());



\# Connect to Product database

docker exec -it postgres-productdb psql -U postgres -d productdb



\# Insert products

INSERT INTO products (id, name, price, created\_by, created\_date) 

VALUES (1, 'Laptop', 1200.00, 1, NOW());

```



\### Test Endpoints

```bash

\# Get all products with user details

curl http://localhost:8080/products



\# Get specific product

curl http://localhost:8080/products/1



\# Test circuit breaker (stop user-service)

docker-compose stop user-service

curl http://localhost:8080/products/1

\# Returns product with cached fallback user data

```



\## 📊 Monitoring



\### Eureka Dashboard

```

http://localhost:8761

```

View all registered service instances and their health status.



\### Actuator Endpoints

```

http://localhost:8082/actuator/health

http://localhost:8082/actuator/circuitbreakers

http://localhost:8082/actuator/circuitbreakerevents

```



\## 🎓 Learning Outcomes



This project demonstrates:

\- ✅ Microservices architecture design

\- ✅ Service discovery and registration

\- ✅ API Gateway pattern

\- ✅ Resilience patterns (Circuit Breaker, Retry)

\- ✅ Docker containerization

\- ✅ Docker Compose orchestration

\- ✅ Database isolation per service

\- ✅ Inter-service communication (Feign)

\- ✅ Graceful degradation with fallbacks



\## 🚧 Roadmap



\- \[ ] Redis caching layer

\- \[ ] Distributed tracing (Zipkin)

\- \[ ] Centralized logging (ELK Stack)

\- \[ ] Metrics \& monitoring (Prometheus + Grafana)

\- \[ ] JWT authentication

\- \[ ] Event-driven communication (RabbitMQ)



\## 📝 License



This project is for educational and portfolio purposes.



\## 👤 Author



\*\*Omar Hammouda\*\*

\- Building production-grade microservices

\- Actively seeking junior software developer positions in Germany

