package com.collegeadmin.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class AdminUIHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HttpUtils.sendPlain(exchange, 405, "Method Not Allowed");
            return;
        }

        String html = """
                <!doctype html>
                <html>
                <head>
                  <meta charset="utf-8"/>
                  <title>Admin - Exam Seating System</title>
                  <style>
                    body{font-family:Arial,Helvetica,sans-serif;margin:20px}
                    nav a{margin-right:12px}
                    section{margin-top:20px}
                    table{border-collapse:collapse;width:100%}
                    td,th{padding:8px;border:1px solid #ddd}
                    .hidden{display:none}
                    .btn{padding:6px 10px;background:#3498db;color:#fff;border-radius:4px;text-decoration:none}
                  </style>
                </head>
                <body>
                  <h1>Admin Console</h1>
                  <nav>
<a href="#" onclick="show('students');return false;">Manage Students</a>

<a href="#" onclick="show('invigilators');return false;">Manage Invigilators</a>

<a href="#" onclick="show('halls');return false;">Manage Halls</a>

<a href="#" onclick="show('sessions');return false;">Create Session</a>

<a href="#" onclick="show('alloc');return false;">Generate Allocations</a>

<a href="#" onclick="show('reports');return false;">View Reports</a>
</nav>v

                  <section id="students" class="">
                    <h2>Manage Students</h2>
                    <form onsubmit="return addStudent()">
                      Roll #: <input id="s_roll" required/> Name: <input id="s_name" required/> Email: <input id="s_email"/>
                      <button class="btn" type="submit">Add</button>
                    </form>
                    <table id="studentsTable"><thead><tr><th>ID</th><th>Roll</th><th>Name</th><th>Email</th></tr></thead><tbody></tbody></table>
                  </section>

                  <section id="invigilators" class="hidden">
                    <h2>Manage Invigilators</h2>
                    <form onsubmit="return addInvigilator()">
                      Name: <input id="i_name" required/> Email: <input id="i_email" required/> Dept: <input id="i_dept"/>
                      <button class="btn" type="submit">Add</button>
                    </form>
                    <table id="invTable"><thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Dept</th></tr></thead><tbody></tbody></table>
                  </section>

                  <section id="halls" class="hidden">
                    <h2>Manage Halls</h2>
                    <form onsubmit="return addHall()">
                      Name: <input id="h_name" required/> Rows: <input id="h_rows" type="number" min="1" value="5" required/> Columns: <input id="h_cols" type="number" min="1" value="6" required/>
                      <button class="btn" type="submit">Add</button>
                    </form>
                    <table id="hallsTable"><thead><tr><th>ID</th><th>Name</th><th>Rows</th><th>Cols</th><th>Capacity</th></tr></thead><tbody></tbody></table>
                  </section>

                  <section id="sessions" class="hidden">
                    <h2>Create Session</h2>
                    <form onsubmit="return addSession()">
                      Name: <input id="sess_name" required/> Date: <input id="sess_date" type="date" required/> Time: <input id="sess_time" type="time"/> Duration (mins): <input id="sess_dur" type="number" value="60"/>
                      <button class="btn" type="submit">Create</button>
                    </form>
                    <table id="sessTable"><thead><tr><th>ID</th><th>Name</th><th>Date</th><th>Time</th><th>Duration</th></tr></thead><tbody></tbody></table>
                  </section>

                  <section id="alloc" class="hidden">
                    <h2>Generate Allocations</h2>
                    <select id="allocSession"></select>
                    <button class="btn" onclick="generateAllocations()">Generate</button>
                    <div id="allocResult"></div>
                  </section>

                  <section id="reports" class="hidden">
                    <h2>View Reports</h2>
                    <select id="reportSession"></select>
                    <button class="btn" onclick="loadReport()">Load</button>
                    <table id="reportTable"><thead><tr><th>SeatID</th><th>Row</th><th>Col</th><th>Roll</th><th>Student</th><th>Hall</th></tr></thead><tbody></tbody></table>
                  </section>

                  <script>
                    function show(id){
                      ['students','invigilators','halls','sessions','alloc','reports'].forEach(x=>{
                        document.getElementById(x).classList.toggle('hidden', x!==id);
                      });
                      if(id==='students') loadStudents();
                      if(id==='invigilators') loadInvigilators();
                      if(id==='halls') loadHalls();
                      if(id==='sessions') loadSessions();
                      if(id==='alloc') loadAllocSessions();
                      if(id==='reports') loadReportSessions();
                      // update URL hash without adding history entry
                      if(location.hash !== '#' + id) history.replaceState(null, '', '/admin#' + id);
                    }

                    // choose initial section based on hash, fallback to 'students'
                    function initialShow(){
                      const hash = (location.hash || '').replace('#','');
                      const valid = ['students','invigilators','halls','sessions','alloc','reports'];
                      if(valid.includes(hash)) show(hash);
                      else show('students');
                    }

                    // (API helper + handler functions are the same as before)
                    async function api(path, opts){
                      const r = await fetch(path, opts);
                      if (!r.ok) throw new Error(await r.text());
                      return r.json();
                    }

                    /* Students */
                    async function loadStudents(){
                      try {
                        const data = await api('/api/students');
                        const tb = document.querySelector('#studentsTable tbody'); tb.innerHTML='';
                        data.forEach(s=> tb.innerHTML += `<tr><td>${s.id}</td><td>${s.roll_number}</td><td>${s.name}</td><td>${s.email||''}</td></tr>`);
                      } catch(e){ console.error(e); }
                    }
                    async function addStudent(){
                      const fd = new URLSearchParams();
                      fd.append('roll_number', document.getElementById('s_roll').value);
                      fd.append('name', document.getElementById('s_name').value);
                      fd.append('email', document.getElementById('s_email').value);
                      await fetch('/api/students',{method:'POST',body:fd});
                      document.getElementById('s_roll').value='';document.getElementById('s_name').value='';document.getElementById('s_email').value='';
                      await loadStudents(); return false;
                    }

                    /* Invigilators */
                    async function loadInvigilators(){
                      try {
                        const data = await api('/api/invigilators');
                        const tb = document.querySelector('#invTable tbody'); tb.innerHTML='';
                        data.forEach(i=> tb.innerHTML += `<tr><td>${i.id}</td><td>${i.name}</td><td>${i.email}</td><td>${i.department||''}</td></tr>`);
                      } catch(e){ console.error(e); }
                    }
                    async function addInvigilator(){
                      const fd = new URLSearchParams();
                      fd.append('name', document.getElementById('i_name').value);
                      fd.append('email', document.getElementById('i_email').value);
                      fd.append('department', document.getElementById('i_dept').value);
                      await fetch('/api/invigilators',{method:'POST',body:fd});
                      document.getElementById('i_name').value='';document.getElementById('i_email').value='';document.getElementById('i_dept').value='';
                      await loadInvigilators(); return false;
                    }

                    /* Halls */
                    async function loadHalls(){
                      try {
                        const data = await api('/api/halls');
                        const tb = document.querySelector('#hallsTable tbody'); tb.innerHTML='';
                        data.forEach(h=> tb.innerHTML += `<tr><td>${h.id}</td><td>${h.name}</td><td>${h.row_count}</td><td>${h.column_count}</td><td>${h.total_capacity}</td></tr>`);
                      } catch(e){ console.error(e); }
                    }
                    async function addHall(){
                      const fd = new URLSearchParams();
                      fd.append('name', document.getElementById('h_name').value);
                      fd.append('row_count', document.getElementById('h_rows').value);
                      fd.append('column_count', document.getElementById('h_cols').value);
                      await fetch('/api/halls',{method:'POST',body:fd});
                      document.getElementById('h_name').value=''; await loadHalls(); return false;
                    }

                    /* Sessions */
                    async function loadSessions(){
                      try {
                        const data = await api('/api/sessions');
                        const tb = document.querySelector('#sessTable tbody'); tb.innerHTML='';
                        data.forEach(s=> tb.innerHTML += `<tr><td>${s.id}</td><td>${s.session_name}</td><td>${s.exam_date||''}</td><td>${s.exam_time||''}</td><td>${s.duration_minutes||''}</td></tr>`);
                        return data;
                      } catch(e){ console.error(e); return []; }
                    }
                    async function addSession(){
                      const fd = new URLSearchParams();
                      fd.append('session_name', document.getElementById('sess_name').value);
                      fd.append('exam_date', document.getElementById('sess_date').value);
                      fd.append('exam_time', document.getElementById('sess_time').value);
                      fd.append('duration_minutes', document.getElementById('sess_dur').value);
                      await fetch('/api/sessions',{method:'POST',body:fd});
                      document.getElementById('sess_name').value=''; await loadSessions(); return false;
                    }

                    async function loadAllocSessions(){
                      const sessions = await loadSessions();
                      const sel = document.getElementById('allocSession'); sel.innerHTML='';
                      sessions.forEach(s=> sel.innerHTML += `<option value="${s.id}">${s.session_name} (${s.exam_date||''})</option>`);
                    }
                    async function generateAllocations(){
                      const sid = document.getElementById('allocSession').value;
                      const r = await fetch('/api/allocations?sessionId='+sid, {method:'POST'});
                      const txt = await r.text();
                      document.getElementById('allocResult').innerText = txt;
                    }

                    async function loadReportSessions(){
                      const sessions = await loadSessions();
                      const sel = document.getElementById('reportSession'); sel.innerHTML='';
                      sessions.forEach(s=> sel.innerHTML += `<option value="${s.id}">${s.session_name} (${s.exam_date||''})</option>`);
                    }
                    async function loadReport(){
                      try {
                        const sid = document.getElementById('reportSession').value;
                        const data = await api('/api/reports?sessionId='+sid);
                        const tb = document.querySelector('#reportTable tbody'); tb.innerHTML='';
                        data.forEach(r=> tb.innerHTML += `<tr><td>${r.id}</td><td>${r.seat_row}</td><td>${r.seat_column}</td><td>${r.roll_number}</td><td>${r.student_name}</td><td>${r.hall_name}</td></tr>`);
                      } catch(e){ console.error(e); }
                    }

                    // Initialize view using hash (so /admin#students works)
                    window.addEventListener('load', initialShow);
                    // If user changes hash manually or navigates, switch section
                    window.addEventListener('hashchange', () => {
                      const h = (location.hash||'').replace('#','');
                      if(h) show(h);
                    });
                  </script>
                </body>
                </html>
                """;

        HttpUtils.sendHtml(exchange, 200, html);
    }
}