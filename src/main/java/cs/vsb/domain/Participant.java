package cs.vsb.domain;

import lombok.*;

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


    @Override
    public String getRole() {
        return "PARTICIPANT";
    }
}
