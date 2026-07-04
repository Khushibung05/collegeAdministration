<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Manage Halls</title>
  <script src="../js/admin.js"></script>
</head>
<body>
  <h1>Manage Halls</h1>
  <form id="hallForm" onsubmit="return createHall();">
    Name: <input id="hall_name" required/> Capacity: <input id="hall_capacity" type="number" required/>
    <button type="submit">Add Hall</button>
  </form>
  <h2>Halls</h2>
  <table id="hallsTable" border="1"><thead><tr><th>ID</th><th>Name</th><th>Capacity</th></tr></thead><tbody></tbody></table>
  <script>loadHalls();</script>
</body>
</html>