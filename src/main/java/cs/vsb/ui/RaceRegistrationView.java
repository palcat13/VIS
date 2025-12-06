package cs.vsb.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import cs.vsb.domain.*;
import cs.vsb.service.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Random;

@Route(value = "registration", layout = MainLayout.class)
public class RaceRegistrationView extends VerticalLayout implements HasUrlParameter<Long> {

    private final RaceService raceService;
    private final PaymentService paymentService;
    private final UserService userService;

    private Participant loggedParticipant;
    private Race selectedRace;
    private final Binder<Racer> racerBinder = new Binder<>(Racer.class);

    // UI Components
    private TextField fnameField;
    private TextField lnameField;
    private IntegerField birthYearField;
    private ComboBox<String> genderField;

    public RaceRegistrationView() throws SQLException {
        this.raceService = new RaceService();
        this.paymentService = new PaymentService();
        this.userService = new UserService();

        setSizeFull();
        setAlignItems(Alignment.CENTER);
    }

    @Override
    public void setParameter(BeforeEvent event, Long raceId) {
        // 1. Check if user is a Participant
        User user = VaadinSession.getCurrent().getAttribute(User.class);
        if (!(user instanceof Participant)) {
            event.rerouteTo(MainView.class);
            Notification.show("Access denied: Participants only.", 3000, Notification.Position.MIDDLE);
            return;
        }
        this.loggedParticipant = (Participant) user;

        // 2. Load the Race from the ID passed in URL
        try {
            this.selectedRace = raceService.getRace(raceId);
            if (this.selectedRace == null) {
                Notification.show("Race not found.");
                event.rerouteTo(MainView.class);
                return;
            }
            removeAll();
            buildUI();
        } catch (SQLException e) {
            Notification.show("Database error: " + e.getMessage());
        }
    }

    private void buildUI() {
        VerticalLayout container = new VerticalLayout();
        container.setMaxWidth("600px");
        container.setWidthFull();
        container.setAlignItems(Alignment.STRETCH);

        // Header showing the selected race
        container.add(new H2("Registrace do závodu"));

        VerticalLayout raceInfo = new VerticalLayout();
        raceInfo.setPadding(false);
        raceInfo.setSpacing(false);
        raceInfo.add(new H3(selectedRace.getName()));
        raceInfo.add(new Span(selectedRace.getLocation().getCity() + ", " + selectedRace.getDate()));
        container.add(raceInfo);

        // Racer Form
        FormLayout racerForm = new FormLayout();

        fnameField = new TextField("Jméno");
        lnameField = new TextField("Příjmení");
        birthYearField = new IntegerField("Rok narození");
        genderField = new ComboBox<>("Pohlaví");
        genderField.setItems("Muž", "Žena");

        // Note: Category selection removed as requested.

        racerForm.add(fnameField, lnameField, birthYearField, genderField);

        // Binding
        racerBinder.forField(fnameField).asRequired().bind(Racer::getFname, Racer::setFname);
        racerBinder.forField(lnameField).asRequired().bind(Racer::getLname, Racer::setLname);
        racerBinder.forField(birthYearField).asRequired().bind(Racer::getBirthYear, Racer::setBirthYear);
        racerBinder.forField(genderField).asRequired().bind(Racer::getGender, Racer::setGender);

        // Pre-fill data if Participant already has a Racer profile
        if (loggedParticipant.getRacer() != null) {
            racerBinder.setBean(loggedParticipant.getRacer());
        } else {
            racerBinder.setBean(new Racer("", "", 2000, ""));
        }

        Button payAndRegisterBtn = new Button("Přejít k platbě a registrovat", e -> initiateRegistration());
        payAndRegisterBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        payAndRegisterBtn.setWidthFull();

        container.add(new H3("Údaje závodníka"), racerForm, payAndRegisterBtn);
        add(container);
    }

    private void initiateRegistration() {
        if (!racerBinder.validate().isOk()) {
            Notification.show("Vyplňte prosím správně údaje o závodníkovi.").addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        // Open Payment Gateway Simulation
        openPaymentGatewayDialog();
    }

    private void openPaymentGatewayDialog() {
        Dialog paymentDialog = new Dialog();
        paymentDialog.setHeaderTitle("Platební brána");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(new Span("Závod: " + selectedRace.getName()));
        dialogLayout.add(new Span("Cena startovného: 500 CZK"));
        dialogLayout.add(new TextField("Číslo karty (Simulace)"));

        Button payBtn = new Button("Zaplatit", e -> {
            boolean paymentSuccess = simulatePaymentProcessing();
            if (paymentSuccess) {
                paymentDialog.close();
                completeRegistration(500.0);
            } else {
                Notification.show("Platba byla zamítnuta. Zkuste to prosím znovu.")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        payBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelBtn = new Button("Zrušit", e -> paymentDialog.close());

        paymentDialog.getFooter().add(cancelBtn, payBtn);
        paymentDialog.add(dialogLayout);
        paymentDialog.open();
    }

    private boolean simulatePaymentProcessing() {
        return new Random().nextInt(10) > 1; // 80% success chance
    }

    private void completeRegistration(double amount) {
        try {
            Racer racer = racerBinder.getBean();

            // NOTE: 'racer.getCategory()' is null here because we removed the selection.
            // Logic for auto-assigning category is pending implementation.

            // 1. Call logic in Participant domain
            loggedParticipant.registerToRace(selectedRace, racer);

            // If a new Racer was created, update the User link in DB
            userService.updateUser(loggedParticipant);

            // 2. Create Payment Record
            Payment payment = new Payment(selectedRace, loggedParticipant, true, LocalDate.now());
            paymentService.createPayment(payment);

            // 3. Success
            Notification.show("Registrace úspěšná! Potvrzovací e-mail byl odeslán.")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            UI.getCurrent().navigate(MainView.class);

        } catch (SQLException e) {
            Notification.show("Chyba při registraci: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }
}