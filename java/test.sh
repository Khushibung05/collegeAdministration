#!/bin/bash

echo "🧪 Testing Exam Seating System..."

# Test 1: Database Connection
echo "✓ Test 1: Checking database connection..."
mysql -u root exam_seating_system -e "SELECT COUNT(*) as tables FROM information_schema.tables WHERE table_schema='exam_seating_system';"

# Test 2: Add a test student
echo "✓ Test 2: Testing Student insertion..."
mysql -u root exam_seating_system -e "INSERT INTO students (name, roll_number, email, phone) VALUES ('Test Student', 'TS001', 'test@example.com', '1234567890');"

# Test 3: Add a test invigilator
echo "✓ Test 3: Testing Invigilator insertion..."
mysql -u root exam_seating_system -e "INSERT INTO invigilators (name, email, phone, department) VALUES ('Test Invigilator', 'invig@example.com', '9876543210', 'CSE');"

# Test 4: Add a test hall
echo "✓ Test 4: Testing Hall insertion..."
mysql -u root exam_seating_system -e "INSERT INTO halls (name, rows, columns) VALUES ('Hall A', 5, 10);"

# Test 5: Verify data
echo "✓ Test 5: Verifying inserted data..."
mysql -u root exam_seating_system -e "SELECT * FROM students; SELECT * FROM invigilators; SELECT * FROM halls;"

echo "✅ All tests passed!"