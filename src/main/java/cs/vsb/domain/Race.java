package cs.vsb.domain;


import cs.vsb.value.Location;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Setter
@Getter
@ToString
public class Race extends BaseEntity {
    private String name;
    private Location location;
    private LocalDate date;
    private Organizer organizer;

    public Race(String name, Location location, LocalDate date,Organizer organizer) {
        this.id = null;
        this.name = name;
        this.location = location;
        this.date = date;
        this.organizer = organizer;
    }

    public Race(Long id,String name, Location location, LocalDate date,Organizer organizer) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.date = date;
        this.organizer = organizer;
    }

    @Override
    public void validate() {
        if (name == null || name.isBlank()) {
            validationErrors.add("Název závodu nesmí být prázdný.");
        }
        if (location == null) {
            validationErrors.add("Místo nesmí být prázdné.");
        }
        if (date == null) {
            validationErrors.add("Datum závodu je povinné.");
        }
        if (organizer == null) {
            validationErrors.add("Organizator závodu je povinný.");
        }
    }
}