# Medique – Hospital Appointment Management System

A server-rendered hospital appointment application built with Java 21, Spring Boot 3.5, Spring Security, Spring Data JPA, H2 for local development, optional MySQL, Thymeleaf and Bootstrap.

## Features
- Patient registration and login with BCrypt password hashing
- Role-protected admin and patient routes
- Admin doctor and user management
- Patient doctor directory and appointment booking
- Doctor-managed weekly working hours with optional breaks
- Date-wise doctor leave management
- Dynamic 60-minute booking slots based on the doctor's schedule, breaks, leave dates and existing bookings
- Appointment status management

> Learning project only. It has not been independently security-audited or load-tested. Do not store real patient/medical data in this demo.

## Requirements
- JDK 21 or newer
- Git

MySQL is not required for the default local setup. The application uses a persistent H2 file database by default.

## Local setup (recommended)

1. Clone the repository:
   ```powershell
   git clone https://github.com/Divyanshu41/medique.git
   cd medique
   ```

2. Build and run on Windows:
   ```powershell
   .\mvnw.cmd clean verify
   .\mvnw.cmd spring-boot:run
   ```

   If Maven is already installed, `mvn clean verify` and `mvn spring-boot:run` also work.

3. Open:
   - Login: http://localhost:8080/login
   - Patient registration: http://localhost:8080/register

The local H2 database is created automatically in `medique-data.mv.db` in the project directory and persists across restarts. This file is ignored by Git. Registration creates patient accounts; doctor and admin accounts must be created by an administrator.

## Optional: use MySQL instead

The project keeps MySQL support. Create a database first:

```sql
CREATE DATABASE new_hsrole_db;
```

Then set these variables in PowerShell before starting the application:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/new_hsrole_db"
$env:DB_USERNAME="root"
$secure = Read-Host "Enter MySQL password" -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new("", $secure).Password
.\mvnw.cmd spring-boot:run
```

Spring Boot does not automatically load a plain `.env` file; set environment variables in the shell or IDE run configuration.

## Doctor availability and booking

Doctors must save at least one weekly schedule from the doctor dashboard before patients can book them. A weekday has one working interval and an optional break interval. The application generates one-hour slots from those hours. It hides slots that overlap the break, are already reserved, have passed, or fall on a saved leave date.

A doctor cannot change or remove a weekday schedule when future non-cancelled appointments exist on that weekday. A leave date cannot be added when non-cancelled appointments already exist on that date. Existing appointment records are not deleted by these operations.

## Configuration

- `DB_URL`: JDBC URL; defaults to the local H2 file database
- `DB_USERNAME`: database username; defaults to `sa` for H2
- `DB_PASSWORD`: database password; defaults to blank for local H2
- `DDL_AUTO`: Hibernate schema mode (local default: `update`; production: `validate`)
- `SERVER_PORT`: HTTP port (default 8080)
- `SESSION_COOKIE_SECURE`: set true behind HTTPS

## Security

- Never commit `.env`, credentials, API keys or patient data.
- Rotate every credential previously committed to a public repository. Removing it from the latest commit does not erase Git history.
- Use HTTPS and secure cookies in production.
- Use database migrations and set `DDL_AUTO=validate` in production.
- Do not use real patient records in this demo.

## Project status

CI/CD automation is intentionally not included. Build and test the project locally with Maven before running it.
