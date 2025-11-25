package cs.vsb.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class BaseEntity {
    protected Long id;

    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    protected List<String> validationErrors = new ArrayList<>();

    protected BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    public abstract void validate();

    public boolean isValid() {
        validationErrors.clear();
        validate();
        return validationErrors.isEmpty();
    }

    public void markUpdated() {
        this.updatedAt = LocalDateTime.now();
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}
