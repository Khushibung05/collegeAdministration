# Smart Exam Seating System for Intercollege Examinations 🎓

A Python and web-based system that automates **exam seating arrangements** and **invigilator duty allocation**.  
The system ensures error-free allocations, optimized hall usage, and provides a simple web interface for admins, students, and invigilators.

---

## Features

📋 **Admin Features**  
- Secure login for administrators  
- Generate seating allocation & invigilator duties  
- View and manage session details  

🎓 **Student Features**  
- Search seat assignments by student details  

👩‍🏫 **Invigilator Features**  
- View assigned hall and duty details  

⚙️ **System Features**  
- JSON-based data management (`students.json`, `halls.json`, `invigilators.json`, etc.)  
- Automatic seat allocation and invigilator assignment using `generate_allocation.py`  
- Organized front-end templates for easy navigation  

## Project Structure

Smart-Exam-Seating-System/
├── data/
│ ├── allocations.json # Stores seat allocation results
│ ├── duties.json # Stores invigilator duty assignments
│ ├── halls.json # Hall/room configurations
│ ├── invigilators.json # Invigilator details
│ ├── session.json # Exam session info
│ ├── students.json # Student data
│ └── static/
│ └── styles.css # Styling for frontend
├── templates/
│ ├── admin_login.html # Admin login page
│ ├── dashboard.html # Admin dashboard
│ ├── index.html # Role selection (landing page)
│ ├── invigilator.html # Invigilator duty view
│ └── student.html # Student seat lookup
├── generate_allocation.py # Core seat + duty allocation logic
├── main.py # Flask app entry point
├── pyvenv.cfg # Virtual environment config
└── README.md # Documentation


---

## Technology Stack
- **Frontend:** HTML, CSS (templates + static)  
- **Backend:** Python (Flask for web routing, allocation logic)  
- **Data Storage:** JSON (students, halls, invigilators, allocations, duties, sessions)  

---

## Installation

### Prerequisites
- Python 3.8+  
- pip (Python package manager)  
- Virtual environment (recommended)  

---

### Quick Setup
📥 Clone the repository  
```bash
git clone https://github.com/Khushibung05/collegeAdministration
cd Smart-Exam-Seating-System-for-Intercollege-Examinations


📦 Install dependencies

pip install -r requirements.txt


▶️ Run the application

python main.py


Open your browser and go to:
👉 http://localhost:5000

Usage Flow

Admin Login → Enter credentials and access dashboard

Generate Allocation → Run algorithms to assign student seating & invigilator duties

View Dashboard → See hall utilization and duty schedules

Student Lookup → Students can view their assigned hall/seat

Invigilator View → Invigilators check assigned duties

### Future Enhancements

🚀 Planned improvements:

📩 Email/SMS notifications for students & invigilators

🗄️ Replace JSON with a relational database (MySQL/SQLite)

📱 Mobile-friendly responsive design

###Contributors

👩‍💻 Khushi Bung
👩‍💻 Nikhitha Sircilla
👩‍💻 Karnati Sharvani

Guided by Mrs. B. Sabitha (Assistant Professor, CVR College of Engineering)

⭐ If you found this project useful, please give it a star on GitHub!

