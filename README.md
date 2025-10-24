# 🏥 Hospital Appointment Management System (Simplified)

A minimal hospital appointment management system built with **Spring Boot 3**, featuring role-based dashboards for Admin and Users (Patients).

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue)
![License](https://img.shields.io/badge/License-Apache%202.0-blue)

## ✨ Features

### 👤 User Features
- ✅ User registration and login
- ✅ Role-based dashboard access

### 👨‍💼 Admin Features
- ✅ Admin dashboard access
- ✅ User management overview

## 🛠 Tech Stack

### Backend
- **Java 21**
- **Spring Boot 3.5.4**
- **Spring Security** (Authentication & Authorization)
- **Spring Data JPA** (ORM)
- **MySQL** (Database)

### Frontend
- **Thymeleaf** (Server-side rendering)
- **HTML5/CSS3/JavaScript**
- **Bootstrap 5** (UI Framework)

## 📦 Prerequisites

- ☕ **Java 21** or higher
- 🛢 **MySQL 8.0+**
- 📦 **Maven 3.6+**

## 🔧 Installation

### 1. Clone the Repository
```bash
git clone https://github.com/Divyanshu41/medique.git
cd medique
```

### 2. Setup MySQL Database
Create a database named `new-hsrole_db` in MySQL.

### 3. Configure Database
Edit `src/main/resources/application.properties` with your MySQL credentials.

### 4. Build the Project
```bash
mvnw clean package
```

## ▶️ Running the Application

```bash
java -jar target/medique-appointment-0.0.1-SNAPSHOT.jar
```

The application will start on **http://localhost:8080**

## 📁 Project Structure

```
medique/
├── src/main/java/com/example/medique/
│   ├── controller/      # Controllers
│   ├── model/           # JPA entities
│   ├── repository/      # Data repositories
│   ├── service/         # Business logic
│   └── config/security  # Config and security
├── src/main/resources/
│   ├── templates/       # Thymeleaf templates
│   └── application.properties
├── pom.xml
└── README.md
```

## 🔐 Default Users

Create users manually in the database or via registration.

## Support

For issues, check the code — it's minimal and easy to understand.

---
Note: This repository was renamed from an earlier structure; the project has been flattened and cleaned.
