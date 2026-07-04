# Smart Exam Seating System - Java Edition

## Overview

A complete Java rewrite of the exam seating allocation system using:
- **Core Java** (JDK 11+)
- **JDBC** for database operations
- **MySQL** for persistent storage
- **DAO Pattern** for data access abstraction
- **Service Layer** for business logic
- **OOP Principles** throughout

## Architecture

### Project Structure
```
java/
├── src/main/java/com/collegeadmin/
│   ├── config/           # Database configuration
│   ├── model/            # Entity classes
│   ├── dao/              # Data Access Objects
│   ├── service/          # Business logic services
│   ├── http/             # HTTP handlers
│   └── ExamSeatingApp.java  # Entry point
├── src/main/resources/   # Configuration files
└── pom.xml              # Maven dependencies
```

### Key Components

#### Models
- **Student**: Exam participant
- **Invigilator**: Exam supervision staff
- **Hall**: Examination venue
- **ExamSession**: Examination event
- **SeatAllocation**: Student seating assignment
- **InvigilatorDuty**: Staff duty assignment

#### DAOs (Data Access Objects)
- **StudentDAO**: Student CRUD operations
- **InvigilatorDAO**: Invigilator CRUD operations
- **HallDAO**: Hall/Room management
- **ExamSessionDAO**: Session management
- **SeatAllocationDAO**: Seat allocation queries
- **InvigilatorDutyDAO**: Duty assignment queries

#### Services
- **AllocationService**: Core allocation algorithm
- **AdminService**: Authentication and security

#### Database
- Connection pooling with HikariCP
- Auto-schema initialization on startup
- Proper transaction handling

## Features Preserved

✅ **Admin Login** - Secure authentication
✅ **Student Search** - Find seat allocation by roll number
✅ **Invigilator Search** - Find duty assignment by email
✅ **Seat Allocation** - Automatic seating arrangement
✅ **Hall Management** - Add/update exam halls
✅ **Session Management** - Create exam sessions
✅ **Invigilator Duty Assignment** - Automatic staff assignment
✅ **Audit Logging** - Track all operations

## Setup Instructions

### Prerequisites
- Java Development Kit (JDK) 11 or higher
- Maven 3.6+
- MySQL Server 5.7+

### Database Setup
```sql
CREATE DATABASE exam_seating_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON exam_seating_system.* TO 'root'@'localhost' IDENTIFIED BY '';
FLUSH PRIVILEGES;
```

### Configuration
Edit `DatabaseConfig.java` to match your MySQL credentials:
```java
private static final String DB_HOST = "localhost";
private static final String DB_PORT = "3306";
private static final String DB_NAME = "exam_seating_system";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "";
```

### Build and Run
```bash
# Build the project
mvn clean package

# Run the application
mvn exec:java -Dexec.mainClass="com.collegeadmin.ExamSeatingApp"

# Or run the JAR directly
java -jar target/exam-seating-system-1.0.0.jar
```

The application will start on `http://localhost:8083`

## API Endpoints (Planned)

- `GET /login` - Admin login page
- `POST /login` - Process login
- `GET /dashboard` - Admin dashboard
- `GET /api/students` - List all students
- `POST /api/students` - Add new student
- `GET /api/invigilators` - List invigilators
- `POST /api/invigilators` - Add new invigilator
- `GET /api/halls` - List halls
- `POST /api/halls` - Add new hall
- `POST /api/allocate` - Generate allocations
- `GET /api/student-seat/{rollNumber}` - Find student seat
- `GET /api/invigilator-duty/{email}` - Find invigilator duty

## OOP Design Principles Applied

### Encapsulation
- Private fields with public getters/setters
- DAO pattern hides database implementation
- Service layer abstracts business logic

### Inheritance & Polymorphism
- HttpHandler base class for HTTP route handlers
- Common methods in parent class

### Single Responsibility
- Each DAO handles one entity type
- Services handle business logic
- Models represent data structures

### Dependency Injection
- Services inject DAOs
- Loose coupling between layers

## Database Schema

The application auto-creates the following tables:
- `students` - Student records
- `invigilators` - Staff records
- `halls` - Examination halls
- `exam_sessions` - Exam events
- `seat_allocations` - Seating assignments
- `invigilator_duties` - Staff assignments
- `audit_logs` - Operation audit trail

## Improvements Over Python Version

1. **Type Safety**: Full compile-time type checking
2. **Performance**: Faster execution, better memory management
3. **Scalability**: Connection pooling, better concurrency handling
4. **Maintainability**: Clear separation of concerns
5. **Error Handling**: Comprehensive exception handling
6. **Logging**: SLF4J with Logback configuration
7. **Database**: ACID compliance, proper indexing
8. **Security**: Prepared statements prevent SQL injection

## Future Enhancements

- [ ] Spring Boot framework integration
- [ ] REST API with comprehensive endpoints
- [ ] WebSocket support for real-time updates
- [ ] Advanced seat allocation algorithms
- [ ] Email/SMS notifications
- [ ] Multi-language support
- [ ] Reporting and analytics
- [ ] Mobile app integration

## Contributing

Contributions are welcome! Please follow the established code structure and patterns.

## License

MIT License

## Support

For issues or questions, please create an issue in the repository.
