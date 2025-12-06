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
    private double entryFee;
    private Organizer organizer;
    private boolean published;

    public Race(String name, Location location, LocalDate date,Organizer organizer, double entryFee) {
        this.id = null;
        this.name = name;
        this.location = location;
        this.date = date;
        this.organizer = organizer;
        this.entryFee = entryFee;
    }

    public Race(Long id,String name, Location location, LocalDate date,Organizer organizer,double entryFee,boolean published) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.date = date;
        this.organizer = organizer;
        this.entryFee = entryFee;
        this.published = published;
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
        if (getOrganizer() == null) {
            validationErrors.add("Organizator závodu je povinný.");
        }
        if(entryFee < 0) {
            validationErrors.add("Částka nesmí být záporná.");
        }
    }
}