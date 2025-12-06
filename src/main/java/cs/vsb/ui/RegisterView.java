package cs.vsb.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink; // For navigation links
import cs.vsb.domain.Organizer;
import cs.vsb.domain.Participant;
import cs.vsb.domain.Timekeeper;
import cs.vsb.domain.User;
import cs.vsb.service.UserService;

import java.sql.SQLException;

@Route("register")
public class RegisterView extends VerticalLayout {

    public RegisterView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 title = new H1("Register");

        TextField usernameField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");
        EmailField emailField = new EmailField("Email");

        ComboBox<String> roleSelect = new ComboBox<>("Role");
        roleSelect.setItems("Organizer", "Participant", "Timekeeper");
        roleSelect.setValue("Participant"); // Default

        Checkbox offlineModeField = new Checkbox("Offline Mode");
        offlineModeField.setVisible(false);

        roleSelect.addValueChangeListener(e -> {
            offlineModeField.setVisible("Timekeeper".equals(e.getValue()));
        });

        Button registerButton = new Button("Sign Up", event -> {
            try {
                register(
                        usernameField.getValue(),
                        passwordField.getValue(),
                        emailField.getValue(),
                        roleSelect.getValue(),
                        offlineModeField.getValue()
                );
            } catch (Exception e) {
                Notification.show("Registration failed: " + e.getMessage());
            }
        });

        RouterLink loginLink = new RouterLink("Already have an account? Log in", LoginView.class);

        add(title, usernameField, passwordField, emailField, roleSelect, offlineModeField, registerButton, loginLink);
    }

    private void register(String username, String password, String email, String role, boolean isOffline) throws SQLException {
        if (username.isBlank() || password.isBlank() || email.isBlank() || role == null) {
            Notification.show("Please fill in all fields.");
            return;
        }

        User user;
        switch (role) {
            case "Organizer" -> user = new Organizer(username, password, email);
            case "Timekeeper" -> user = new Timekeeper(username, password, email, isOffline);
            case "Participant" -> user = new Participant(username, password, email, null);
            default -> throw new IllegalArgumentException("Invalid role selected");
        }

        if (!user.isValid()) {
            Notification.show("Validation error: " + user.getValidationErrors());
            return;
        }

        UserService userService = new UserService();
        userService.createUser(user);

        Notification.show("Registration successful! Please log in.");
        UI.getCurrent().navigate(LoginView.class);
    }
}