---
trigger: always_on
---

# 1. Tech Stack & Dependencies
- **Java:** JDK 25.
- **Framework:** Spring Boot 4.0.6.
- **Database:** PostgreSQL.
- **Object Storage**: MinIO (use this for free, but alway config to prepare switching to cloud storage service like S3/R2)

# 2. Build & Execution Rules
- **Maven Wrapper:** DO NOT use the local machine's default `mvn` installation. Always use the Maven wrapper:
  - Linux/macOS: `./mvnw`
  - Windows: `./mvnw.cmd`
- **Development Environment:** 
  - The backend server must run directly on the host machine using: `./mvnw spring-boot:run`
  - All other infrastructure components (PostgreSQL DB, Object Storage, Redis, etc.) are containerized and run via Docker.


# 3. Architecture & Project Structure
The project strictly follows a Layered Architecture. 

## 3.1. Package Structure
Maintain the following standard package organization:
- `config`: Configuration classes (e.g., Security, Swagger, Beans).
- `controller`: REST APIs (`@RestController`).
- `service`: Business logic interfaces and implementations (`@Service`)
- `repository`: Database access using Spring Data JPA (`@Repository`), include interfaces extends `JpaRepository`.
- `entity`: JPA Domain models mapping to database tables.
- `dto`: Data Transfer Objects. MUST be sub-divided into:
  - `common`: Shared DTOs.
  - `request`: Input payloads.
  - `response`: Output payloads.
- `exception`: Custom exceptions and the Global Exception Handler.

## 3.2. Strict Layer Boundaries (CRITICAL)
- **Flow:** `Controller` -> `Service` -> `Repository`.
- **Controllers:** Must only handle HTTP request routing, input validation, and response formatting. **Controllers MUST NOT contain business logic and MUST NOT interact with Repositories directly.**
- **Services:** Must contain 100% of the business logic. 
- **Data Encapsulation:** JPA Entities (`entity` package) MUST NEVER be returned directly by the Controller to the client. Always map Entities to `response` DTOs before returning them. Map `request` DTOs to Entities before saving to the database.