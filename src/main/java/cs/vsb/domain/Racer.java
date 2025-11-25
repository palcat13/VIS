package cs.vsb.domain;

import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString

public class Racer extends BaseEntity {
    private String fname;
    private String lname;
    private int birthYear;
    private String gender;

    private Category category;

    public Racer(String fname, String lname, int birthYear, String gender) {
        this.id = null;
        this.fname = fname;
        this.lname = lname;
        this.birthYear = birthYear;
        this.gender = gender;
    }

    public Racer(Long id, String fname, String lname, int birthYear, String gender) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.birthYear = birthYear;
        this.gender = gender;
    }

    @Override
    public void validate() {
        if (this.fname == null || this.fname.isBlank())
            validationErrors.add("jmeno nesmi byt prazdne");
        if (this.lname == null || this.lname.isBlank())
            validationErrors.add("prijmeni nesmi byt prazdne");
        if (gender == null || gender.isBlank())
            validationErrors.add("pohlavi nesmi byt prazdne");
    }
}
