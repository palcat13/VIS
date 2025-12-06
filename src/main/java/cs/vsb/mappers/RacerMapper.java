package cs.vsb.mappers;

import cs.vsb.domain.Racer;
import cs.vsb.domain.Category;
import cs.vsb.orm.RacerProxy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RacerMapper {

    private final Connection connection;
    private final CategoryMapper categoryMapper;

    public RacerMapper(Connection connection) {
        this.connection = connection;
        this.categoryMapper = new CategoryMapper(connection);
    }

    public void insert(Racer racer) throws SQLException {
        String sql = "INSERT INTO r_racer (fname, lname, birth_year, gender, category_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, racer.getFname());
            stmt.setString(2, racer.getLname());
            stmt.setInt(3, racer.getBirthYear());
            stmt.setString(4, racer.getGender());
            if (racer.getCategory() != null && racer.getCategory().getId() != null) {
                stmt.setLong(5, racer.getCategory().getId());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                racer.setId(keys.getLong(1));
            }
        }
    }

    public void update(Racer racer) throws SQLException {
        String sql = "UPDATE r_racer SET fname=?, lname=?, birth_year=?, gender=?, category_id=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, racer.getFname());
            stmt.setString(2, racer.getLname());
            stmt.setInt(3, racer.getBirthYear());
            stmt.setString(4, racer.getGender());
            if (racer.getCategory() != null && racer.getCategory().getId() != null) {
                stmt.setLong(5, racer.getCategory().getId());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }
            stmt.setLong(6, racer.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM r_racer WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public Racer findById(Long id) throws SQLException {
        String sql = "SELECT * FROM r_racer WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    public List<Racer> findAll() throws SQLException {
        String sql = "SELECT * FROM r_racer";
        List<Racer> racers = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                racers.add(mapRow(rs));
            }
        }
        return racers;
    }

    private Racer mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String fname = rs.getString("fname");
        String lname = rs.getString("lname");
        int birthYear = rs.getInt("birth_year");
        String gender = rs.getString("gender");

        Long categoryId = rs.getLong("category_id");
        if (!rs.wasNull()) {
            categoryId = null;
        }

        if (categoryId != null) {
            return new RacerProxy(id, fname, lname, birthYear, gender, categoryId, categoryMapper);
        } else {
            Racer racer = new Racer(id, fname, lname, birthYear, gender);
            racer.setCategory(null);
            return racer;
        }
    }
}
