# Employee Lifecycle Management System

A comprehensive **Employee Lifecycle Management System (ELMS)** built using **Spring Boot** that streamlines the complete employee journey within an organization. The system manages everything from candidate onboarding to employee management, manager assignment, leave tracking, payroll, and offboarding while ensuring secure, role-based access.

---

## 🚀 Features

- **Role-Based Access Control (RBAC)**
  - Admin
  - HR
  - Manager
  - Employee

- **Candidate Management**
  - Register new candidates
  - Candidate profile management
  - Convert candidates into employees

- **Employee Management**
  - Employee onboarding
  - Profile management
  - Employee information updates
  - Department allocation

- **Manager Assignment**
  - Assign managers to employees
  - View reporting hierarchy

- **Leave Management**
  - Apply for leave
  - Approve/Reject leave requests
  - Leave history tracking

- **Payroll Management**
  - Generate payroll
  - View salary details
  - Employee payroll records

- **Authentication & Authorization**
  - Secure login
  - Role-based access using Spring Security

- **Logging**
  - Application logging using SLF4J
  - Error and activity tracking

- **Testing**
  - Unit testing using JUnit

---

## 🛠 Tech Stack

### Backend
- Spring Boot
- Spring Security
- Spring Data JPA

### Database
- SQL Database

### Frontend
- HTML
- CSS
- JavaScript

### Testing
- JUnit

### Logging
- SLF4J

### Build Tool
- Maven

### Version Control
- Git
- GitHub

---

## 📂 Project Structure

```
Employee-Lifecycle-Management-System
│
├── src
│   ├── main
│   │   ├── java
│   │   ├── resources
│   │   └── webapp
│   └── test
│
├── pom.xml
└── README.md
```

---

## ⚙️ Installation

### Prerequisites

- Java 17+
- Maven
- SQL Database
- IDE (IntelliJ IDEA / Eclipse / VS Code)

### Clone Repository

```bash
git clone https://github.com/coderheist/Employee-Lifecycle-Mangement-System.git
```

### Navigate to Project

```bash
cd Employee-Lifecycle-Mangement-System
```

### Configure Database

Update the database configuration in:

```
application.properties
```

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/elms
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
```

### Build Project

```bash
mvn clean install
```

### Run Application

```bash
mvn spring-boot:run
```

Application runs at:

```
http://localhost:8080
```

---

## 👥 User Roles

### Admin
- Manage users
- Assign roles
- Manage departments
- View reports

### HR
- Manage candidates
- Onboard employees
- Process payroll
- Manage employee records

### Manager
- View team members
- Approve leave requests
- Monitor employee information

### Employee
- View profile
- Apply for leave
- View payroll
- Update personal information

---

## 🔒 Security

- Spring Security Authentication
- Role-Based Authorization
- Protected Endpoints
- Secure Session Management

---

## 🧪 Testing

The project includes **JUnit** test cases for validating:

- Service layer
- Business logic
- Repository operations

Run tests:

```bash
mvn test
```

---

## 📊 Logging

SLF4J is used for:

- Request logging
- Error logging
- Exception tracking
- Debugging

---

## 🎯 Future Enhancements

- Email Notifications
- Attendance Management
- Performance Evaluation
- Document Upload
- Dashboard Analytics
- JWT Authentication
- Docker Deployment
- REST API Documentation (Swagger)

---

## 👨‍💻 Authors

Developed as a full-stack enterprise application using Spring Boot to automate and manage the complete employee lifecycle with secure role-based access and efficient HR workflows.

---

## 📄 License

This project is intended for educational and learning purposes.
