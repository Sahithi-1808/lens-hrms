# Lens HRMS - Production-style Spring Boot Employee API

This Spring Boot backend expands the original Employee CRUD into a learning/portfolio project with:

- Java 17 + Spring Boot 3.5
- Maven
- REST CRUD
- MySQL + JPA/Hibernate
- DTOs and Bean Validation
- Global exception handling
- Standard API response structure
- Search, pagination and sorting
- JUnit + Mockito service/controller tests
- Spring Security + JWT
- Roles: ADMIN / HR / EMPLOYEE
- Swagger/OpenAPI
- SLF4J logging
- Spring Boot Actuator
- Docker + Docker Compose

## Architecture

```text
Client
  -> JWT Security
  -> Controller
  -> DTO + Validation
  -> Service
  -> Repository
  -> JPA/Hibernate
  -> MySQL
```

## Run locally

1. Create MySQL database:

```sql
CREATE DATABASE lens_hrms;
```

2. Set environment variables (do not commit real secrets):

Git Bash:
```bash
export DB_USERNAME=root
export DB_PASSWORD=YOUR_PASSWORD
export APP_JWT_SECRET="a-random-secret-at-least-32-characters-long"
```

PowerShell:
```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="YOUR_PASSWORD"
$env:APP_JWT_SECRET="a-random-secret-at-least-32-characters-long"
```

3. Run:

```bash
mvn clean test
mvn spring-boot:run
```

## API

```text
POST   /api/auth/login
GET    /api/employees
GET    /api/employees/{id}
POST   /api/employees
PUT    /api/employees/{id}
DELETE /api/employees/{id}
```

Search/pagination example:

```text
GET /api/employees?search=alice&page=0&size=10&sortBy=name&direction=asc
```

## JWT demo users

For local learning only, enable:

```text
SEED_DEMO_USERS=true
```

Then the application creates these users if they do not already exist:

```text
admin@lens.com / Admin@123       ADMIN
hr@lens.com / Hr@12345          HR
employee@lens.com / Employee@123 EMPLOYEE
```

Change/remove these credentials before any real deployment.

Login:

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@lens.com",
  "password": "Admin@123"
}
```

Use the returned token:

```text
Authorization: Bearer <token>
```

Permissions:

```text
ADMIN    -> create, read, update, delete
HR       -> create, read, update
EMPLOYEE -> read
```

## Swagger

After startup:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## Actuator

Health:

```text
http://localhost:8080/actuator/health
```

## Docker

```bash
docker compose up --build
```

The application will be available at:

```text
http://localhost:8080
```

## Tests

```bash
mvn test
```

The project contains unit tests using JUnit/Mockito and a controller test using MockMvc.

## Important

`application.properties` uses environment variables for database credentials and JWT secrets. Do not put production passwords or JWT secrets directly into source control.
