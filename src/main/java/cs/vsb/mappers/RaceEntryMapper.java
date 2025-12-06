package cs.vsb.mappers;

import cs.vsb.domain.Race;
import cs.vsb.domain.RaceEntry;
import cs.vsb.domain.Racer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RaceEntryMapper {

    private final Connection connection;
    private final RaceMapper raceMapper;
    private final RacerMapper racerMapper;

    public RaceEntryMapper(Connection connection) {
        this.connection = connection;
        this.raceMapper = new RaceMapper(connection);
        this.racerMapper = new RacerMapper(connection);
    }

    public void insert(RaceEntry entry) throws SQLException {
        String sql = "INSERT INTO r_race_entry (race_id, racer_id,race_number, race_time, place) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, entry.getRace().getId());
            stmt.setLong(2, entry.getRacer().getId());
            stmt.setInt(3, entry.getRaceNumber());
            stmt.setLong(4, entry.getRaceTime());
            stmt.setInt(5, entry.getPlace());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                entry.setId(keys.getLong(1));
            }
        }
    }

    public void update(RaceEntry entry) throws SQLException {
        String sql = "UPDATE r_race_entry SET race_id=?, racer_id=?,race_number = ?, race_time=?, place=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, entry.getRace().getId());
            stmt.setLong(2, entry.getRacer().getId());
            stmt.setInt(3, entry.getRaceNumber());
            stmt.setLong(4, entry.getRaceTime());
            stmt.setInt(5, entry.getPlace());
            stmt.setLong(6, entry.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM r_race_entry WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public RaceEntry findById(Long id) throws SQLException {
        String sql = "SELECT * FROM r_race_entry WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    public List<RaceEntry> findByRaceId(Long raceId) throws SQLException {
        String sql = "SELECT * FROM r_race_entry WHERE race_id = ?";
        List<RaceEntry> entries = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, raceId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                entries.add(mapRow(rs));
            }
        }
        return entries;
    }

    public List<RaceEntry> findAll() throws SQLException {
        String sql = "SELECT * FROM r_race_entry";
        List<RaceEntry> entries = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                entries.add(mapRow(rs));
            }
        }
        return entries;
    }

    private RaceEntry mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");

        Long raceId = rs.getLong("race_id");
        Race race = raceMapper.findById(raceId);

        Long racerId = rs.getLong("racer_id");
        Racer racer = racerMapper.findById(racerId);
        int raceNumber = rs.getInt("race_number");
        long raceTime = rs.getLong("race_time");
        int place = rs.getInt("place");

        RaceEntry entry = new RaceEntry(id,race, racer,raceNumber, raceTime,place);
        return entry;
    }
}
