<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Manage Invigilators</title>
  <script src="../js/admin.js"></script>
</head>
<body>
  <h1>Manage Invigilators</h1>
  <form id="invForm" onsubmit="return createInvigilator();">
    Emp ID: <input id="emp_id" required/> Name: <input id="inv_name" required/>
    <button type="submit">Add</button>
  </form>
  <h2>Invigilators</h2>
  <table id="invTable" border="1"><thead><tr><th>ID</th><th>Emp ID</th><th>Name</th></tr></thead><tbody></tbody></table>
  <script>loadInvigilators();</script>
</body>
</html>