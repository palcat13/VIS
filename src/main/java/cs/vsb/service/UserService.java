package cs.vsb.service;

import cs.vsb.domain.User;
import cs.vsb.domain.Participant;
import cs.vsb.domain.Timekeeper;
import cs.vsb.domain.Organizer;
import cs.vsb.mappers.UserMapper;
import cs.vsb.db.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {

    private final UserMapper userMapper;

    public UserService() throws SQLException {
        Connection connection = ConnectionManager.getConnection();
        this.userMapper = new UserMapper(connection);
    }

    public User createUser(User user) throws SQLException {
        userMapper.insert(user);
        return user;
    }

    public void updateUser(User user) throws SQLException {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null for update.");
        }
        userMapper.update(user);
    }

    public User logIn(String username,String password) throws SQLException {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Username or password is incorrect.");
        }
        if(!userMapper.verifyPassword(user, password)) {
            throw new IllegalArgumentException("Incorrect password.");
        }
        return user;
    }

    public void deleteUser(Long id) throws SQLException {
        userMapper.delete(id);
    }

    public User getUser(Long id) throws SQLException {
        return userMapper.findById(id);
    }

    public List<User> getAllUsers() throws SQLException {
        return userMapper.findAll();
    }

    public List<User> getUsersByType(String type) throws SQLException {
        return userMapper.findByType(type);
    }

    public List<Organizer> getAllOrganizers() throws SQLException {
        return userMapper.findByType("Organizer").stream()
                .map(u -> (Organizer) u)
                .toList();
    }

    public List<Participant> getAllParticipants() throws SQLException {
        return userMapper.findByType("Participant").stream()
                .map(u -> (Participant) u)
                .toList();
    }

    public List<Timekeeper> getAllTimekeepers() throws SQLException {
        return userMapper.findByType("Timekeeper").stream()
                .map(u -> (Timekeeper) u)
                .toList();
    }
}
