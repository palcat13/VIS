package cs.vsb.domain;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class Organizer extends User {
    private List<Race> races = new ArrayList<>();

    public Organizer(String username, String password, String email) {
        super(username, password, email);
    }

    public Organizer(Long id,String username, String password, String email) {
        super(id,username, password, email);
    }

    @Override
    public String getRole() {
        return "ORGANIZER";
    }

    public void addRace(Race race) {
        races.add(race);
    }

    public void publishRace(Race race){
        if(races.contains(race)){
            race.setPublished(true);
        }
    }

    @Override
    public void validate(){
        super.validate();
    }



}
