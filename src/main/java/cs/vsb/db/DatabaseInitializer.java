package cs.vsb.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {



    public static void init() {
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                
                    CREATE TABLE IF NOT EXISTS r_user (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(50) NOT NULL,
                    hash VARCHAR(50) NOT NULL,
                    salt VARCHAR(255),
                    email VARCHAR(100),
                    user_type VARCHAR(20) NOT NULL,   
                    offline_mode BOOLEAN,              
                    racer_id BIGINT            
                );
                
                """);

            // Categories table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS r_category (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(50) NOT NULL,
                    year_from INT NOT NULL,
                    year_to INT NOT NULL,
                    gender VARCHAR(10) NOT NULL
                );
                """);

            // Racers table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS r_racer (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    fname VARCHAR(50) NOT NULL,
                    lname VARCHAR(50) NOT NULL,
                    birth_year INT NOT NULL,
                    gender VARCHAR(10) NOT NULL,
                    category_id BIGINT,
                    FOREIGN KEY (category_id) REFERENCES r_category(id)
                );
                """);

            // Races table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS r_race (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100) NOT NULL,
                    city VARCHAR(100),
                    country VARCHAR(100),
                    date DATE NOT NULL,
                    fee DOUBLE NOT NULL,
                    organizer_id BIGINT,
                    published BOOLEAN NOT NULL,
                    FOREIGN KEY (organizer_id) REFERENCES r_user(id)
                );
                """);

            // Race entries table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS r_race_entry (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    race_id BIGINT,
                    racer_id BIGINT,
                    race_number INT,
                    race_time BIGINT,
                    place INT,
                    FOREIGN KEY (race_id) REFERENCES r_race(id),
                    FOREIGN KEY (racer_id) REFERENCES r_racer(id)
                );
                """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS r_payment (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    race_id BIGINT NOT NULL,
                    participant_id BIGINT NOT NULL,
                    paid BOOLEAN NOT NULL,
                    payment_date DATE NOT NULL,
                    FOREIGN KEY (race_id) REFERENCES r_race(id),
                    FOREIGN KEY (participant_id) REFERENCES r_user(id)
                );
                """);

            System.out.println("Tables created successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
