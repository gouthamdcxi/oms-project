# 🔐 Auth Service

## 📌 Overview

Auth Service is a Spring Boot-based microservice responsible for handling user authentication and authorization. It provides secure APIs for user registration, login, and token-based access control using JWT (JSON Web Token).

This service acts as a centralized authentication system and integrates with other microservices to ensure secure communication.

---

## 🚀 Features

* User Registration (Signup)
* User Login (Authentication)
* JWT Token Generation
* Token Validation
* Role-Based Authorization
* Secure Password Encryption
* Stateless Authentication

---

## 🛠️ Tech Stack

* Java 8+
* Spring Boot
* Spring Security
* JWT (JSON Web Token)
* MySQL
* Maven

---

## 🗄️ Database Design

### 🔹 Table: `auth_user`

| Column Name | Type        | Description           |
| ----------- | ----------- | --------------------- |
| user_id     | BIGINT (PK) | Unique user ID        |
| name        | VARCHAR     | User name             |
| email       | VARCHAR     | User email (unique)   |
| password    | VARCHAR     | Encrypted password    |
| role        | VARCHAR     | USER / ADMIN          |
| created_at  | TIMESTAMP   | Account creation time |

---

## 🔑 API Endpoints

### ➕ Register User

```
POST /auth/register
```

**Request:**

```json
{
  "name": "John",
  "email": "john@example.com",
  "password": "password123"
}
```

---

### 🔐 Login User

```
POST /auth/login
```

**Request:**

```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**

```json
{
  "token": "jwt_token_here"
}
```

---

### ✅ Validate Token

```
GET /auth/validate
```

**Header:**

```
Authorization: Bearer <token>
```

---

## 🔒 Security

* Passwords are encrypted using BCrypt
* JWT tokens are used for authentication
* Stateless session management
* Secure API access using token validation filters

---

## 🔄 Flow

1. User registers via `/auth/register`
2. User logs in via `/auth/login`
3. JWT token is generated
4. Token is passed in API headers
5. Backend validates token for each request

---

## 📦 Integration

* Used by Order Service, Payment Service, etc.
* Provides centralized authentication
* Ensures secure inter-service communication

---

## ⚙️ Setup Instructions

1. Clone the repository
2. Configure `application.properties`:

   * Database URL
   * Username & Password
   * JWT Secret Key
3. Run the application:

```
mvn spring-boot:run
```

---

## 💡 Future Enhancements

* Refresh Token Mechanism
* OAuth2 Integration
* Multi-Factor Authentication (MFA)
* Social Login (Google, Facebook)

---

## 👨‍💻 Author

Developed as part of a microservices-based system for scalable and secure application architecture.

---
