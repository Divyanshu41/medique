# 🏥 Hospital Appointment System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-red.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A comprehensive Hospital Management System with role-based dashboards built using Spring Boot, Thymeleaf, and MySQL.

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Quick Start](#-quick-start)
- [Project Structure](#-project-structure)
- [Documentation](#-documentation)
- [Screenshots](#-screenshots)
- [Database Setup](#-database-setup)
- [Default Users](#-default-users)
- [API Endpoints](#-api-endpoints)
- [Contributing](#-contributing)
- [License](#-license)

---

## ✨ Features

### ✅ Currently Functional

#### 🔐 Authentication & Authorization
- ✅ User Login with CSRF protection
- ✅ User Registration (auto-assigns PATIENT role)
- ✅ Role-based Access Control (ADMIN, DOCTOR, PATIENT)
- ✅ Password Encryption (BCrypt)
- ✅ Session Management (30-minute timeout)
- ✅ Auto-redirect to role-based dashboards

#### 👨‍💼 Admin Panel
- ✅ **Admin Dashboard** - Statistics and overview
- ✅ **User Management**
  - View all users in a table
  - Edit user details (name, email, phone, etc.)
  - Change user roles (PATIENT ↔ DOCTOR ↔ ADMIN)
  - Delete users
  - Toggle user status (Active/Inactive)
- ✅ **Doctor Management**
  - View all doctors
  - Add new doctors
  - Edit doctor information
  - Delete doctors
- ✅ **Appointment Management**
  - View all appointments
  - Update appointment status (Pending/Confirmed/Completed/Cancelled)

#### 🗄️ Database & Security
- ✅ MySQL Database Integration
- ✅ JPA/Hibernate ORM
- ✅ Connection Pooling (HikariCP)
- ✅ Automatic Schema Creation
- ✅ Entity Auditing (createdAt, updatedAt)
- ✅ Data Validation

---

## 🛠️ Tech Stack

### Backend
- **Framework:** Spring Boot 3.5.4
- **Language:** Java 21
- **Security:** Spring Security 6
- **ORM:** Hibernate 6.6.22 (JPA)
- **Database:** MySQL 8.0
- **Connection Pool:** HikariCP
- **Build Tool:** Maven 3.9+

### Frontend
- **Template Engine:** Thymeleaf
- **CSS Framework:** Bootstrap 5.3.3
- **JavaScript:** Vanilla JS (ES6+)
- **Icons:** Bootstrap Icons

### Additional Features
- **Validation:** Jakarta Bean Validation
- **Monitoring:** Spring Boot Actuator
- **Logging:** SLF4J with Logback

---

## 🚀 Quick Start

### Prerequisites

Before running this project, make sure you have:
- ☕ **Java 21** or higher installed
- 🗄️ **MySQL 8.0** or higher installed and running
- 📦 **Maven 3.9+** (or use included Maven Wrapper)

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/hospital-appointment-system.git
   cd hospital-appointment-system
   ```

2. **Create MySQL Database**
   ```sql
   CREATE DATABASE new-hsrole_db;
   ```

3. **Configure Database Connection**
   
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/new-hsrole_db
   spring.datasource.username=root
   spring.datasource.password=@Dibu__413
   ```

4. **Build the Project**
   ```bash
   # Windows
   mvnw.cmd clean install
   
   # Linux/Mac
   ./mvnw clean install
   ```

5. **Run the Application**
   ```bash
   # Windows
   mvnw.cmd spring-boot:run
   
   # Linux/Mac
   ./mvnw spring-boot:run
   ```

6. **Access the Application**
   
   Open your browser and navigate to:
   ```
   http://localhost:8080/login
   ```

---

## 📁 Project Structure

```
hospital-appointment/
├── src/
│   └── main/
│       ├── java/com/example/hospital/
│       │   ├── config/              # Configuration classes
│       │   │   ├── SecurityConfig.java
│       │   │   ├── DataInitializer.java
│       │   │   └── AuditingConfig.java
│       │   ├── controller/          # REST Controllers
│       │   │   ├── AdminController.java
│       │   │   ├── AuthController.java
│       │   │   ├── DashboardController.java
│       │   │   └── UserManagementController.java
│       │   ├── model/               # Entity classes
│       │   │   └── User.java
│       │   ├── repository/          # JPA Repositories
│       │   │   └── UserRepository.java
│       │   ├── service/             # Business logic
│       │   │   └── UserService.java
│       │   └── hospital.java        # Main application class
│       └── resources/
│           ├── application.properties
│           ├── static/              # CSS, JS, Images
│           │   ├── css/
│           │   └── js/
│           └── templates/           # Thymeleaf HTML templates
│               ├── admin_dashboard.html
│               ├── admin_users.html
│               ├── admin_doctors.html
│               ├── admin_appointments.html
│               ├── login.html
│               ├── register.html
│               └── ...
├── pom.xml                          # Maven configuration
├── mvnw.cmd / mvnw                  # Maven wrapper scripts
└── README.md
```

---

## 📚 Documentation

Comprehensive documentation is available in the following files:

- **[PROJECT_AUDIT_REPORT.md](PROJECT_AUDIT_REPORT.md)** - Complete project audit with functional features analysis
- **[CLEANUP_SUMMARY.md](CLEANUP_SUMMARY.md)** - Cleanup instructions and before/after comparison
- **[ARCHITECTURE_OVERVIEW.md](ARCHITECTURE_OVERVIEW.md)** - System architecture diagrams and data flow
- **[README_HINDI.md](README_HINDI.md)** - Hindi version of documentation

---

## 📸 Screenshots

### Login Page
![Login Page](docs/screenshots/login.png)

### Admin Dashboard
![Admin Dashboard](docs/screenshots/admin-dashboard.png)

### User Management
![User Management](docs/screenshots/user-management.png)

### Doctor Management
![Doctor Management](docs/screenshots/doctor-management.png)

---

## 🗄️ Database Setup

### Automatic Setup (Recommended)

The application automatically:
- Creates database tables on first run
- Creates indexes for optimized queries
- Initializes with default test users

### Manual Setup (Optional)

If you prefer manual setup, run:
```sql
CREATE DATABASE new-hsrole_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE new-hsrole_db;
```

Then start the application. Tables will be created automatically.

---

## 👥 Default Users

On first run, the following test users are automatically created:

| Email | Password | Role | Access Level |
|-------|----------|------|--------------|
| `admin@example.com` | `Admin@123` | ADMIN | Full system access |
| `doctor@hospital.com` | `doctor123` | DOCTOR | Doctor dashboard |
| `patient@hospital.com` | `patient123` | PATIENT | Patient dashboard |

**⚠️ Important:** Change these passwords in production!

---

## 🌐 API Endpoints

### Public Endpoints (No Authentication)
```
GET  /login           - Show login page
POST /login           - Process login
GET  /register        - Show registration page
POST /register        - Process registration
```

### Protected Endpoints (Authentication Required)

#### Admin Endpoints (`/admin/*`)
```
GET  /admin/dashboard                    - Admin dashboard
GET  /admin/users                        - List all users
GET  /admin/users/edit/{id}              - Edit user form
POST /admin/users/edit/{id}              - Save user changes
POST /admin/users/change-role/{id}       - Change user role
GET  /admin/users/delete/{id}            - Delete user
GET  /admin/users/toggle-status/{id}     - Toggle user status
GET  /admin/doctors                      - List all doctors
GET  /admin/add-doctor                   - Add doctor form
POST /admin/add-doctor                   - Save new doctor
GET  /admin/doctors/edit/{id}            - Edit doctor
POST /admin/doctors/edit/{id}            - Save doctor changes
GET  /admin/doctors/delete/{id}          - Delete doctor
GET  /admin/appointments                 - List appointments
POST /admin/appointments/update-status/{id} - Update status
```

#### General Endpoints
```
GET  /                - Redirect to login or dashboard
GET  /dashboard       - Role-based dashboard redirect
GET  /logout          - Logout user
```

---

## 🔧 Configuration

### Application Properties

Key configuration options in `application.properties`:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/new-hsrole_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=create    # Change to 'update' after first run
spring.jpa.show-sql=false

# Session Timeout
server.servlet.session.timeout=30m

# Logging
logging.level.com.example.hospital=DEBUG
logging.file.name=logs/app.log
```

### Security Configuration

- **CSRF Protection:** Enabled
- **Password Encoding:** BCrypt
- **Session Timeout:** 30 minutes
- **Cookie Security:** HttpOnly enabled

---

## 🧪 Testing

### Run Tests
```bash
# Windows
mvnw.cmd test

# Linux/Mac
./mvnw test
```

### Test Users
Use the default test users (see [Default Users](#-default-users) section)

---

## 📊 Monitoring

### Actuator Endpoints

The application includes Spring Boot Actuator for monitoring:

- **Health Check:** http://localhost:8080/actuator/health
- **Info:** http://localhost:8080/actuator/info
- **Metrics:** http://localhost:8080/actuator/metrics

---

## 🐛 Troubleshooting

### Common Issues

1. **Port 8080 already in use**
   ```
   Solution: Change port in application.properties
   server.port=8081
   ```

2. **Database connection failed**
   ```
   Solution: Verify MySQL is running and credentials are correct
   ```

3. **Login not working**
   ```
   Solution: Check if CSRF token is present in login form
   ```

4. **Data lost on restart**
   ```
   Solution: Change spring.jpa.hibernate.ddl-auto from 'create' to 'update'
   ```

For more troubleshooting tips, see logs at `logs/app.log`

---

## 🔄 Project Cleanup

If you cloned this project and want to remove unnecessary files:

1. Read **PROJECT_AUDIT_REPORT.md** for detailed analysis
2. Run **CLEANUP_INSTRUCTIONS.bat** (Windows) to remove clutter
3. Follow steps in **CLEANUP_SUMMARY.md**

**Benefits after cleanup:**
- 75% fewer files
- 50% smaller JAR size
- 37% faster startup time

---

## 🚀 Deployment

### Build JAR File
```bash
mvnw.cmd clean package
```

### Run JAR
```bash
java -jar target/hospital-appointment-0.0.1-SNAPSHOT.jar
```

### Docker (Future)
```bash
# Build image
docker build -t hospital-appointment .

# Run container
docker run -p 8080:8080 hospital-appointment
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Your Name**
- GitHub: [@yourusername](https://github.com/yourusername)
- LinkedIn: [Your Profile](https://linkedin.com/in/yourprofile)

---

## 🙏 Acknowledgments

- Spring Boot Team for the amazing framework
- Bootstrap for the UI components
- Thymeleaf for the template engine
- MySQL for the database

---

## 📞 Support

For support, email your-email@example.com or open an issue on GitHub.

---

## 🗺️ Roadmap

### Upcoming Features (Not Yet Implemented)
- [ ] Patient appointment booking system
- [ ] Doctor's appointment view and management
- [ ] Email notifications
- [ ] SMS OTP authentication
- [ ] PDF report generation
- [ ] Excel export functionality
- [ ] Real-time notifications (WebSocket)
- [ ] Payment gateway integration
- [ ] OAuth2 social login (Google, Facebook)
- [ ] Biometric authentication

---

## ⭐ Show your support

Give a ⭐️ if this project helped you!

---

<div align="center">
  <sub>Built with ❤️ using Spring Boot</sub>
</div>
