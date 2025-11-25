package cs.vsb.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private static final String URL = "jdbc:h2:file:./db/java2";
    private static final String USER = "app";
    private static final String PASSWORD = "app";

    // Returns a new JDBC Connection
    public static Connection getConnection() throws SQLException {
        try {
            // 1. Explicitly load the driver. This forces it to register with DriverManager.
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("H2 Driver not found in classpath/WEB-INF lib", e);
        }

        // 2. Now DriverManager will find it
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
