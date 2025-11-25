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
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
