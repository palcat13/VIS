package cs.vsb.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import cs.vsb.domain.Organizer;
import cs.vsb.domain.Race;
import cs.vsb.value.Location;
import cs.vsb.service.RaceService;

import java.sql.SQLException;

public class CreateRaceDialog extends Dialog {

    private final RaceService raceService;
    private final Organizer organizer;
    private final Runnable onSuccess;

    public CreateRaceDialog(RaceService raceService, Organizer organizer, Runnable onSuccess) {
        this.raceService = raceService;
        this.organizer = organizer;
        this.onSuccess = onSuccess;

        setHeaderTitle("Nový závod");

        VerticalLayout layout = createFormLayout();
        add(layout);

        Button saveButton = new Button("Uložit", e -> save());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Zrušit", e -> close());

        getFooter().add(cancelButton, saveButton);
    }

    // UI Komponenty
    private final TextField nameField = new TextField("Název závodu");
    private final TextField cityField = new TextField("Město");
    private final TextField countryField = new TextField("Země");
    private final DatePicker datePicker = new DatePicker("Datum konání");
    private final NumberField entryFeeField = new NumberField("Startovné");

    private VerticalLayout createFormLayout() {
        VerticalLayout layout = new VerticalLayout();

        nameField.setWidthFull();
        nameField.setRequired(true);

        // Město a Země vedle sebe
        HorizontalLayout locationLayout = new HorizontalLayout(cityField, countryField);
        locationLayout.setWidthFull();
        cityField.setWidthFull();
        countryField.setWidthFull();

        datePicker.setWidthFull();
        datePicker.setRequired(true);

        entryFeeField.setWidthFull();
        entryFeeField.setRequired(true);

        layout.add(nameField, locationLayout, datePicker,entryFeeField);
        return layout;
    }

    private void save() {
        try {
            // 1. Validace vstupů (UI level)
            if (nameField.isEmpty() || cityField.isEmpty() || countryField.isEmpty() || datePicker.isEmpty()) {
                Notification.show("Vyplňte prosím všechna pole.").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // 2. Vytvoření doménových objektů
            Location location = new Location(cityField.getValue(), countryField.getValue());
            Race race = new Race(
                    nameField.getValue(),
                    location,
                    datePicker.getValue(),
                    organizer,
                    entryFeeField.getValue()

            );

            // 3. Validace doménové logiky
            if (!race.isValid()) {
                Notification.show("Chyba: " + race.getValidationErrors()).addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // 4. Uložení do databáze
            raceService.createRace(race);

            Notification.show("Závod úspěšně vytvořen.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            // 5. Callback pro refresh tabulky v rodiči a zavření okna
            onSuccess.run();
            close();

        } catch (SQLException e) {
            Notification.show("Chyba databáze: " + e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}