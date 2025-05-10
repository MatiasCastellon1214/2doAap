# ToDoApp - Full Stack Version

This project is a full-stack version of the original [ToDoApp frontend project](https://github.com/MatiasCastellon1214/toDoAap.git), which was initially developed as a final assignment for the "Frontend II" course in the Certified Tech Developer program.

In this version, the frontend was improved and a complete backend was implemented to provide a fully functional, persistent task management system.

---

## 🚀 Technologies Used

### Frontend
- HTML
- CSS
- JavaScript (Vanilla)

### Backend
- Java 17
- Spring Boot
- Spring Security (JWT Authentication)
- Spring Data JPA
- MySQL

---

## ⚙️ Features

- User registration and login with JWT-based authentication
- Create, update, delete and list tasks
- Tasks are user-specific (each user sees only their tasks)
- Responsive user interface
- Environment-based configuration (using `.env` variables)

---

## 📦 Project Structure

toDoApp/
├── backend/ → Spring Boot API
│ ├── src/
│ └── ...
├── frontend/ → Vanilla JS App (HTML, CSS, JS)
│ ├── index.html
│ └── ...
└── .env.example → Example of required environment variables



---

## 🛠️ Setup Instructions

### 1. Clone the repository

```bash
git clone https://github.com/your-username/your-todoapp-repo.git
cd your-todoapp-repo
```
---
### 2. Backend Configuration

- Create a .env file in the root directory and fill it based on .env.example.

```ini
APP_NAME=toDoApp
SERVER_PORT=8081

DB_URL=jdbc:mysql://localhost:3306/todo_app
DB_USERNAME=root
DB_PASSWORD=admin

JWT_SECRET=your_secret_key
JWT_EXPIRATION_MS=86400000

```

- Start your MySQL server and create a database called todo_app.

- Run the Spring Boot application.

---
### 3. Frontend
- Simply open frontend/index.html in your browser.
- Ensure it’s pointing to the correct backend base URL (e.g., http://localhost:8081).


---
📌 Notes
- This repository merges both frontend and backend into one full-stack solution.

- Authentication is handled with JWT, and each user's tasks are securely managed.

- Ideal for educational purposes or as a starting point for more complex full-stack apps.
