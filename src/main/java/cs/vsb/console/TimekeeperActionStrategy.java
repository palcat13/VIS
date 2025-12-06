package cs.vsb.console;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import cs.vsb.domain.Race;
import cs.vsb.domain.RaceEntry;
import cs.vsb.domain.Timekeeper;
import cs.vsb.service.RaceEntryService;
import cs.vsb.service.RaceService;
import cs.vsb.ui.RaceDetailView;

import java.sql.SQLException;
import java.util.List;

public class TimekeeperActionStrategy implements UserActionStrategy {

    private final Timekeeper timekeeper;
    private final RaceService raceService;
    private final RaceEntryService raceEntryService;

    public TimekeeperActionStrategy(Timekeeper timekeeper, RaceService raceService, RaceEntryService raceEntryService) {
        this.timekeeper = timekeeper;
        this.raceService = raceService;
        this.raceEntryService = raceEntryService;
    }

    // --- DASHBOARD: List all races ---
    @Override
    public Component renderDashboard() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        Grid<Race> grid = new Grid<>(Race.class, false);
        grid.addColumn(Race::getName).setHeader("Závod");
        grid.addColumn(Race::getDate).setHeader("Datum");
        grid.addColumn(r -> r.getLocation().getCity()).setHeader("Místo");

        // Navigate to detail on row click
        grid.addItemClickListener(event ->
                UI.getCurrent().navigate(RaceDetailView.class, event.getItem().getId())
        );

        try {
            grid.setItems(raceService.getAllRaces());
        } catch (SQLException e) {
            Notification.show("Chyba při načítání závodů: " + e.getMessage());
        }

        layout.add(new H3("Vyberte závod pro měření času"), grid);
        return layout;
    }

    // --- DETAIL: Edit Times ---
    @Override
    public Component renderRaceDetail(Race race, List<RaceEntry> entries) {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSizeFull();

        layout.add(new H3("Zadávání časů: " + race.getName()));

        Grid<RaceEntry> grid = new Grid<>(RaceEntry.class, false);

        // Read-only columns
        grid.addColumn(RaceEntry::getRaceNumber).setHeader("Start. číslo").setSortable(true);
        grid.addColumn(e -> e.getRacer().getFname() + " " + e.getRacer().getLname()).setHeader("Závodník");

        // --- Editable Time Column ---
        Binder<RaceEntry> binder = new Binder<>(RaceEntry.class);
        Editor<RaceEntry> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true); // Require explicit save

        // Time Field (Milliseconds)
        NumberField timeField = new NumberField();
        timeField.setWidthFull();
        timeField.setPlaceholder("ms");

        // Bind Long (domain) to Double (UI component)
        binder.forField(timeField)
                .withConverter(Double::longValue, Long::doubleValue, "Musí být číslo")
                .bind(RaceEntry::getRaceTime, RaceEntry::setRaceTime);

        grid.addColumn(RaceEntry::getRaceTime).setHeader("Čas (ms)").setEditorComponent(timeField);

        // Action Buttons for Editor
        Grid.Column<RaceEntry> editColumn = grid.addComponentColumn(entry -> {
            Button editButton = new Button("Zadat čas");
            editButton.addClickListener(e -> {
                if (editor.isOpen()) editor.cancel();
                editor.editItem(entry);
            });
            return editButton;
        });

        Button saveBtn = new Button("Uložit", e -> editor.save());
        Button cancelBtn = new Button("Zrušit", e -> editor.cancel());

        HorizontalLayout actions = new HorizontalLayout(saveBtn, cancelBtn);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);

        // Save Listener
        editor.addSaveListener(e -> {
            try {
                raceEntryService.updateRaceEntry(e.getItem());
                Notification.show("Čas uložen.");
            } catch (SQLException ex) {
                Notification.show("Chyba ukládání: " + ex.getMessage());
                // Refresh items to reset UI state if needed
                grid.getDataProvider().refreshItem(e.getItem());
            }
        });

        grid.setItems(entries);
        layout.add(grid);

        return layout;
    }
}