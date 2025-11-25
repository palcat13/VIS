package cs.vsb.service;

import cs.vsb.domain.Racer;
import cs.vsb.domain.Category;
import cs.vsb.mappers.RacerMapper;
import cs.vsb.mappers.CategoryMapper;
import cs.vsb.db.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RacerService {

    private final RacerMapper mapper;
    private final CategoryMapper categoryMapper;

    public RacerService() throws SQLException {
        Connection connection = ConnectionManager.getConnection();
        this.mapper = new RacerMapper(connection);
        this.categoryMapper = new CategoryMapper(connection);
    }

    public Racer createRacer(Racer racer) throws SQLException {
        mapper.insert(racer);
        return racer;
    }

    public void updateRacer(Racer racer) throws SQLException {
        if (racer.getId() == null) {
            throw new IllegalArgumentException("Racer ID cannot be null for update.");
        }
        mapper.update(racer);
    }

    public void deleteRacer(Long id) throws SQLException {
        mapper.delete(id);
    }

    public Racer getRacer(Long id) throws SQLException {
        return mapper.findById(id);
    }

    public List<Racer> getAllRacers() throws SQLException {
        return mapper.findAll();
    }

    public Category getCategoryForRacer(Racer racer) throws SQLException {
        if (racer.getCategory() != null) {
            return categoryMapper.findById(racer.getCategory().getId());
        }
        return null;
    }
}
