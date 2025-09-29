# Task Management System

A Java-based task management application using Spring JDBC and PostgreSQL for managing users, projects, and tasks with full CRUD operations.

## 🛠️ Technologies

- **Java 21** - Core language
- **Spring JDBC** - Data access
- **PostgreSQL** - Database
- **HikariCP** - Connection pooling
- **Maven** - Build tool

## 📋 Features

- **User Management** - Create, read, update, delete users with role-based access
- **Project Management** - Full project lifecycle with date tracking
- **Task Management** - Task assignment with priority, status, and due dates
- **Advanced Queries** - Find tasks by project, user, status, and overdue tasks
- **Data Relationships** - Foreign key constraints between users, projects, and tasks

## 🗄️ Database Schema


## 🚀 Quick Start

### Prerequisites
- Java 21+
- PostgreSQL
- Maven

### Setup
1. **Clone repository:**
2. **Create database:**
3. **Run schema:**
4. **Configure database connection in `DatabaseConfig.java`:**
5. **Build and run:**

## 📁 Project Structure

src/main/java/com/task/Task_management/
├── config/DatabaseConfig.java # Spring JDBC configuration
├── model/ # Entity classes
├── mapper/ # Row mappers for database
├── dao/ # Data access layer
└── main/App.java # Application entry point


## ✅ Testing

Run `App.java` to test all functionality. Expected output:

## 🔧 Available Operations

### UserDAO
- `findAll()`, `findById(int)`, `save(User)`, `update(User)`, `deleteById(int)`

### ProjectDAO
- `findAll()`, `findById(int)`, `save(Project)`, `update(Project)`, `deleteById(int)`
- `findActiveProjects()`, `countProjects()`

### TaskDAO
- `findAll()`, `findById(int)`, `save(Task)`, `update(Task)`, `deleteById(int)`
- `findByProjectId(int)`, `findByUserId(int)`, `findByStatus(String)`
- `findOverdueTasks()`, `countTasksByProject(int)`

## 👨‍💻 Author

Created to demonstrate Spring JDBC with PostgreSQL integration patterns.

