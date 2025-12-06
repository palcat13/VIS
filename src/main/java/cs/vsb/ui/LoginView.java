package cs.vsb.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import cs.vsb.console.*;
import cs.vsb.domain.*;
import cs.vsb.service.*;

import java.sql.SQLException;

@Route("login")
public class LoginView extends VerticalLayout {

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 title = new H1("Login");
        TextField usernameField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");

        Button loginButton = new Button("Log In", event -> {
            try {
                login(usernameField.getValue(), passwordField.getValue());
            } catch (Exception e) {
                Notification.show("Login failed: " + e.getMessage());
            }
        });

        add(title, usernameField, passwordField, loginButton);
    }

    private void login(String username, String password) throws SQLException {
        // 1. Authenticate User
        UserService userService = new UserService();
        User user = userService.logIn(username, password);

        // 2. Instantiate Services required for strategies
        RaceService raceService = new RaceService();
        RaceEntryService raceEntryService = new RaceEntryService();
        CategoryService categoryService = new CategoryService();

        // 3. Determine and Create Strategy
        UserActionStrategy strategy = null;

        if (user instanceof Organizer organizer) {
            strategy = new OrganizerActionStrategy(organizer, raceService, categoryService, raceEntryService);
        } else if (user instanceof Participant participant) {
            strategy = new ParticipantActionStrategy(participant, raceService);
        } else if (user instanceof Timekeeper timekeeper) {
            strategy = new TimekeeperActionStrategy(timekeeper,raceService,raceEntryService);
        }

        // 4. Store in Session
        VaadinSession.getCurrent().setAttribute(User.class, user);
        VaadinSession.getCurrent().setAttribute(UserActionStrategy.class, strategy);

        UI.getCurrent().navigate(MainView.class);
    }
}