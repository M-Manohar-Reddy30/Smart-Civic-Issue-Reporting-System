# Smart Civic Issue Reporting System (CivicReport Pro)

A world-class, production-grade full-stack web application designed to bridge the gap between citizens and city officials. This platform ensures transparency and accountability through a mandatory "Proof-of-Work" system and encourages civic engagement through gamification.

## 🚀 Key Features

### 🏛️ Dual Dashboard System
- **Citizen Portal**: Report issues (potholes, garbage, lighting) with image attachments. Track resolution progress in real-time.
- **Admin Dashboard**: Centralized hub for city officials to monitor, prioritize, and manage community concerns.

### 🛡️ Verified Accountability (Proof-of-Work)
- **Mandatory Documentation**: Admins must upload "Work Started" and "Work Resolved" photos to transition an issue through its lifecycle.
- **Strict Workflow**: Enforced status transitions (Pending → In Progress → Resolved) to prevent data manipulation.

### 🏆 Gamification (Citizen Score)
- **Point System**: Earn +10 points for every valid report and +20 points when your reported issue is successfully resolved.
- **Impact Tracker**: Users can see their total contribution to the community via a dedicated "Citizen Impact" scorecard.

### 📊 Real-Time Analytics
- **Admin Insights**: Instant overview of total reports, pending tasks, and successful resolutions via a clean analytics dashboard.
- **Public Feed**: A searchable community feed showing real-time civic activity filtered by location.

---

## 🛠️ Technology Stack

### Backend (Java)
- **Framework**: Spring Boot 3.2.5
- **Data Access**: **JDBC (JdbcTemplate)** - Strictly no ORM/Hibernate as per project requirements.
- **Database**: MySQL 8.0+
- **Build Tool**: Maven

### Frontend (React)
- **Framework**: React 18 (Vite)
- **Styling**: Vanilla CSS (Custom Design System)
- **Icons**: Lucide-React
- **API Client**: Axios

---

## 📁 Project Structure

```text
/backend
  /src/main/java/com/civic
    /config      <- CORS & Static Resource configurations
    /controller  <- REST Endpoints
    /model       <- Data Transfer Objects (Issue, Stats)
    /repository  <- JDBC Database logic
    /service     <- Business & Workflow logic
  /uploads       <- Locally stored proof images
  pom.xml

/frontend
  /src
    /components  <- Reusable UI elements
    /pages       <- Main Dashboard views
    /services    <- Axios API integration
    /styles      <- Global Design System
  vite.config.js
```

---

## 💻 Setup & Execution Guide

### 1. Database Setup
1. Ensure MySQL is running on your machine.
2. Create the database:
   ```sql
   CREATE DATABASE civic_app;
   ```
3. Run the provided `schema.sql` to create tables and seed the admin user.

### 2. Backend Execution
1. Navigate to the `/backend` folder.
2. Open `src/main/resources/application.properties` and update your MySQL credentials (`username` and `password`).
3. Run the following command:
   ```bash
   mvn spring-boot:run
   ```
4. Confirm the server is running at `http://localhost:8080`.

### 3. Frontend Execution
1. Navigate to the `/frontend` folder.
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```
4. Access the application at `http://localhost:5173`.

---

## 🔌 API Documentation

### **Issue Management**
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/issues` | Fetch all reported issues |
| `GET` | `/api/issues/stats` | Get analytics and citizen score |
| `POST` | `/api/issues` | Report a new issue (Multipart/Form-Data) |
| `PUT` | `/api/issues/{id}/status` | Update status with mandatory proof |
| `GET` | `/api/issues/search` | Search issues by location |

#### **Example: Status Update**
- **Request**: `PUT /api/issues/1/status?status=In Progress`
- **Body**: `proof: [File Attachment]`
- **Validation**: Rejects if "proof" is missing or if the status jump is invalid (e.g. Pending to Resolved).

---

## 📸 Screenshots & Workflow Demo

1. **Citizen View**: Fill the form → Upload photo → See your "Citizen Score" increase.
2. **Admin View**: Open Dashboard → Click "Start Work" → Upload Photo → Status changes to "In Progress".
3. **Resolution**: Click "Resolve" → Upload "After" Photo → Citizen earns bonus points.

---

## 🔮 Future Improvements
- **Google Maps Integration**: Pinpoint exact issue coordinates.
- **Push Notifications**: Notify citizens instantly when their issue status changes.
- **Admin Approval System**: Add a "Review" stage before rewarding points.

---
Built with ❤️ for a better society.
