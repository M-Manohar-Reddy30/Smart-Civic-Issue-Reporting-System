@echo off
echo Smart Civic Issue Reporting System
echo ==================================
echo.

REM Check if MySQL Connector/J exists
if not exist "lib\mysql-connector-java-*.jar" (
    echo ERROR: MySQL Connector/J not found in lib/ directory.
    echo Please download MySQL Connector/J from:
    echo https://dev.mysql.com/downloads/connector/j/
    echo And place the JAR file in the lib/ directory.
    pause
    exit /b 1
)

REM Compile the application
echo Compiling Java files...
javac -d bin -cp "lib/*" src/main/java/app/Main.java src/main/java/app/db/DBConnection.java src/main/java/app/model/Issue.java src/main/java/app/service/IssueService.java src/main/java/app/ui/AdminDashboard.java src/main/java/app/ui/AdminLogin.java src/main/java/app/ui/UserDashboard.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

REM Run the application
echo Starting application...
java -cp "bin;lib/*" app.Main

pause