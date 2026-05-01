package app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/civic_app?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
private static final String DB_USER = "civic_user";
private static final String DB_PASSWORD = "1234";

    private static Connection connection;

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC driver not found.", e);
            } catch (SQLException e) {
                throw new SQLException("Unable to connect to MySQL. Check JDBC settings.", e);
            }
        }
        return connection;
    }
}