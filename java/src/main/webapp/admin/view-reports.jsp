<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>View Reports</title>
  <script src="../js/admin.js"></script>
</head>
<body>
  <h1>View Allocation Report</h1>
  <label for="reportSession">Session</label>
  <select id="reportSession"></select>
  <button onclick="loadReport()">Load</button>
  <h2>Allocations</h2>
  <table id="reportTable" border="1"><thead><tr><th>Seat</th><th>Roll</th><th>Student</th><th>Hall</th></tr></thead><tbody></tbody></table>

  <script>
    async function fillReportSessions() {
      await loadSessions();
      const sel = document.getElementById('reportSession');
      const rows = document.querySelectorAll('#sessionsTable tbody tr');
      sel.innerHTML = '';
      rows.forEach(r => sel.innerHTML += `<option value="${r.dataset.id}">${r.cells[1].innerText} - ${r.cells[2].innerText}</option>`);
    }
    fillReportSessions();
  </script>
</body>
</html>