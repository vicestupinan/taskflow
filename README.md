# Task Management API

This is a Spring Boot REST API for managing tasks. It supports basic CRUD operations and includes features like logging, transactional service management, and pagination for task listings.

---

## 🚀 Features
- Create, read, update, and delete tasks.
- Authentication and authorization using JWT.
- Transactional service support.
- Pagination for task listing.
- Standard RESTful API structure.

---

## 🛠️ Tech Stack
- Java 17+
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven

---

## 🔐 Authentication
The API uses JWT for secure authentication. To access protected endpoints, you must:

- Authenticate using the login endpoint to receive a JWT token.
- Include the token in the Authorization header for subsequent requests.

Example:
Authorization: Bearer your-jwt-token

---

## 📄 API Endpoints (/api) (/api/admin)

| Method | Endpoint       | Description                         |
|--------|----------------|-------------------------------------|
| GET    | `/tasks`       | List all tasks (supports pagination)|
| POST   | `/tasks`       | Create a new task                   |
| GET    | `/tasks/{id}`  | Get task by ID                      |
| PUT    | `/tasks/{id}`  | Update task by ID                   |
| DELETE | `/tasks/{id}`  | Delete task by ID                   |

### Pagination Example
GET /tasks?page=1&size=5&sortBy=taskStatus&direction=desc

--- 

## ⚙️ Configuration
Update your `application.yml` to configure your database and other settings.

# Build the project
mvn clean install

# Run the project
mvn spring-boot:run
