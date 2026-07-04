# Deployment Guide - Exam Seating System (Java Edition)

## 📋 Prerequisites

### System Requirements
- **OS**: Linux, macOS, or Windows
- **Java**: JDK 11 or higher
- **Maven**: 3.6 or higher
- **MySQL**: 5.7 or higher
- **RAM**: 512MB minimum (1GB recommended)
- **Disk Space**: 2GB minimum

### Check Prerequisites

```bash
# Check Java
java -version
# Expected: openjdk version "11" or higher

# Check Maven
mvn -version
# Expected: Apache Maven 3.6.3 or higher

# Check MySQL
mysql --version
# Expected: mysql Ver X.X.XX
```

---

## 🔧 Installation

### Step 1: Clone Repository

```bash
git clone https://github.com/Khushibung05/collegeAdministration.git
cd collegeAdministration
```

### Step 2: Create Database

```bash
# Connect to MySQL
mysql -u root -p

# Execute SQL
CREATE DATABASE IF NOT EXISTS exam_seating_system 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

GRANT ALL PRIVILEGES ON exam_seating_system.* 
    TO 'examuser'@'localhost' 
    IDENTIFIED BY 'your-secure-password';

FLUSH PRIVILEGES;
EXIT;
```

Or use script:

```bash
mysql -u root -p < setup-database.sql
```

### Step 3: Configure Database Connection

Edit `src/main/java/com/collegeadmin/config/DatabaseConfig.java`:

```java
private static final String DB_HOST = "localhost";
private static final String DB_PORT = "3306";
private static final String DB_NAME = "exam_seating_system";
private static final String DB_USER = "examuser";          // Your username
private static final String DB_PASSWORD = "your-secure-password";  // Your password
```

### Step 4: Build Application

```bash
# Clean build
mvn clean install

# Expected output:
# [INFO] BUILD SUCCESS
# [INFO] Total time: X.XXs
```

### Step 5: Run Application

```bash
# Development mode (with logging)
mvn exec:java -Dexec.mainClass="com.collegeadmin.ExamSeatingApp"

# Production mode (as JAR)
mvn clean package -DskipTests
java -jar target/exam-seating-system-1.0.0.jar
```

### Step 6: Verify Installation

```bash
# Check if server is running
curl http://localhost:8083/login

# Should return HTML login page

# Or open in browser
# http://localhost:8083
```

---

## 🚀 Production Deployment

### Option 1: Standalone JAR

#### Create Executable JAR

```bash
mvn clean package -DskipTests
ls -lh target/exam-seating-system-1.0.0.jar
```

#### Run with Custom Settings

```bash
# Increase memory allocation
java -Xmx512m -Xms256m -jar target/exam-seating-system-1.0.0.jar

# With custom port (modify code and rebuild, or use wrapper)
java -Dport=8084 -jar target/exam-seating-system-1.0.0.jar
```

### Option 2: Linux Service (Systemd)

#### Create Service File

```bash
sudo nano /etc/systemd/system/exam-seating.service
```

#### Add Content

```ini
[Unit]
Description=Exam Seating System
After=network.target mysql.service
Wants=mysql.service

[Service]
Type=simple
User=examseating
Group=examseating
WorkingDirectory=/opt/exam-seating
ExecStart=/usr/bin/java -Xmx512m -Xms256m -jar /opt/exam-seating/app.jar
Restart=always
RestartSec=10
StartLimitInterval=60s
StartLimitBurst=3

[Install]
WantedBy=multi-user.target
```

#### Setup Service

```bash
# Create user
sudo useradd -r -s /bin/false examseating

# Create application directory
sudo mkdir -p /opt/exam-seating
sudo cp target/exam-seating-system-1.0.0.jar /opt/exam-seating/app.jar
sudo chown -R examseating:examseating /opt/exam-seating

# Reload systemd
sudo systemctl daemon-reload

# Enable service
sudo systemctl enable exam-seating

# Start service
sudo systemctl start exam-seating

# Check status
sudo systemctl status exam-seating

# View logs
sudo journalctl -u exam-seating -f
```

#### Service Management

```bash
# Start
sudo systemctl start exam-seating

# Stop
sudo systemctl stop exam-seating

# Restart
sudo systemctl restart exam-seating

# Status
sudo systemctl status exam-seating

# Logs
sudo journalctl -u exam-seating --lines=50
```

### Option 3: Docker

#### Dockerfile

```dockerfile
FROM maven:3.8.1-openjdk-11 AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=builder /build/target/exam-seating-system-1.0.0.jar app.jar

EXPOSE 8083

CMD ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]
```

#### Docker Compose

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:5.7
    container_name: exam-seating-db
    environment:
      MYSQL_DATABASE: exam_seating_system
      MYSQL_ROOT_PASSWORD: root
      MYSQL_USER: examuser
      MYSQL_PASSWORD: your-secure-password
    volumes:
      - mysql_data:/var/lib/mysql
    ports:
      - "3306:3306"
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  app:
    build: .
    container_name: exam-seating-app
    environment:
      DB_HOST: mysql
      DB_USER: examuser
      DB_PASSWORD: your-secure-password
    ports:
      - "8083:8083"
    depends_on:
      mysql:
        condition: service_healthy
    restart: unless-stopped

volumes:
  mysql_data:
```

#### Build and Run

```bash
# Build image
docker build -t exam-seating:1.0.0 .

# Run with docker-compose
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop
docker-compose down
```

### Option 4: Nginx Reverse Proxy

#### Nginx Configuration

```nginx
server {
    listen 80;
    server_name exam.yourdomain.com;

    location / {
        proxy_pass http://localhost:8083;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
}
```

#### Enable Site

```bash
sudo cp exam-seating.conf /etc/nginx/sites-available/
sudo ln -s /etc/nginx/sites-available/exam-seating.conf /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

---

## 🔐 Security Hardening

### 1. MySQL Security

```bash
# Disable root remote login
mysql -u root -p << EOF
DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');
FLUSH PRIVILEGES;
EOF
```

### 2. Change Default Admin Password

Edit `src/main/java/com/collegeadmin/service/AdminService.java`:

```java
private static final String ADMIN_PASSWORD_HASH = "your-new-bcrypt-hash";
```

Generate bcrypt hash:

```bash
# Using online tool or bcrypt CLI
# Password: your-new-password
# Hash: $2a$10$slYQmyNdGzin7olVN3p9POFST9/PgBkqquzi.Ss7KIUgO2t0jKMUe
```

### 3. Enable MySQL SSL

```bash
# Generate certificates
openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout server-key.pem -out server-cert.pem

# Update MySQL connection string
# Add: ?useSSL=true&serverSslCertificateKeyStoreUrl=...
```

### 4. Firewall Rules

```bash
# Allow SSH
sudo ufw allow 22/tcp

# Allow HTTP
sudo ufw allow 80/tcp

# Allow HTTPS
sudo ufw allow 443/tcp

# Block MySQL from external
# (MySQL should only listen on localhost)

# Enable firewall
sudo ufw enable
```

---

## 📊 Monitoring

### Application Logs

```bash
# Real-time logs
tail -f logs/exam-seating.log

# Last 100 lines
tail -n 100 logs/exam-seating.log

# Search for errors
grep "ERROR" logs/exam-seating.log
```

### Health Check

```bash
# Check if application is running
curl -s http://localhost:8083/login | grep -q "Admin Login" && echo "OK" || echo "DOWN"

# Monitor memory usage
ps aux | grep ExamSeatingApp

# Check port
netstat -tlnp | grep 8083
```

### Database Monitoring

```bash
# Connect to database
mysql -u examuser -p exam_seating_system

# Check table sizes
SELECT table_name, ROUND(((data_length + index_length) / 1024 / 1024), 2) as size_mb 
FROM information_schema.tables 
WHERE table_schema = 'exam_seating_system'
ORDER BY size_mb DESC;

# Check row counts
SELECT table_name, table_rows 
FROM information_schema.tables 
WHERE table_schema = 'exam_seating_system';
```

---

## 🔄 Backup and Restore

### Backup Database

```bash
# Full backup
mysqldump -u examuser -p exam_seating_system > backup-$(date +%Y%m%d-%H%M%S).sql

# Compressed backup
mysqldump -u examuser -p exam_seating_system | gzip > backup.sql.gz
```

### Restore Database

```bash
# From SQL file
mysql -u examuser -p exam_seating_system < backup.sql

# From compressed backup
gunzip < backup.sql.gz | mysql -u examuser -p exam_seating_system
```

### Automated Backups

```bash
# Create backup script
cat > /opt/exam-seating/backup.sh << 'EOF'
#!/bin/bash
BACKUP_DIR="/opt/exam-seating/backups"
DATE=$(date +%Y%m%d-%H%M%S)
mkdir -p $BACKUP_DIR
mysqldump -u examuser -pYOUR_PASSWORD exam_seating_system | gzip > $BACKUP_DIR/backup-$DATE.sql.gz
find $BACKUP_DIR -name "backup-*.sql.gz" -mtime +7 -delete
EOF

chmod +x /opt/exam-seating/backup.sh
```

Add to crontab:

```bash
crontab -e

# Add line (daily at 2 AM)
0 2 * * * /opt/exam-seating/backup.sh
```

---

## 🚨 Troubleshooting

### Issue: Connection Refused

```
Error: java.net.ConnectException: Connection refused

Solution:
1. Check MySQL is running: sudo service mysql status
2. Verify credentials in DatabaseConfig.java
3. Check MySQL can accept connections: mysql -u examuser -p -h 127.0.0.1
```

### Issue: Out of Memory

```
Error: java.lang.OutOfMemoryError: Java heap space

Solution:
Increase JVM memory:
java -Xmx1024m -Xms512m -jar app.jar
```

### Issue: Port Already in Use

```
Error: Address already in use: bind

Solution:
# Find process using port 8083
lsof -i :8083

# Kill process
kill -9 PID

# Or change port in ExamSeatingApp.java
private static final int PORT = 8084;
```

### Issue: Permission Denied

```
Error: Permission denied

Solution:
sudo chown -R examseating:examseating /opt/exam-seating
sudo chmod -R 755 /opt/exam-seating
```

---

## 📈 Performance Tuning

### MySQL Optimization

```sql
-- Add indexes
ALTER TABLE seat_allocations ADD INDEX idx_session_student (session_id, student_id);
ALTER TABLE invigilator_duties ADD INDEX idx_session_invigilator (session_id, invigilator_id);

-- Optimize tables
OPTIMIZE TABLE students, invigilators, halls, exam_sessions, seat_allocations, invigilator_duties;
```

### JVM Optimization

```bash
# Garbage collection tuning
java -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Xmx512m -jar app.jar

# Parallel garbage collection
java -XX:+UseParallelGC -XX:ParallelGCThreads=4 -Xmx512m -jar app.jar
```

---

## ✅ Deployment Checklist

- [ ] System requirements verified
- [ ] Java 11+ installed
- [ ] Maven 3.6+ installed
- [ ] MySQL 5.7+ installed
- [ ] Database created
- [ ] Credentials configured
- [ ] Application built successfully
- [ ] Application runs locally
- [ ] Database connection working
- [ ] Logs generated correctly
- [ ] Admin password changed
- [ ] Security hardening applied
- [ ] Firewall configured
- [ ] Backup strategy in place
- [ ] Monitoring setup
- [ ] SSL/TLS configured (if needed)
- [ ] Reverse proxy configured (if needed)
- [ ] Service auto-start configured
- [ ] Documentation updated
- [ ] Team trained on operation

---

**Deployment Guide Version**: 1.0
**Last Updated**: July 2026
**Status**: Ready for Production
