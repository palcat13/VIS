package cs.vsb.service;

import cs.vsb.db.ConnectionManager;
import cs.vsb.domain.RaceEntry;
import cs.vsb.mappers.RaceEntryMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RaceEntryService {

    private final RaceEntryMapper mapper;

    public RaceEntryService() throws SQLException {
        Connection connection = ConnectionManager.getConnection();
        this.mapper = new RaceEntryMapper(connection);
    }

    public RaceEntry createRaceEntry(RaceEntry entry) throws SQLException {
        mapper.insert(entry);
        return entry;
    }

    public void updateRaceEntry(RaceEntry entry) throws SQLException {
        if (entry.getId() == null) {
            throw new IllegalArgumentException("RaceEntry ID cannot be null for update.");
        }
        mapper.update(entry);
    }

    public void deleteRaceEntry(Long id) throws SQLException {
        mapper.delete(id);
    }

    public RaceEntry getRaceEntry(Long id) throws SQLException {
        return mapper.findById(id);
    }

    public List<RaceEntry> getAllRaceEntries() throws SQLException {
        return mapper.findAll();
    }
}
