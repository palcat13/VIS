package cs.vsb.domain;

import cs.vsb.service.RaceEntryService;
import cs.vsb.service.RaceService;
import cs.vsb.service.RacerService;
import lombok.*;

import java.sql.SQLException;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class Participant extends User {
    private Racer racer;

    public Participant(String username, String password, String email, Racer racer) {
        super(username, password, email);
        this.racer = racer;
    }

    public Participant(Long id,String username, String password, String email, Racer racer) {
        super(id,username, password, email);
        this.racer = racer;
    }

    @Override
    public void validate(){
        super.validate();
    }

    public void registerToRace(Race race,Racer racer) throws SQLException {
        if(this.racer == null) {
            RacerService racerService = new RacerService();
            this.racer = racerService.createRacer(racer);
        }
        RaceEntryService raceEntryService = new RaceEntryService();
        raceEntryService.createRaceEntry(new RaceEntry(race,this.racer,0));
    }


    @Override
    public String getRole() {
        return "PARTICIPANT";
    }
}
