package cs.vsb.mappers;

import cs.vsb.value.Location;
import cs.vsb.domain.Race;
import cs.vsb.domain.Organizer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RaceMapper {

    private final Connection connection;
    private final UserMapper userMapper;

    public RaceMapper(Connection connection) {
        this.connection = connection;
        this.userMapper = new UserMapper(connection);
    }

    public Race findById(Long id) throws SQLException {
        String sql = "SELECT * FROM r_race WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        }
    }

    public List<Race> findAll() throws SQLException {
        String sql = "SELECT * FROM r_race";
        List<Race> races = new ArrayList<>();

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                races.add(mapRow(rs));
            }
        }
        return races;
    }

    public void insert(Race race) throws SQLException {
        String sql = "INSERT INTO r_race (name, city,country, date, organizer_id) VALUES (?, ?,?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, race.getName());
            stmt.setString(2, race.getLocation().getCity());
            stmt.setString(3,race.getLocation().getCountry());
            stmt.setDate(3, Date.valueOf(race.getDate()));
            stmt.setLong(4, race.getOrganizer() != null ? race.getOrganizer().getId() : 0); // Organizer → ID

            stmt.executeUpdate();

            // Set generated ID back to domain object
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                race.setId(keys.getLong(1));
            }
        }
    }

    public void update(Race race) throws SQLException {
        String sql = "UPDATE r_race SET name = ?, city = ?,country = ?, date = ?, organizer_id = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, race.getName());
            stmt.setString(2, race.getLocation().getCity());
            stmt.setString(3, race.getLocation().getCountry());
            stmt.setDate(4, Date.valueOf(race.getDate()));
            stmt.setLong(5, race.getOrganizer().getId());
            stmt.setLong(6, race.getId());

            stmt.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM r_race WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private Race mapRow(ResultSet rs) throws SQLException {
        Long userID = rs.getLong("organizer_id");
        Organizer organizer = (Organizer) userMapper.findById(userID);

        return new Race(
                rs.getLong("id"),
                rs.getString("name"),
                new Location(rs.getString("city"), rs.getString("country")),
                rs.getDate("date").toLocalDate(),
                organizer
        );
    }
}

