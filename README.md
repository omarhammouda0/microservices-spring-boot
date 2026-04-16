Microservices E-Commerce Platform
A production-minded Spring Boot microservices system demonstrating distributed-systems patterns end-to-end: service discovery, API gateway with JWT, role-based access control, event-driven communication, circuit breakers, distributed tracing, Redis caching, and a fully containerized stack.

Status: Portfolio / learning project. Designed as a reference for junior-to-mid Spring Boot microservices work.

🏗️ Architecture
                              ┌──────────────────┐
                              │   API Gateway    │   :8080  (only public entry)
                              │  JWT + CORS +    │
                              │  Header Forward  │
                              └────────┬─────────┘
                                       │
            ┌──────────────────────────┼──────────────────────────┐
            ▼                          ▼                          ▼
     ┌─────────────┐            ┌─────────────┐            ┌─────────────┐
     │   User      │            │   Product   │            │   Order     │
     │   Service   │  ◀──Feign──│   Service   │            │   Service   │
     │   :8081     │            │   :8082     │            │   :8083     │
     └──────┬──────┘            └──────┬──────┘            └──────┬──────┘
            │                          │                          │
            ▼                          ▼                          ▼
     ┌─────────────┐            ┌─────────────┐            ┌─────────────┐
     │ Postgres    │            │ Postgres    │            │ Postgres    │
     │ userdb      │            │ productdb   │            │ orderdb     │
     └─────────────┘            └──────┬──────┘            └─────────────┘
                                       │
                                       ▼
                                 ┌──────────┐
                                 │  Redis   │  (product + user cache)
                                 └──────────┘

     ┌──────────────┐                                       ┌──────────────┐
     │    Eureka    │  :8761    Service discovery           │   RabbitMQ   │  :5672
     │    Server    │                                       │   Events     │
     └──────────────┘                                       └──────┬───────┘
                                                                   │
                              order.created / order.cancelled / user.updated
                                       │
                                       ▼
                             (Product Service consumes events
                              for inventory reservation / restore)

     ┌──────────────┐       ┌──────────────┐       ┌──────────────┐
     │    Zipkin    │       │  Prometheus  │       │   Actuator   │
     │  Tracing     │       │   Metrics    │       │   Health     │
     └──────────────┘       └──────────────┘       └──────────────┘
Gateway-only access model: downstream services are declared with expose: (not ports:) in docker-compose.yml. The only way into the system from a browser or external client is through the API gateway, which validates JWTs and forwards X-User-Id / X-User-Role headers downstream.

✨ Features
Security & Auth
JWT authentication issued by user-service on /auth/login and /auth/register.
Role-based access control (RBAC) — ADMIN vs USER. Claims are extracted at the gateway and forwarded as X-User-Id / X-User-Role headers; downstream services enforce them in a shared HelperService.
Stripped header enforcement — the gateway removes any client-supplied X-User-Id / X-User-Role before setting the JWT-derived ones (no role spoofing).
Gateway-only access — service ports are not published on the host.
BCrypt password hashing.
Microservices Patterns
Service discovery (Netflix Eureka).
Single API gateway (Spring Cloud Gateway) with a global JWT filter.
Inter-service communication via OpenFeign + Eureka load-balancing, with configured connect (2s) and read (5s) timeouts.
Internal endpoint convention — user-service exposes /internal/users/{id} for Feign callers only (hidden from Swagger, no RBAC, unreachable from outside Docker network). Gateway blocks /internal/** paths.
Circuit breaker + retry (Resilience4j) on Feign calls — product-service → user-service retries up to 3× with exponential backoff, circuit opens at 50% failure rate over a 10-call sliding window, fallback returns cached user data from Redis.
Idempotent event processing — product-service stores a ProcessedEvent(UUID, timestamp) before mutating inventory. Duplicate deliveries from RabbitMQ are silently skipped.
Soft delete for users and products (isActive flag).
Order state machine with explicitly validated transitions:
PENDING → CONFIRMED (automated via order.confirmed event from product-service)
CONFIRMED → DELIVERED (admin)
CONFIRMED → FAILED (admin)
DELIVERED → RETURNED (admin)
PENDING / CONFIRMED → CANCELLED (order owner)
Any other transition throws InvalidStatusTransition (400).
Optimistic locking (@Version) on inventory — concurrent stock updates are retried via @Retryable(maxAttempts=3, backoff=100ms×2). If retries exhaust, a ConflictException maps to HTTP 409.
Event-Driven Flows (RabbitMQ)
The order lifecycle uses event choreography — no central orchestrator, each service reacts to events and publishes the next:

order-service                    product-service                 order-service
     │                                │                               │
     │── order.created ──────────────▶│ validate stock                │
     │                                │ decrement inventory           │
     │                                │── order.confirmed ───────────▶│ set status = CONFIRMED
     │                                │                               │
     │── order.cancelled ────────────▶│ restore inventory             │
     │                                │                               │
Event	Publisher	Consumer	Action
order.created	order-service	product-service	Validates stock, decrements inventory, publishes order.confirmed
order.confirmed	product-service	order-service	Transitions order from PENDING → CONFIRMED
order.cancelled	order-service	product-service	Restores reserved inventory quantities
user.updated	user-service	product-service	Evicts the cached user entry from Redis (forces fresh Feign fetch on next product read)
All queues route failed messages to dead-letter exchanges (order.dlx, user.dlx) with routing key order.failed / user.failed.
Consumer retries: 5 attempts with exponential backoff (500ms × 2.0, max 10s) before dead-lettering.
Observability
Distributed tracing with Micrometer + Brave, exported to Zipkin.
Metrics exposed to Prometheus via /actuator/prometheus.
Structured logs including traceId / spanId.
Actuator health + circuit-breaker endpoints per service.
API Documentation
Swagger UI per service, aggregated at the gateway: http://localhost:8080/swagger-ui.html.
Paginated endpoints render page / size / sort as discrete Swagger inputs (not a raw JSON blob).
Default sort: createdAt DESC on list endpoints — newest first.
Global BearerAuth security scheme; public endpoints (/auth/**) explicitly opt out.
🧱 Tech Stack
Layer	Tool
Language	Java 17
Framework	Spring Boot 3.3.5, Spring Cloud 2023.0.3
Service Discovery	Netflix Eureka
API Gateway	Spring Cloud Gateway (WebFlux)
Security	Spring Security + JJWT (HS256)
Data	Spring Data JPA (Hibernate), PostgreSQL 15
Cache	Spring Data Redis
Messaging	Spring AMQP + RabbitMQ
Resilience	Resilience4j (CircuitBreaker, Retry), Spring Retry
Tracing	Micrometer Tracing + Brave + Zipkin
Metrics	Micrometer + Prometheus
API Docs	springdoc-openapi 2.5.0
Build	Maven (multi-module)
Runtime	Docker, Docker Compose
🚀 Quick Start
Prerequisites
Docker & Docker Compose
(Optional, for local dev) Java 17, Maven 3.9+
Environment
Create a .env file in the project root:

DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=<at-least-32-chars-base64-safe-random-string>
JWT_EXPIRATION=3600000
RABBITMQ_DEFAULT_USER=guest
RABBITMQ_DEFAULT_PASS=guest
Run the whole stack
docker compose up -d --build
Give Eureka a minute, then everything registers automatically. Useful endpoints:

Service	URL
API Gateway	http://localhost:8080
Aggregated Swagger UI	http://localhost:8080/swagger-ui.html
Eureka Dashboard	http://localhost:8761
RabbitMQ Management	http://localhost:15672
Zipkin	http://localhost:9411
Prometheus	http://localhost:9090
Note: user-service, product-service, and order-service deliberately do not publish host ports. All traffic must go through the gateway on :8080.

Stop the stack
docker compose down          # keeps volumes
docker compose down -v       # wipe databases too
🔐 Authentication
Every non-public endpoint expects Authorization: Bearer <jwt>. The gateway validates the token, extracts userId and userRole, and forwards them as headers to the downstream service.

Register + Login
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"name":"Jane","email":"jane@example.com","password":"P@ssw0rd!"}'

# Login → returns { "token": "ey..." }
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"jane@example.com","password":"P@ssw0rd!"}' | jq -r .token)

# Call a protected endpoint
curl http://localhost:8080/api/users/me -H "Authorization: Bearer $TOKEN"
Roles
Role	Can
USER	Read & update own profile, place / cancel own orders, read own orders
ADMIN	All of the above, plus list all users / all orders, create users, update order status
📡 API Endpoints (via Gateway)
All paths below are prefixed with http://localhost:8080/api.

Auth (public)
Method	Path	Description
POST	/auth/register	Register a new user
POST	/auth/login	Exchange credentials for a JWT
User Service
Method	Path	Auth
GET	/users	ADMIN (paginated, sort=createdAt,desc default)
GET	/users/active	ADMIN
GET	/users/{id}	Self or ADMIN
PATCH	/users/{id}	Self or ADMIN
DELETE	/users/{id}	Self or ADMIN (soft delete, publishes user.updated)
POST	/users	ADMIN
Product Service
Method	Path	Auth
GET	/products	Authenticated (paginated, newest first)
GET	/products/{id}	Authenticated (enriched with user data via Feign, Redis-cached with fallback)
GET	/products/user/{userId}	Authenticated (paginated)
POST	/products	Authenticated
PATCH	/products/{id}	Owner or ADMIN
DELETE	/products/{id}	Owner or ADMIN (soft delete)
GET	/inventory/{productId}	Authenticated
PATCH	/inventory/{productId}	ADMIN
Order Service
Method	Path	Auth
POST	/orders	USER or ADMIN — places an order, publishes order.created
GET	/orders	ADMIN (paginated, newest first)
GET	/orders/my	Authenticated — caller's own orders
GET	/orders/{id}	Owner or ADMIN
PATCH	/orders/{id}/cancel	Owner or ADMIN — publishes order.cancelled
PATCH	/orders/{id}/status	ADMIN — drives the state machine
Pagination query params
All GET list endpoints accept:

?page=0&size=10&sort=createdAt,desc
Invalid sort fields return a 400 with a ProblemDetail body (code: INVALID_SORT_PROPERTY) instead of a 500.

🧪 Try it out
# Place an order (requires a valid JWT for a USER)
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"items":[{"productId":1,"quantity":2}]}'

# Cancel an order → inventory is restored via the order.cancelled event
curl -X PATCH http://localhost:8080/api/orders/1/cancel \
  -H "Authorization: Bearer $TOKEN"

# Watch the order.created / order.cancelled queues in RabbitMQ
open http://localhost:15672

# Trace the request across services
open http://localhost:9411
Circuit breaker demo
# Stop user-service; product-service should still return enriched products
# using the cached user data via the Resilience4j fallback.
docker compose stop user-service
curl http://localhost:8080/api/products/1 -H "Authorization: Bearer $TOKEN"
docker compose start user-service
📊 Observability
Signal	Endpoint
Liveness / readiness	GET /actuator/health
Prometheus scrape	GET /actuator/prometheus
Circuit-breaker state	GET /actuator/circuitbreakers, circuitbreakerevents
Retry state	GET /actuator/retries, retryevents
Traces	Zipkin UI → http://localhost:9411
Logs include traceId / spanId, so a single request can be followed across gateway → order-service → product-service (via Feign) → RabbitMQ consumer.

🗂️ Project Structure
microservices-spring-boot/
├── api-gateway/           # Spring Cloud Gateway + JWT filter
├── eureka-server/         # Service discovery
├── user-service/          # Auth, users, roles
├── product-service/       # Products + inventory + Redis cache + event consumer
├── order-service/         # Orders + state machine + event publisher
├── docker-compose.yml     # Full stack orchestration
└── README.md
Each service follows a conventional layout:

src/main/java/com/<service>/
├── controller/            # REST endpoints + OpenAPI annotations
├── service/               # Business logic (heavily Javadoc'd)
├── repository/            # Spring Data JPA
├── entity/                # JPA entities
├── dto/                   # Request / response records with @Schema
├── mapper/                # MapStruct-style manual mappers
├── event/                 # RabbitMQ publishers / consumers
├── config/                # Rabbit, Swagger, Feign, Redis, Retry
├── exception/             # Typed exceptions + GlobalExceptionHandler
└── security/              # SecurityConfig, JwtService, JwtAuthenticationFilter
⚙️ Configuration Notes
spring.jpa.hibernate.ddl-auto = validate in every service — schemas are authoritative, the app will refuse to start on drift.
spring.jpa.open-in-view = false.
springdoc.default-support-pageable: true everywhere — Swagger renders Pageable as discrete inputs.
CORS is handled only at the gateway (allowedOriginPatterns with allowCredentials: true). Services disable CORS at the filter level because they're not browser-reachable.
The gateway de-duplicates Access-Control-* headers via DedupeResponseHeader default filter.
🚧 Roadmap
 Replace manual mappers with MapStruct
 Integration tests with Testcontainers
 Saga pattern for long-running order flows
 Grafana dashboards for Prometheus metrics
 Centralized logging (Loki / ELK)
 Refresh tokens + token revocation list
 k8s manifests / Helm chart
📝 License
For educational and portfolio purposes.

👤 Author
Omar Hammouda — building production-grade Spring Boot microservices, actively seeking junior software developer positions in Germany.