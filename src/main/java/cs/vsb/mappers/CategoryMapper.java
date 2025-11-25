package cs.vsb.mappers;

import cs.vsb.domain.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryMapper {

    private final Connection connection;

    public CategoryMapper(Connection connection) {
        this.connection = connection;
    }

    public void insert(Category category) throws SQLException {
        String sql = "INSERT INTO r_category (name, year_from, year_to, gender) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, category.getName());
            stmt.setInt(2, category.getYearRange().getYearFrom());
            stmt.setInt(3, category.getYearRange().getYearTo());
            stmt.setString(4, category.getGender());

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                category.setId(keys.getLong(1));
            }
        }
    }

    public void update(Category category) throws SQLException {
        String sql = "UPDATE r_category SET name=?, year_from=?, year_to=?, gender=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, category.getName());
            stmt.setInt(2, category.getYearRange().getYearFrom());
            stmt.setInt(3, category.getYearRange().getYearTo());
            stmt.setString(4, category.getGender());
            stmt.setLong(5, category.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM r_category WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public Category findById(Long id) throws SQLException {
        String sql = "SELECT * FROM r_category WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    public List<Category> findAll() throws SQLException {
        String sql = "SELECT * FROM r_category";
        List<Category> categories = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                categories.add(mapRow(rs));
            }
        }
        return categories;
    }

    private Category mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        int yearFrom = rs.getInt("year_from");
        int yearTo = rs.getInt("year_to");
        String gender = rs.getString("gender");

        return new Category(id, name, yearFrom, yearTo, gender);
    }
}

