# 🏥 Hospital Appointment System - Architecture Overview

## 📐 System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     USER INTERFACE LAYER                     │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Login Page │  │ Register Page│  │  Dashboards  │      │
│  │  (Thymeleaf) │  │  (Thymeleaf) │  │  (Thymeleaf) │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│                                                               │
│  Static Resources: CSS, JavaScript, Images                   │
│                                                               │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    SECURITY LAYER                            │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌────────────────────────────────────────────────────┐     │
│  │           Spring Security Filter Chain             │     │
│  │  • CSRF Protection                                 │     │
│  │  • Authentication (Form Login)                     │     │
│  │  • Authorization (Role-based: ADMIN, DOCTOR,       │     │
│  │                   PATIENT)                         │     │
│  │  • Session Management                              │     │
│  │  • Password Encryption (BCrypt)                    │     │
│  └────────────────────────────────────────────────────┘     │
│                                                               │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                   CONTROLLER LAYER                           │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────────┐  ┌──────────────────┐                │
│  │ AuthController   │  │ DashboardController│               │
│  │ /register        │  │ /                 │               │
│  │ /login           │  │ /dashboard        │               │
│  └──────────────────┘  └──────────────────┘                │
│                                                               │
│  ┌──────────────────┐  ┌──────────────────┐                │
│  │ AdminController  │  │UserMgmtController │               │
│  │ /admin/dashboard │  │ /admin/users      │               │
│  │ /admin/doctors   │  │ /admin/users/edit │               │
│  │ /admin/appts     │  │ /admin/users/del  │               │
│  └──────────────────┘  └──────────────────┘                │
│                                                               │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                     SERVICE LAYER                            │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌────────────────────────────────────────────────────┐     │
│  │              UserService.java                      │     │
│  │  • Business Logic                                  │     │
│  │  • User Management Operations                      │     │
│  │  • Role Validation                                 │     │
│  │  • Data Transformation                             │     │
│  └────────────────────────────────────────────────────┘     │
│                                                               │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                  REPOSITORY LAYER                            │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌────────────────────────────────────────────────────┐     │
│  │         UserRepository.java (JPA)                  │     │
│  │  • findByEmail()                                   │     │
│  │  • findAll()                                       │     │
│  │  • save()                                          │     │
│  │  • delete()                                        │     │
│  └────────────────────────────────────────────────────┘     │
│                                                               │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                   PERSISTENCE LAYER                          │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌────────────────────────────────────────────────────┐     │
│  │          Hibernate/JPA (ORM)                       │     │
│  │  • Entity Mapping                                  │     │
│  │  • Transaction Management                          │     │
│  │  • Query Generation                                │     │
│  │  • Connection Pooling (HikariCP)                   │     │
│  └────────────────────────────────────────────────────┘     │
│                                                               │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                     DATABASE LAYER                           │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌────────────────────────────────────────────────────┐     │
│  │            MySQL Database                          │     │
│  │  Database: new-hsrole_db                           │     │
│  │  Tables:                                           │     │
│  │    • users (id, name, email, password, role, etc.) │     │
│  │    • [other tables if added]                       │     │
│  └────────────────────────────────────────────────────┘     │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 Request Flow Example: Admin Login

```
1. User → http://localhost:8080/login
   ↓
2. Browser → GET /login → AuthController.showLoginForm()
   ↓
3. Controller → Returns "login.html" template
   ↓
4. User fills form (email + password) + CSRF token
   ↓
5. Browser → POST /login → Spring Security Filter
   ↓
6. Security Filter → Validates CSRF token
   ↓
7. Security Filter → Authenticates user (UserDetailsService)
   ↓
8. UserRepository → findByEmail(email)
   ↓
9. MySQL → Query users table
   ↓
10. MySQL → Returns user data
    ↓
11. Security → Validates password (BCrypt)
    ↓
12. Security → Creates Authentication object
    ↓
13. Security → Redirects to /dashboard
    ↓
14. DashboardController → Checks user role
    ↓
15. If ADMIN → redirect:/admin/dashboard
    ↓
16. AdminController → /admin/dashboard endpoint
    ↓
17. Controller → Returns "admin_dashboard.html"
    ↓
18. Browser → Displays Admin Dashboard
```

---

## 🗂️ Data Model

```
┌─────────────────────────────────────────┐
│              User Entity                 │
├─────────────────────────────────────────┤
│ • id: Long (Primary Key)                │
│ • name: String (NOT NULL)               │
│ • email: String (UNIQUE, NOT NULL)      │
│ • password: String (BCrypt encoded)     │
│ • role: String (PATIENT/DOCTOR/ADMIN)   │
│ • phone: String (10 digits)             │
│ • address: String                        │
│ • age: Integer (1-150)                  │
│ • gender: String (MALE/FEMALE/OTHER)    │
│ • isActive: Boolean (default: true)     │
│ • createdAt: LocalDateTime              │
│ • updatedAt: LocalDateTime              │
└─────────────────────────────────────────┘

Indexes:
- idx_user_email (email)
- idx_user_role (role)

Validations:
- Email: Valid format required
- Password: Min 6 characters
- Phone: Exactly 10 digits
- Role: Must be PATIENT, DOCTOR, or ADMIN
```

---

## 🎭 Role-based Access Control

```
┌──────────────────────────────────────────────────────────┐
│                     ROLE HIERARCHY                        │
├──────────────────────────────────────────────────────────┤
│                                                            │
│  ┌───────────────────────────────────────────────┐       │
│  │              ADMIN (Highest)                  │       │
│  │  ✓ Full system access                         │       │
│  │  ✓ User management (CRUD)                     │       │
│  │  ✓ Doctor management (CRUD)                   │       │
│  │  ✓ Appointment management                     │       │
│  │  ✓ Change user roles                          │       │
│  │  ✓ Delete users                               │       │
│  │  ✓ View all data                              │       │
│  └───────────────────────────────────────────────┘       │
│                         ↓                                  │
│  ┌───────────────────────────────────────────────┐       │
│  │              DOCTOR (Medium)                  │       │
│  │  ✓ View own profile                           │       │
│  │  ✓ View assigned appointments (future)        │       │
│  │  ✓ Update appointment status (future)         │       │
│  │  ✗ Cannot manage other users                  │       │
│  │  ✗ Cannot delete data                         │       │
│  └───────────────────────────────────────────────┘       │
│                         ↓                                  │
│  ┌───────────────────────────────────────────────┐       │
│  │              PATIENT (Lowest)                 │       │
│  │  ✓ View own profile                           │       │
│  │  ✓ Book appointments (future)                 │       │
│  │  ✓ View own appointments (future)             │       │
│  │  ✗ Cannot manage other users                  │       │
│  │  ✗ Cannot access admin panel                  │       │
│  └───────────────────────────────────────────────┘       │
│                                                            │
└──────────────────────────────────────────────────────────┘
```

---

## 🔐 Security Features

```
┌──────────────────────────────────────────────────────────┐
│                  SECURITY MECHANISMS                      │
├──────────────────────────────────────────────────────────┤
│                                                            │
│  1. CSRF Protection                                       │
│     • Token-based validation                              │
│     • Prevents Cross-Site Request Forgery                 │
│     • Token in hidden form field                          │
│                                                            │
│  2. Password Encryption                                   │
│     • BCrypt algorithm                                    │
│     • One-way hashing                                     │
│     • Salt included automatically                         │
│                                                            │
│  3. Session Management                                    │
│     • 30-minute timeout                                   │
│     • HttpOnly cookies (prevents XSS)                     │
│     • Auto-logout on expiry                               │
│                                                            │
│  4. Role-based Authorization                              │
│     • URL pattern matching                                │
│     • Method-level security                               │
│     • Hierarchical roles                                  │
│                                                            │
│  5. SQL Injection Prevention                              │
│     • JPA/Hibernate parameterized queries                 │
│     • No raw SQL used                                     │
│                                                            │
└──────────────────────────────────────────────────────────┘
```

---

## 🌐 URL Routes Map

```
Public Routes (No Auth Required):
├── GET  /login           → Show login page
├── POST /login           → Process login (handled by Spring Security)
├── GET  /register        → Show registration page
└── POST /register        → Process registration

Protected Routes (Auth Required):
├── GET  /                → Redirect to /dashboard
├── GET  /dashboard       → Role-based redirect
│
├── ADMIN Routes (/admin/*)
│   ├── GET  /admin/dashboard              → Admin dashboard
│   ├── GET  /admin/users                  → List all users
│   ├── GET  /admin/users/edit/{id}        → Edit user form
│   ├── POST /admin/users/edit/{id}        → Save user changes
│   ├── POST /admin/users/change-role/{id} → Change user role
│   ├── GET  /admin/users/delete/{id}      → Delete user
│   ├── GET  /admin/users/toggle-status/{id} → Activate/Deactivate
│   ├── GET  /admin/doctors                → List all doctors
│   ├── GET  /admin/add-doctor             → Add doctor form
│   ├── POST /admin/add-doctor             → Save new doctor
│   ├── GET  /admin/doctors/edit/{id}      → Edit doctor form
│   ├── POST /admin/doctors/edit/{id}      → Save doctor changes
│   ├── GET  /admin/doctors/delete/{id}    → Delete doctor
│   ├── GET  /admin/appointments           → List all appointments
│   └── POST /admin/appointments/update-status/{id} → Update status
│
├── DOCTOR Routes (Future Implementation)
│   ├── GET  /doctor/dashboard             → Doctor dashboard
│   └── GET  /doctor/appointments          → Assigned appointments
│
└── PATIENT Routes (Partial Implementation)
    ├── GET  /user/dashboard               → Patient dashboard
    └── GET  /my-appointments              → Patient's appointments
```

---

## 📦 Project Dependencies

```
Core Dependencies (Essential):
├── spring-boot-starter-web          → REST API, MVC
├── spring-boot-starter-data-jpa     → Database ORM
├── spring-boot-starter-security     → Authentication & Authorization
├── spring-boot-starter-thymeleaf    → HTML templating
├── thymeleaf-extras-springsecurity6 → Security integration
├── spring-boot-starter-validation   → Input validation
├── mysql-connector-j                → MySQL driver
└── lombok                           → Reduce boilerplate code

Optional Dependencies (Keep if needed):
└── spring-boot-starter-actuator     → Health checks, metrics

Development Dependencies:
├── spring-boot-starter-test         → Unit testing
└── spring-security-test             → Security testing
```

---

## 🚀 Deployment Architecture (Future)

```
┌────────────────────────────────────────────────────────┐
│                   Production Setup                      │
├────────────────────────────────────────────────────────┤
│                                                          │
│  Internet                                               │
│     ↓                                                    │
│  Load Balancer (Nginx/Apache)                           │
│     ↓                                                    │
│  Application Server (Spring Boot on Tomcat)             │
│     ↓                                                    │
│  MySQL Database (Separate Server)                       │
│     ↓                                                    │
│  Redis Cache (Optional)                                 │
│     ↓                                                    │
│  File Storage (AWS S3 / Local NAS)                      │
│                                                          │
└────────────────────────────────────────────────────────┘
```

---

## 📈 Performance Optimization

```
Current Optimizations:
├── Connection Pooling (HikariCP)
│   ├── Max Pool Size: 20
│   ├── Min Idle: 5
│   └── Connection Timeout: 30s
│
├── Session Management
│   └── Timeout: 30 minutes
│
├── Database Indexing
│   ├── Index on email column
│   └── Index on role column
│
└── Static Resource Caching
    └── Thymeleaf cache disabled (dev mode)
```

---

**📚 For detailed cleanup instructions, see CLEANUP_SUMMARY.md**
**📋 For complete audit report, see PROJECT_AUDIT_REPORT.md**
