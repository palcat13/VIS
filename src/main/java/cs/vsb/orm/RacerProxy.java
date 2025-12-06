package cs.vsb.orm;

import cs.vsb.domain.Category;
import cs.vsb.domain.Racer;
import cs.vsb.mappers.CategoryMapper;

import java.sql.SQLException;

public class RacerProxy extends Racer {

    private final Long categoryId;
    private final CategoryMapper categoryMapper;
    private boolean isLoaded = false;

    public RacerProxy(Long id, String fname, String lname, int birthYear, String gender, Long categoryId, CategoryMapper categoryMapper) {
        super(id, fname, lname, birthYear, gender);
        super.setCategory(null);
        this.categoryId = categoryId;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Category getCategory() {
        if (!isLoaded) {
            if (super.getCategory() == null && categoryId != null) {
                try {
                    Category fetchedCategory = categoryMapper.findById(categoryId);
                    super.setCategory(fetchedCategory);
                } catch (SQLException e) {
                    throw new RuntimeException("Lazy loading failed for Category ID: " + categoryId, e);
                }
            }
            isLoaded = true;
        }
        return super.getCategory();
    }
}