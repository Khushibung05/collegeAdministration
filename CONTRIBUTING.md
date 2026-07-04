# Contributing to Exam Seating System

Thank you for your interest in contributing! This document provides guidelines and instructions for contributing.

## 📋 Code of Conduct

- Be respectful and inclusive
- Focus on code quality and functionality
- Help others learn and grow
- Report issues responsibly

## 🎯 How to Contribute

### 1. Fork the Repository

```bash
git clone https://github.com/YOUR_USERNAME/collegeAdministration.git
cd collegeAdministration
git remote add upstream https://github.com/Khushibung05/collegeAdministration.git
```

### 2. Create a Feature Branch

```bash
git checkout -b feature/your-feature-name
```

### 3. Make Your Changes

- Write clean, readable code
- Follow existing code style
- Add comments for complex logic
- Use meaningful commit messages

### 4. Test Your Changes

```bash
mvn clean test
mvn compile
mvn exec:java -Dexec.mainClass="com.collegeadmin.ExamSeatingApp"
```

### 5. Commit and Push

```bash
git add .
git commit -m "Add: Brief description of changes"
git push origin feature/your-feature-name
```

### 6. Create Pull Request

1. Go to GitHub
2. Create PR from your fork to main repository
3. Fill in PR template
4. Wait for review

## 📝 Commit Message Guidelines

```
Format: [TYPE]: Brief description

Types:
- Add: New feature
- Fix: Bug fix
- Refactor: Code refactoring
- Docs: Documentation
- Test: Testing
- Chore: Build, dependencies

Examples:
Add: Student DAO CRUD operations
Fix: SQL injection vulnerability in StudentDAO
Refactor: Simplify allocation algorithm
Docs: Update README with deployment guide
```

## 🏗️ Code Style

### Java Conventions

```java
// Classes and interfaces
public class StudentDAO { }

// Methods
public void createStudent(Student student) { }

// Constants
private static final int MAX_STUDENTS = 1000;

// Variables
private String studentName;

// Indentation: 4 spaces
if (condition) {
    doSomething();
}
```

### DAO Pattern

```java
// Follow DAO pattern
public Optional<Student> findById(int id) throws SQLException { }

// Use PreparedStatements
String sql = "SELECT * FROM students WHERE id = ?";
pstmt.setInt(1, id);
```

### Exception Handling

```java
// Use specific exceptions
try {
    // Database operation
} catch (SQLException e) {
    logger.error("Database error", e);
    throw new RuntimeException("Failed to create student", e);
}
```

## ✅ PR Requirements

### Code Quality
- [ ] Code compiles without errors
- [ ] No warnings in IDE
- [ ] Follows code style guidelines
- [ ] Comments on complex logic
- [ ] No commented-out code

### Testing
- [ ] All tests pass: `mvn clean test`
- [ ] Manual testing completed
- [ ] No regression in existing features

### Documentation
- [ ] README updated (if needed)
- [ ] Code comments added
- [ ] Javadoc for public methods
- [ ] Commit messages clear

### Security
- [ ] No hardcoded credentials
- [ ] No SQL injection vulnerabilities
- [ ] Proper input validation
- [ ] Secure error handling

## 🔍 Review Process

1. **Automated Checks**: GitHub Actions runs tests
2. **Code Review**: Maintainers review code
3. **Discussion**: Feedback and improvements
4. **Approval**: PR approved by maintainers
5. **Merge**: Changes merged to main

## 🐛 Reporting Issues

### Bug Report Template

```markdown
## Description
Clear description of the bug

## Steps to Reproduce
1. Step 1
2. Step 2
3. Step 3

## Expected Behavior
What should happen

## Actual Behavior
What actually happens

## Environment
- Java version: 
- Maven version: 
- MySQL version: 
- OS: 

## Logs
```
error output here
```

## Screenshots
If applicable
```

### Feature Request Template

```markdown
## Feature Description
Clear description of the feature

## Use Case
Why is this feature needed?

## Proposed Solution
How should it work?

## Alternatives
Other possible approaches
```

## 📚 Documentation

When adding features, update:

1. **README.md** - Overview and usage
2. **DEPLOYMENT.md** - Deployment steps
3. **Code comments** - Inline documentation
4. **Javadoc** - Method documentation

### Documentation Example

```java
/**
 * Find a student by roll number.
 * 
 * @param rollNumber the student's roll number
 * @return Optional containing student if found
 * @throws SQLException if database error occurs
 * 
 * Example:
 * Optional<Student> student = studentDAO.findByRollNumber("STU001");
 */
public Optional<Student> findByRollNumber(String rollNumber) throws SQLException {
    // Implementation
}
```

## 🔄 Development Workflow

```
1. Pick an issue or create feature request
2. Discuss in comments
3. Fork repository
4. Create feature branch
5. Make changes with commits
6. Test thoroughly
7. Update documentation
8. Create pull request
9. Respond to reviews
10. Merge when approved
```

## 🎓 Project Structure

```
src/main/java/com/collegeadmin/
├── config/           # Database and app configuration
├── model/            # Entity classes (Student, Invigilator, etc.)
├── dao/              # Data Access Objects
├── service/          # Business logic services
├── http/             # HTTP request handlers
└── ExamSeatingApp.java  # Main entry point
```

## 🚀 Building and Testing

```bash
# Build
mvn clean install

# Run tests
mvn test

# Run application
mvn exec:java -Dexec.mainClass="com.collegeadmin.ExamSeatingApp"

# Package JAR
mvn package -DskipTests

# Check code
mvn checkstyle:check
```

## 💡 Tips for Contributors

1. **Start small** - Fix a bug or improve documentation first
2. **Discuss first** - Create an issue before major changes
3. **Follow patterns** - Use existing code as reference
4. **Test thoroughly** - Test edge cases and error scenarios
5. **Write comments** - Explain complex logic
6. **Update docs** - Keep documentation in sync
7. **Be patient** - Reviews take time
8. **Ask questions** - Don't hesitate to ask for help

## ❓ Questions?

- Check existing [issues](https://github.com/Khushibung05/collegeAdministration/issues)
- Open a discussion
- Contact maintainers

---

**Thank you for contributing! Your help makes this project better.** 🙏
