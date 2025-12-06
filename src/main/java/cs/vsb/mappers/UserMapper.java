package cs.vsb.mappers;

import cs.vsb.domain.User;
import cs.vsb.domain.Organizer;
import cs.vsb.domain.Participant;
import cs.vsb.domain.Racer;
import cs.vsb.domain.Timekeeper;
import cs.vsb.orm.ParticipantProxy;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static cs.vsb.db.PasswordUtils.generateSalt;
import static cs.vsb.db.PasswordUtils.hashPassword;

public class UserMapper {

    private final Connection connection;
    private final RacerMapper racerMapper;

    public UserMapper(Connection connection) {
        this.connection = connection;
        this.racerMapper = new RacerMapper(connection);
    }


    public User findById(Long id) throws SQLException {
        String sql = "SELECT * FROM r_user WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        }
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM r_user WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        }
    }

    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM r_user";
        List<User> users = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        }
        return users;
    }

    public void insert(User user) throws SQLException {
        String salt = generateSalt();
        String hashed = hashPassword(user.getPassword(), salt);

        String sql = "INSERT INTO r_user (username, hash, salt, email, user_type, offline_mode, racer_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, hashed);
            stmt.setString(3, salt);
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getRole());

            if (user instanceof Timekeeper timekeeper) {
                stmt.setBoolean(6, timekeeper.isOfflineMode());
            } else {
                stmt.setNull(6, Types.BOOLEAN);
            }

            if (user instanceof Participant participant && participant.getRacer() != null) {
                stmt.setLong(7, participant.getRacer().getId());
            } else {
                stmt.setNull(7, Types.BIGINT);
            }

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                user.setId(keys.getLong(1));
            }
        }
    }

    public void update(User user) throws SQLException {

        String sql = "UPDATE r_user SET username=?, hash=?,salt=?, email=?, user_type=?, offline_mode=?, racer_id=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, getSaltForUser(user.getId()));
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getRole());

            if (user instanceof Timekeeper timekeeper) {
                stmt.setBoolean(6, timekeeper.isOfflineMode());
            } else {
                stmt.setNull(6, Types.BOOLEAN);
            }

            // Handle racer_id for Participant
            if (user instanceof Participant participant && participant.getRacer() != null) {
                stmt.setLong(7, participant.getRacer().getId());
            } else {
                stmt.setNull(7, Types.BIGINT);
            }

            stmt.setLong(8, user.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM r_user WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public List<User> findByType(String type) throws SQLException {
        String sql = "SELECT * FROM r_user WHERE user_type=?";
        List<User> users = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        }
        return users;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        String type = rs.getString("user_type");
        User user;

        Long id = rs.getLong("id");
        String username = rs.getString("username");
        String hash = rs.getString("hash");
        String email = rs.getString("email");

        switch (type) {
            case "ORGANIZER":
                user = new Organizer(id, username, hash, email);
                break;
            case "PARTICIPANT":
                Long racerId = rs.getLong("racer_id");
                if (rs.wasNull()) {
                    user = new Participant(id, username, hash, email, null);
                } else {
                    user = new ParticipantProxy(id, username, hash, email, racerId, racerMapper);
                }
                break;
            case "TIMEKEEPER":
                boolean offline = rs.getBoolean("offline_mode");
                user = new Timekeeper(id, username, hash, email, offline);
                break;
            default:
                throw new IllegalArgumentException("Unknown user type: " + type);
        }

        return user;
    }

    public String getSaltForUser(long userId) throws SQLException {
        String sql = "SELECT salt FROM r_user WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("salt");
            } else {
                throw new IllegalArgumentException("User not found");
            }
        }
    }



}
