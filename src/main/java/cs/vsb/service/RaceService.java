package cs.vsb.service;

import cs.vsb.domain.Race;
import cs.vsb.mappers.RaceMapper;
import cs.vsb.db.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RaceService {

    private final RaceMapper raceMapper;

    public RaceService() throws SQLException {
        Connection connection = ConnectionManager.getConnection();
        this.raceMapper = new RaceMapper(connection);
    }

    public Race createRace(Race race) throws SQLException {
        raceMapper.insert(race);
        return race;
    }

    public void updateRace(Race race) throws SQLException {
        if (race.getId() == null) {
            throw new IllegalArgumentException("Race ID cannot be null for update.");
        }
        raceMapper.update(race);
    }

    public void deleteRace(Long id) throws SQLException {
        raceMapper.delete(id);
    }

    public Race getRace(Long id) throws SQLException {
        return raceMapper.findById(id);
    }

    public List<Race> getAllRaces() throws SQLException {
        return raceMapper.findAll();
    }

}
