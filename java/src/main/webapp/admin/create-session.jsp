<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Create Session</title>
  <script src="../js/admin.js"></script>
</head>
<body>
  <h1>Create Exam Session</h1>
  <form id="sessionForm" onsubmit="return createSession();">
    Code: <input id="code" required/> Subject: <input id="subject" required/>
    Date: <input id="date" type="date" required/> Start: <input id="start_time" type="time"/> End: <input id="end_time" type="time"/>
    <button type="submit">Create</button>
  </form>
  <h2>Existing Sessions</h2>
  <table id="sessionsTable" border="1"><thead><tr><th>ID</th><th>Code</th><th>Subject</th><th>Date</th><th>Start</th><th>End</th></tr></thead><tbody></tbody></table>
  <script>loadSessions();</script>
</body>
</html>