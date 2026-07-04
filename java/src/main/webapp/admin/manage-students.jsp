<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Manage Students</title>
  <script src="../js/admin.js"></script>
</head>
<body>
  <h1>Manage Students</h1>
  <form id="studentForm" onsubmit="return createStudent();">
    Roll No: <input name="roll_no" id="roll_no" required/>
    Name: <input name="name" id="name" required/>
    Dept: <input name="department" id="department"/>
    <button type="submit">Add</button>
  </form>
  <h2>Students</h2>
  <table id="studentsTable" border="1"><thead><tr><th>ID</th><th>Roll</th><th>Name</th><th>Dept</th></tr></thead><tbody></tbody></table>
  <script>loadStudents();</script>
</body>
</html>