async function api(path, opts) {
  const r = await fetch(path, opts);
  if (!r.ok) {
    const txt = await r.text();
    throw new Error(txt || r.statusText);
  }
  return r.json();
}

/* Students */
async function loadStudents() {
  try {
    const data = await api('/api/students');
    const tb = document.querySelector('#studentsTable tbody');
    tb.innerHTML = '';
    data.forEach(s => {
      const tr = document.createElement('tr');
      tr.dataset.id = s.id;
      tr.innerHTML = `<td>${s.id}</td><td>${s.roll_no}</td><td>${s.name}</td><td>${s.department}</td>`;
      tb.appendChild(tr);
    });
  } catch (e){ console.error(e); }
}
async function createStudent() {
  const form = document.getElementById('studentForm');
  const fd = new FormData(form);
  await fetch('/api/students', { method: 'POST', body: fd });
  form.reset();
  await loadStudents();
  return false;
}

/* Invigilators */
async function loadInvigilators() {
  try {
    const data = await api('/api/invigilators');
    const tb = document.querySelector('#invTable tbody');
    tb.innerHTML = '';
    data.forEach(i => {
      const tr = document.createElement('tr');
      tr.dataset.id = i.id;
      tr.innerHTML = `<td>${i.id}</td><td>${i.emp_id}</td><td>${i.name}</td>`;
      tb.appendChild(tr);
    });
  } catch(e){ console.error(e); }
}
async function createInvigilator() {
  const emp = document.getElementById('emp_id').value;
  const name = document.getElementById('inv_name').value;
  const fd = new FormData();
  fd.append('emp_id', emp);
  fd.append('name', name);
  await fetch('/api/invigilators', { method: 'POST', body: fd });
  document.getElementById('emp_id').value = '';
  document.getElementById('inv_name').value = '';
  await loadInvigilators();
  return false;
}

/* Halls */
async function loadHalls() {
  try {
    const data = await api('/api/halls');
    const tb = document.querySelector('#hallsTable tbody');
    tb.innerHTML = '';
    data.forEach(h => {
      const tr = document.createElement('tr');
      tr.dataset.id = h.id;
      tr.innerHTML = `<td>${h.id}</td><td>${h.name}</td><td>${h.capacity}</td>`;
      tb.appendChild(tr);
    });
  } catch(e){ console.error(e); }
}
async function createHall() {
  const name = document.getElementById('hall_name').value;
  const cap = document.getElementById('hall_capacity').value;
  const fd = new FormData();
  fd.append('name', name); fd.append('capacity', cap);
  await fetch('/api/halls', { method: 'POST', body: fd });
  document.getElementById('hall_name').value = '';
  document.getElementById('hall_capacity').value = '';
  await loadHalls();
  return false;
}

/* Sessions */
async function loadSessions() {
  try {
    const data = await api('/api/sessions');
    const tb = document.querySelector('#sessionsTable tbody');
    if (!tb) return data;
    tb.innerHTML = '';
    data.forEach(s => {
      const tr = document.createElement('tr');
      tr.dataset.id = s.id;
      tr.innerHTML = `<td>${s.id}</td><td>${s.code}</td><td>${s.subject}</td><td>${s.date}</td><td>${s.start_time||''}</td><td>${s.end_time||''}</td>`;
      tb.appendChild(tr);
    });
    return data;
  } catch(e){ console.error(e); }
}
async function createSession() {
  const fd = new FormData();
  fd.append('code', document.getElementById('code').value);
  fd.append('subject', document.getElementById('subject').value);
  fd.append('date', document.getElementById('date').value);
  fd.append('start_time', document.getElementById('start_time').value);
  fd.append('end_time', document.getElementById('end_time').value);
  await fetch('/api/sessions', { method: 'POST', body: fd });
  document.getElementById('sessionForm').reset();
  await loadSessions();
  return false;
}

/* Allocations */
async function generateAllocations() {
  const sel = document.getElementById('allocSession');
  const sid = sel.value;
  const r = await fetch('/api/allocations?sessionId=' + sid, { method: 'POST' });
  const txt = await r.text();
  document.getElementById('allocResult').innerText = txt;
}

/* Reports */
async function loadReport() {
  const sid = document.getElementById('reportSession').value;
  const data = await api('/api/reports?sessionId=' + sid);
  const tb = document.querySelector('#reportTable tbody');
  tb.innerHTML = '';
  data.forEach(row => {
    const tr = document.createElement('tr');
    tr.innerHTML = `<td>${row.seat_no}</td><td>${row.roll_no}</td><td>${row.student_name}</td><td>${row.hall_name}</td>`;
    tb.appendChild(tr);
  });
}