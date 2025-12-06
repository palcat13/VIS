package cs.vsb.service;

import cs.vsb.db.ConnectionManager;
import cs.vsb.domain.RaceEntry;
import cs.vsb.mappers.RaceEntryMapper;
import cs.vsb.mappers.ResultJsonExportMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RaceEntryService {

    private final RaceEntryMapper sqlMapper;
    private final ResultJsonExportMapper jsonMapper;

    public RaceEntryService() throws SQLException {
        Connection connection = ConnectionManager.getConnection();
        this.sqlMapper = new RaceEntryMapper(connection);
        this.jsonMapper = new ResultJsonExportMapper();
    }

    public RaceEntry createRaceEntry(RaceEntry entry) throws SQLException {
        sqlMapper.insert(entry);
        return entry;
    }

    public void updateRaceEntry(RaceEntry entry) throws SQLException {
        if (entry.getId() == null) {
            throw new IllegalArgumentException("RaceEntry ID cannot be null for update.");
        }
        sqlMapper.update(entry);
    }

    public void deleteRaceEntry(Long id) throws SQLException {
        sqlMapper.delete(id);
    }

    public RaceEntry getRaceEntry(Long id) throws SQLException {
        return sqlMapper.findById(id);
    }

    public List<RaceEntry> getAllRaceEntries() throws SQLException {
        return sqlMapper.findAll();
    }


    public String getRaceResultsAsJson(Long raceId) throws SQLException {
        List<RaceEntry> entries = sqlMapper.findByRaceId(raceId);
        if (entries.isEmpty()) {
            return "[]"; // Return empty JSON array if no entries
        }
        return jsonMapper.generateJsonString(entries);
    }
}
