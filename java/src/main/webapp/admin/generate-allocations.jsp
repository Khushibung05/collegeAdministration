<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Generate Allocations</title>
  <script src="../js/admin.js"></script>
</head>
<body>
  <h1>Generate Allocations</h1>
  <label for="allocSession">Select Session</label>
  <select id="allocSession"></select>
  <button onclick="generateAllocations()">Generate</button>
  <div id="allocResult"></div>
  <script>
    async function loadForAlloc() {
      await loadSessions();
      const sel = document.getElementById('allocSession');
      const rows = document.querySelectorAll('#sessionsTable tbody tr');
      sel.innerHTML = '';
      rows.forEach(r => {
        sel.innerHTML += `<option value="${r.dataset.id}">${r.cells[1].innerText} - ${r.cells[2].innerText}</option>`;
      });
    }
    loadForAlloc();
  </script>
</body>
</html>