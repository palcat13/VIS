package cs.vsb.domain;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public abstract class User extends BaseEntity {
    protected String username;
    protected String password;
    protected String email;

    public User(String username, String password, String email) {
        this.id = null;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User(Long id,String username, String password, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    public void validate() {
        if (username == null || username.isBlank())
            validationErrors.add("Uživatelské jméno je povinné.");
        if (email == null || !email.contains("@"))
            validationErrors.add("Email není platný.");
    }


    public abstract String getRole();
}
