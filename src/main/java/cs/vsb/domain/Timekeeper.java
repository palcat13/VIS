package cs.vsb.domain;

import cs.vsb.service.RaceEntryService;
import lombok.*;

import java.sql.SQLException;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class Timekeeper extends User {
    private boolean offlineMode;

    public Timekeeper(String username, String password, String email, boolean offlineMode) {
        super(username, password, email);
        this.offlineMode = offlineMode;
    }

    public Timekeeper(Long id,String username, String password, String email, boolean offlineMode) {
        super(id,username, password, email);
        this.offlineMode = offlineMode;
    }

    @Override
    public void validate() {
        super.validate();
    }

    public void addTime(RaceEntry entry, long raceTime ) throws SQLException {
        RaceEntryService raceEntryService = new RaceEntryService();
        entry.setRaceTime(raceTime);
        raceEntryService.updateRaceEntry(entry);
    }

    @Override
    public String getRole() {
        return "TIMEKEEPER";
    }
}
