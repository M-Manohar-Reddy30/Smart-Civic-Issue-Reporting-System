# Smart Civic Issue Reporting System

A production-level Java desktop application for civic issue reporting and management, built with Java Swing, JDBC, and MySQL. The system provides separate dashboards for citizens and administrators to report, track, and resolve public issues efficiently.

## 📋 Table of Contents
- [Project Overview](#project-overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Database Setup](#database-setup)
- [JDBC Setup](#jdbc-setup)
- [How to Run](#how-to-run)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [Screenshots](#screenshots)
- [Contributing](#contributing)

## 🎯 Project Overview

The Smart Civic Issue Reporting System is designed to bridge the gap between citizens and local authorities by providing a streamlined platform for reporting civic issues. Citizens can easily report problems like potholes, garbage, streetlight failures, and other public infrastructure issues, while administrators can efficiently manage and resolve these issues.

The application follows a clean layered architecture with separate user roles, modern dark-themed UI, and robust database operations using JDBC PreparedStatements.

## ✨ Features

### 👤 Citizen (User) Dashboard
- **Issue Reporting**: Report civic issues with location, category, description, and image upload
- **Dashboard Analytics**: View statistics for total, pending, in-progress, and resolved issues
- **Issue Tracking**: Filter and view issues by location with real-time status updates
- **High Priority Detection**: Automatic highlighting of issues pending for more than 3 days
- **Image Preview**: View uploaded images in popup dialogs

### 🛠️ Administrator Dashboard
- **Secure Login**: Fixed credentials authentication (manoharreddyind@gmail.com / Nani@8888)
- **Comprehensive Issue Management**: View all issues across all locations in a detailed table
- **Advanced Filtering**: Filter issues by location, status, and category
- **Status Management**: Update issue status (Pending → In Progress → Resolved)
- **Proof of Work**: Upload images showing completed work after issue resolution
- **Analytics Dashboard**: View system-wide statistics and high-priority issue counts

### 🔧 Technical Features
- **Clean Architecture**: Model-Service-Database layered design
- **JDBC Best Practices**: PreparedStatement usage for all database operations
- **Exception Handling**: Comprehensive error handling and user feedback
- **Image Management**: Local file storage with path management
- **Modern UI**: Dark-themed professional interface with sidebar navigation
- **Responsive Design**: Cards, tables, and forms with hover effects

## 🛠️ Tech Stack

- **Language**: Java 8+
- **UI Framework**: Java Swing
- **Database**: MySQL 5.7+
- **JDBC Driver**: MySQL Connector/J
- **Architecture**: Layered Architecture (Model-Service-Database)
- **Build Tool**: Manual compilation (javac)
- **IDE**: VS Code recommended

## 🗄️ Database Setup

### Prerequisites
- MySQL Server installed and running
- MySQL user with database creation privileges

### Setup Steps

1. **Start MySQL Server**
   ```bash
   # On Windows
   net start mysql

   # On Linux/Mac
   sudo systemctl start mysql
   ```

2. **Create Database and Tables**
   ```sql
   -- Run the schema.sql file
   source schema.sql;
   ```

3. **Verify Setup**
   ```sql
   USE civic_app;
   SHOW TABLES;
   SELECT * FROM issues;
   SELECT * FROM users;
   ```

### Database Schema

#### `issues` Table
```sql
CREATE TABLE issues (
  id INT AUTO_INCREMENT PRIMARY KEY,
  category VARCHAR(100) NOT NULL,
  description TEXT NOT NULL,
  location VARCHAR(255) NOT NULL,
  status VARCHAR(50) NOT NULL DEFAULT 'Pending',
  image_path VARCHAR(500),
  proof_image_path VARCHAR(500),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### `users` Table
```sql
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL DEFAULT 'admin'
);
```

## 🔌 JDBC Setup

### Download MySQL Connector/J

1. Visit [MySQL Connector/J Download Page](https://dev.mysql.com/downloads/connector/j/)
2. Download the latest version (Platform Independent)
3. Extract the JAR file (`mysql-connector-java-x.x.xx.jar`)

### Add to Project

1. Create a `lib` folder in your project root
2. Copy the downloaded JAR file to `lib/`
3. Update your classpath when compiling/running

### Database Configuration

Update the connection details in `src/main/java/app/db/DBConnection.java`:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/civic_app?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
private static final String DB_USER = "your_mysql_username";
private static final String DB_PASSWORD = "your_mysql_password";
```

## 🚀 How to Run

### Prerequisites
- Java 8 or higher installed
- MySQL Server running
- MySQL Connector/J JAR file

### Quick Start (Windows)

1. **Download MySQL Connector/J**
   - Visit [MySQL Connector/J Download Page](https://dev.mysql.com/downloads/connector/j/)
   - Download the latest version (Platform Independent)
   - Extract and place the JAR file in the `lib/` directory

2. **Setup Database**
   ```bash
   # Login to MySQL
   mysql -u root -p

   # Run schema
   source schema.sql;
   ```

3. **Run the Application**
   ```bash
   # Double-click run.bat or run from command line
   run.bat
   ```

### Manual Setup (All Platforms)

1. **Clone/Download the Project**
   ```bash
   cd "c:\projects\Smart Civic Issue Reporting System"
   ```

2. **Setup Database**
   ```bash
   # Login to MySQL
   mysql -u root -p

   # Run schema
   source schema.sql;
   ```

3. **Compile the Application**
   ```bash
   # Compile with JDBC driver in classpath
   javac -cp ".;lib/mysql-connector-java-x.x.xx.jar" -d bin src/main/java/app/*.java src/main/java/app/*/*.java
   ```

4. **Run the Application**
   ```bash
   # Run with JDBC driver in classpath
   java -cp "bin;lib/mysql-connector-java-x.x.xx.jar" app.Main
   ```

### Alternative: Using VS Code

1. Open the project in VS Code
2. Ensure Java extensions are installed
3. Add MySQL Connector/J to the project classpath
4. Run `Main.java`

## 📁 Project Structure

```
Smart Civic Issue Reporting System/
├── README.md
├── schema.sql
├── images/                          # Local image storage
│   └── (uploaded images)
├── lib/                             # External JARs
│   └── mysql-connector-java-x.x.xx.jar
├── src/main/java/app/
│   ├── Main.java                    # Application entry point
│   ├── model/
│   │   └── Issue.java               # Issue entity model
│   ├── service/
│   │   └── IssueService.java        # Business logic & JDBC operations
│   ├── db/
│   │   └── DBConnection.java        # JDBC connection manager
│   └── ui/
│       ├── UserDashboard.java       # Citizen interface
│       ├── AdminLogin.java          # Admin authentication
│       └── AdminDashboard.java      # Admin management interface
└── bin/                             # Compiled classes (generated)
```

## 🏗️ Architecture

### Layered Architecture

```
┌─────────────────┐
│   UI Layer      │  UserDashboard, AdminDashboard
│   (Java Swing)  │
├─────────────────┤
│ Service Layer   │  IssueService
│ (Business Logic)│
├─────────────────┤
│ Database Layer  │  DBConnection
│   (JDBC)        │
├─────────────────┤
│   MySQL DB      │  issues, users tables
└─────────────────┘
```

### Key Design Patterns

- **DAO Pattern**: Data Access Object for database operations
- **Service Pattern**: Business logic separation
- **MVC Pattern**: Model-View separation
- **Singleton Pattern**: Database connection management

### JDBC Workflow

1. **Connection**: DBConnection.getConnection() establishes connection
2. **PreparedStatement**: All queries use PreparedStatement for security
3. **ResultSet**: Data retrieval and mapping to Issue objects
4. **Transaction Management**: Proper connection handling and cleanup

## 📸 Screenshots

### Citizen Dashboard
- Modern dark-themed interface
- Sidebar navigation (Dashboard, Report Issue, View Issues, Admin Panel)
- Statistics cards showing issue counts
- Issue reporting form with image upload
- Filtered issue table with status indicators

### Admin Dashboard
- Secure login dialog
- Comprehensive issue management table
- Advanced filtering options
- Status update functionality
- Proof image upload capability

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Built for academic and interview demonstration
- Implements production-level Java practices
- Showcases modern desktop application development
- Demonstrates full-stack Java application architecture

---

**Note**: This is a complete, working desktop application ready for production use. The codebase demonstrates professional Java development practices, proper database design, and modern UI/UX principles.
private static final String DB_URL = "jdbc:mysql://localhost:3306/civic_app?useSSL=false&serverTimezone=UTC";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "password";
```

## Compile and Run

From the project root folder:

```powershell
cd "c:\projects\Smart Civic Issue Reporting System"
javac -d out -sourcepath src\main\java src\main\java\app\Main.java src\main\java\app\db\DBConnection.java src\main\java\app\model\Issue.java src\main\java\app\service\IssueService.java src\main\java\app\ui\UI.java
java -cp out;path\to\mysql-connector-java-8.0.x.jar app.Main
```

Replace `path\to\mysql-connector-java-8.0.x.jar` with your actual Connector/J path.

## IDE Setup

1. Open the project in your IDE.
2. Add the MySQL Connector/J JAR to the classpath.
3. Run `app.Main`.

## Notes

- Uploaded images are copied into the local `images/` folder.
- The UI uses dark mode styling and responsive card layout.
- Database access uses `PreparedStatement` and reusable JDBC connections.
