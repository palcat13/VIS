package cs.vsb.domain;

import cs.vsb.value.YearRange;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Category extends BaseEntity {
    private String name;
    private YearRange yearRange;
    private String gender;

    public Category(String name, int yearFrom, int yearTo, String gender) {
        this.id = null;
        this.name = name;
        this.yearRange = new YearRange(yearFrom, yearTo);
        this.gender = gender;
    }

    public Category(Long id,String name, int yearFrom, int yearTo, String gender) {
        this.id = id;
        this.name = name;
        this.yearRange = new YearRange(yearFrom, yearTo);
        this.gender = gender;
    }
    @Override
    public void validate() {
        if (name == null || name.isBlank())
            validationErrors.add("Název kategorie je povinný.");
        if (yearRange.getYearFrom() > yearRange.getYearTo())
            validationErrors.add("Neplatný rozsah let v kategorii.");
        if (gender == null || gender.isBlank())
            validationErrors.add("pohlavi nesmi byt prazdne");
    }

}
