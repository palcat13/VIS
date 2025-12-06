package cs.vsb.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Payment extends BaseEntity {
    private Race race;
    private Participant participant;
    private boolean paid;
    private LocalDate date;

    public Payment(Race race, Participant participant, boolean isPaid, LocalDate date) {
        this.race = race;
        this.participant = participant;
        this.paid = isPaid;
        this.date = date;
    }

    @Override
    public void validate() {
        if (participant == null) {
            validationErrors.add(String.format("Participant is mandatory"));
        }
        if (race == null) {
            validationErrors.add(String.format("Race is mandatory"));
        }
        if (date == null) {
            validationErrors.add(String.format("Date is mandatory"));
        }
    }
}
