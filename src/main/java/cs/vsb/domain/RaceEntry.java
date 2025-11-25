package cs.vsb.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@AllArgsConstructor
@Setter
@Getter
@ToString

public class RaceEntry extends BaseEntity {
    private Race race;
    private Racer racer;
    private int raceNumber;
    private long raceTime;
    private int place;


    public RaceEntry(Race race, Racer racer, long raceTime) {
        this.id = null;
        this.race = race;
        this.racer = racer;
        this.raceNumber = 0;
        this.raceTime = raceTime;
        this.place = 0;
    }

    public RaceEntry(Long id,Race race, Racer racer,int raceNumber, long raceTime, int place) {
        this.id = id;
        this.race = race;
        this.racer = racer;
        this.raceNumber = raceNumber;
        this.raceTime = raceTime;
        this.place = place;
    }

    @Override
    public void validate() {
        if (this.race == null)
            validationErrors.add("zavod nesmi byt prazdny");
        if (this.racer == null)
            validationErrors.add("zavodnik nesmi byt prazdny");
    }


}
