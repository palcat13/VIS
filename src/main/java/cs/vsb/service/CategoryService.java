package cs.vsb.service;

import cs.vsb.domain.Category;
import cs.vsb.mappers.CategoryMapper;
import cs.vsb.db.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CategoryService {

    private final CategoryMapper mapper;

    public CategoryService() throws SQLException {
        Connection connection = ConnectionManager.getConnection();
        this.mapper = new CategoryMapper(connection);
    }

    public Category createCategory(Category category) throws SQLException {
        mapper.insert(category);
        return category;
    }

    public void updateCategory(Category category) throws SQLException {
        if (category.getId() == null) {
            throw new IllegalArgumentException("Category ID cannot be null for update.");
        }
        mapper.update(category);
    }

    public void deleteCategory(Long id) throws SQLException {
        mapper.delete(id);
    }

    public Category getCategory(Long id) throws SQLException {
        return mapper.findById(id);
    }

    public List<Category> getAllCategories() throws SQLException {
        return mapper.findAll();
    }
}
