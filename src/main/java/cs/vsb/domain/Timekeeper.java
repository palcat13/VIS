package cs.vsb.domain;

import lombok.*;

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

    @Override
    public String getRole() {
        return "TIMEKEEPER";
    }
}
