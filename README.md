# Lens HRMS - Spring Boot Employee Management API

Lens HRMS is a production-style Spring Boot backend for employee management. It demonstrates REST APIs, JWT authentication, role-based authorization, advanced employee search, entity relationships, validation, exception handling, Swagger/OpenAPI documentation, logging, testing, and database integration.

## Tech Stack

- Java 17
- Spring Boot 3.5
- Maven
- MySQL
- Spring Data JPA / Hibernate
- Spring Security
- JWT
- JUnit 5
- Mockito
- MockMvc
- Swagger / OpenAPI
- SLF4J Logging
- Spring Boot Actuator
- Docker / Docker Compose

## Architecture
Client
  -> JWT Security
  -> Controller
  -> DTO + Validation
  -> Service
  -> Repository
  -> JPA/Hibernate
  -> MySQL

## Project Structure
src/main/java/com/lens/hrms
│
├── controller
├── dto
├── entity
├── exception
├── repository
├── security
└── service

**Core Features**
**Employee Management**
Create employee
Get employee by ID
Get all employees
Update employee
Delete employee

**Advanced Search
The employee API supports:**
General search
Department filtering
Designation filtering
Status filtering
Minimum salary filtering
Maximum salary filtering
Dynamic filtering using JPA Specifications
Sorting
Pagination

Example:

GET /api/employees?search=alice&page=0&size=10&sortBy=name&direction=asc

Example with filters:

GET /api/employees?department=IT&status=ACTIVE&minSalary=30000&maxSalary=80000

**Entity Relationships**
The project implements a Many-to-One relationship between Employee and Department.

Department
    |
    | 1
    |
    |------< Many Employees

Employees reference a Department through department_id.
The relationship uses lazy fetching:

@ManyToOne(fetch = FetchType.LAZY)

Cascade delete is not used because a department can be shared by multiple employees.

**Validation**
Employee requests use Jakarta Bean Validation:

Name is required
Email is required and must be valid
Department is required
Designation is required
Salary cannot be negative
Status is required

**Global Exception Handling**
The application uses @RestControllerAdvice for centralized exception handling.
Custom exceptions include:

EmployeeNotFoundException
DuplicateEmailException
DepartmentNotFoundException

The application also handles:

Validation errors
Authorization errors
Unexpected server errors

**API Response Standardization**
Successful responses follow a consistent structure:

{
  "success": true,
  "message": "Employee fetched successfully",
  "data": {}
}

Error responses follow a standardized structure:

{
  "status": 404,
  "message": "Employee not found with id: 10",
  "timestamp": "2026-08-21T15:00:00",
  "errors": {}
}

The application uses appropriate HTTP status codes such as:
200 OK
201 CREATED
204 NO CONTENT
400 BAD REQUEST
403 FORBIDDEN
404 NOT FOUND
409 CONFLICT
500 INTERNAL SERVER ERROR

# Security
The application uses Spring Security and JWT authentication.

**Roles**
ADMIN
HR
EMPLOYEE

**Permissions**
ADMIN    -> Create, Read, Update, Delete
HR       -> Create, Read, Update
EMPLOYEE -> Read

**Login**
POST /api/auth/login

Example request:
{
  "email": "admin@lens.com",
  "password": "Admin@123"
}
Use the returned JWT token:
   Authorization: Bearer <token>

For local development only, demo users can be enabled with:
   SEED_DEMO_USERS=true
Demo credentials should never be used in production.
 
# API Endpoints
**Authentication**
POST /api/auth/login
**Employees**
GET    /api/employees
GET    /api/employees/{id}
POST   /api/employees
PUT    /api/employees/{id}
DELETE /api/employees/{id}

Swagger / OpenAPI

Swagger UI:

http://localhost:8080/swagger-ui.html

OpenAPI specification:

http://localhost:8080/v3/api-docs

The API documentation includes:

Endpoints
Request parameters
Request models
Response models
HTTP status codes
Error responses
Security requirements
Logging

The application uses SLF4J logging for important operations such as:

Employee creation
Employee updates
Employee deletion
Employee search operations

Sensitive information such as passwords and JWT tokens is not logged.

Actuator

Health endpoint:

http://localhost:8080/actuator/health

Configured actuator endpoints include:

Health
Info
Metrics
Database

Create the MySQL database:

CREATE DATABASE lens_hrms;

Database credentials are configured using environment variables.

Git Bash
export DB_USERNAME=root
export DB_PASSWORD=YOUR_PASSWORD
export APP_JWT_SECRET="a-random-secret-at-least-32-characters-long"
PowerShell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="YOUR_PASSWORD"
$env:APP_JWT_SECRET="a-random-secret-at-least-32-characters-long"

Do not commit real passwords or JWT secrets.

Run Locally

Run tests:

mvn clean test

Start the application:

mvn spring-boot:run

The application runs on:

http://localhost:8080
Testing

The project contains:

JUnit 5 tests
Mockito service tests
MockMvc controller tests

Current test result:

Tests run: 12
Failures: 0
Errors: 0
Skipped: 0


BUILD SUCCESS
Docker

Run the application using Docker Compose:

docker compose up --build

The application will be available at:

http://localhost:8080
Git Workflow

Development is performed using feature branches.

Example:

main
 |
 └── feature/hrms-enhancements
          |
          └── Pull Request
                 |
                 v
                main

Feature work should be reviewed and merged through a Pull Request instead of committing directly to the main branch.

Git Ignore

The project ignores:

target/
.idea/
*.iml
.classpath
.project
.settings/
.env
.env.*

Sensitive configuration files and generated build files should not be committed.