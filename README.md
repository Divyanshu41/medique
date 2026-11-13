# Medique – Hospital Appointment Management System

A server-rendered hospital appointment application built with Java 21, Spring Boot 3.5, Spring Security, Spring Data JPA, MySQL, Thymeleaf and Bootstrap.

## Features
- Patient registration and login with BCrypt password hashing
- Role-protected admin and patient routes
- Admin doctor and user management
- Patient doctor directory and appointment booking
- Appointment status management
- Environment-based database configuration
- GitHub Actions Maven verification workflow

> Learning project only. It has not been independently security-audited or load-tested. Do not store real patient/medical data in this demo.

## Requirements
- JDK 21
- MySQL 8+
- Git

## Local setup
1. Clone:
   ```bash
   git clone https://github.com/Divyanshu41/medique.git
   cd medique
   ```
2. Create database:
   ```sql
   CREATE DATABASE new-hsrole_db;
   ```
3. Copy `.env.example` to `.env` as a reference and configure variables in your shell or IDE. Spring Boot does not automatically load a plain `.env` file.

   PowerShell:
   ```powershell
   $env:DB_URL="jdbc:mysql://localhost:3306/new-hsrole_db"
   $env:DB_USERNAME="root"
   $env:DB_PASSWORD="your_local_password"
   ```

   Linux/macOS:
   ```bash
   export DB_URL='jdbc:mysql://localhost:3306/new-hsrole_db'
   export DB_USERNAME='root'
   export DB_PASSWORD='your_local_password'
   ```
4. Build and run:
   ```bash
   ./mvnw clean verify
   ./mvnw spring-boot:run
   ```
   On Windows use `mvnw.cmd`.
5. Open http://localhost:8080.

## Configuration
- `DB_URL`: JDBC URL
- `DB_USERNAME`: database username
- `DB_PASSWORD`: database password
- `DDL_AUTO`: Hibernate schema mode (local default: update; production: validate)
- `SERVER_PORT`: HTTP port (default 8080)
- `SESSION_COOKIE_SECURE`: set true behind HTTPS

## Security
- Never commit `.env`, credentials, API keys or patient data.
- Rotate every credential previously committed to a public repository. Removing it from the latest commit does not erase Git history.
- Use HTTPS and secure cookies in production.
- Use database migrations and set `DDL_AUTO=validate` in production.
- Do not use real patient records in this demo.

## CI
GitHub Actions runs Maven `clean verify` on pushes and pull requests to `main`. Automated production deployment is not configured.
