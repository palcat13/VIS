package cs.vsb.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private static final String URL = "jdbc:h2:file:/home/palcki/IdeaProjects/VIS/db/java2;AUTO_SERVER=TRUE";
    private static final String USER = "app";
    private static final String PASSWORD = "app";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("H2 Driver not found in classpath/WEB-INF lib", e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
