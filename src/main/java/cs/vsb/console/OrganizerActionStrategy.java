package cs.vsb.console;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.server.StreamResource;
import cs.vsb.domain.Category;
import cs.vsb.domain.Organizer;
import cs.vsb.domain.Race;
import cs.vsb.domain.RaceEntry;
import cs.vsb.service.CategoryService;
import cs.vsb.service.RaceEntryService;
import cs.vsb.service.RaceService;
import cs.vsb.ui.CreateRaceDialog;
import cs.vsb.ui.RaceDetailView;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

public class OrganizerActionStrategy implements UserActionStrategy {

    private final Organizer organizer;
    private final RaceService raceService;
    private final CategoryService categoryService;
    private final RaceEntryService raceEntryService;

    public OrganizerActionStrategy(Organizer organizer, RaceService raceService, CategoryService categoryService, RaceEntryService raceEntryService) {
        this.organizer = organizer;
        this.raceService = raceService;
        this.categoryService = categoryService;
        this.raceEntryService = raceEntryService;
    }

    // --- DASHBOARD LOGIC ---
    @Override
    public Component renderDashboard() {
        VerticalLayout root = new VerticalLayout();
        root.setSizeFull();

        Tabs tabs = new Tabs();
        Tab racesTab = new Tab("Závody");
        Tab categoriesTab = new Tab("Kategorie");
        tabs.add(racesTab, categoriesTab);

        VerticalLayout tabContent = new VerticalLayout();
        tabContent.setSizeFull();

        // Default
        showOrganizerRaces(tabContent);

        tabs.addSelectedChangeListener(event -> {
            tabContent.removeAll();
            if (event.getSelectedTab().equals(racesTab)) {
                showOrganizerRaces(tabContent);
            } else if (event.getSelectedTab().equals(categoriesTab)) {
                showOrganizerCategories(tabContent);
            }
        });

        root.add(tabs, tabContent);
        return root;
    }

    private void showOrganizerRaces(VerticalLayout layout) {
        Grid<Race> grid = new Grid<>(Race.class, false);
        grid.addColumn(Race::getName).setHeader("Název");
        grid.addColumn(Race::getDate).setHeader("Datum");
        grid.addColumn(r -> r.isPublished() ? "ANO" : "NE").setHeader("Zveřejněno");

        // Navigate to Detail View on click
        grid.addItemClickListener(e ->
                UI.getCurrent().navigate(RaceDetailView.class, e.getItem().getId())
        );

        Runnable refreshData = () -> {
            try {
                grid.setItems(raceService.getAllRaces());
            } catch (SQLException e) {
                Notification.show("Error: " + e.getMessage());
            }
        };
        refreshData.run();

        Button addBtn = new Button("Vytvořit závod", e -> {
            new CreateRaceDialog(raceService, organizer, refreshData).open();
        });
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        layout.add(new H3("Správa závodů (Klikněte pro detail)"), addBtn, grid);
    }

    private void showOrganizerCategories(VerticalLayout layout) {
        Grid<Category> grid = new Grid<>(Category.class, false);
        grid.addColumn(Category::getName).setHeader("Název");
        try {
            grid.setItems(categoryService.getAllCategories());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        layout.add(new H3("Správa kategorií"), grid);
    }

    // --- RACE DETAIL LOGIC ---
    @Override
    public Component renderRaceDetail(Race race, List<RaceEntry> entries) {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        // --- EXPORT BUTTON LOGIC ---

        // 1. Create the StreamResource
        StreamResource jsonResource = new StreamResource("results_" + race.getId() + ".json", () -> {
            try {
                // Fetch JSON content dynamically when clicked
                String jsonContent = raceEntryService.getRaceResultsAsJson(race.getId());
                return new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
                return new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8));
            }
        });

        // 2. Create the Button
        Button exportBtn = new Button("Stáhnout JSON");
        exportBtn.setIcon(VaadinIcon.DOWNLOAD.create());

        // 3. Wrap in Anchor
        Anchor downloadAnchor = new Anchor(jsonResource, "");
        downloadAnchor.getElement().setAttribute("download", true);
        downloadAnchor.add(exportBtn);

        // --- PUBLISH BUTTON LOGIC ---
        Button publishBtn = new Button(race.isPublished() ? "Skrýt výsledky" : "Zveřejnit výsledky");
        publishBtn.addThemeVariants(race.isPublished() ? ButtonVariant.LUMO_ERROR : ButtonVariant.LUMO_SUCCESS);
        publishBtn.addClickListener(e -> {
            try {
                race.setPublished(!race.isPublished());
                raceService.updateRace(race);
                // Refresh UI state
                publishBtn.setText(race.isPublished() ? "Skrýt výsledky" : "Zveřejnit výsledky");
                publishBtn.removeThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SUCCESS);
                publishBtn.addThemeVariants(race.isPublished() ? ButtonVariant.LUMO_ERROR : ButtonVariant.LUMO_SUCCESS);
                Notification.show("Stav změněn.");
            } catch (SQLException ex) {
                Notification.show("Chyba: " + ex.getMessage());
            }
        });

        layout.add(new HorizontalLayout(downloadAnchor, publishBtn));

        // 2. Editable Grid
        Grid<RaceEntry> grid = new Grid<>(RaceEntry.class, false);
        grid.addColumn(e -> e.getRacer().getFname() + " " + e.getRacer().getLname()).setHeader("Závodník");
        grid.addColumn(RaceEntry::getRaceNumber).setHeader("Start. č.");

        Binder<RaceEntry> binder = new Binder<>(RaceEntry.class);
        Editor<RaceEntry> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        IntegerField placeField = new IntegerField();
        binder.forField(placeField).bind(RaceEntry::getPlace, RaceEntry::setPlace);
        grid.addColumn(RaceEntry::getPlace).setHeader("Umístění").setEditorComponent(placeField);

        NumberField timeField = new NumberField();
        binder.forField(timeField).withConverter(Double::longValue, Long::doubleValue, "Err").bind(RaceEntry::getRaceTime, RaceEntry::setRaceTime);
        grid.addColumn(RaceEntry::getRaceTime).setHeader("Čas (ms)").setEditorComponent(timeField);

        Grid.Column<RaceEntry> editCol = grid.addComponentColumn(e -> {
            Button edit = new Button("Upravit");
            edit.addClickListener(ev -> {
                if (editor.isOpen()) editor.cancel();
                editor.editItem(e);
            });
            return edit;
        });

        Button save = new Button("Uložit", e -> editor.save());
        Button cancel = new Button("Zrušit", e -> editor.cancel());
        editCol.setEditorComponent(new HorizontalLayout(save, cancel));

        editor.addSaveListener(e -> {
            try {
                raceEntryService.updateRaceEntry(e.getItem());
                Notification.show("Uloženo.");
            } catch (SQLException ex) {
                Notification.show("Chyba: " + ex.getMessage());
            }
        });

        grid.setItems(entries);
        layout.add(grid);

        return layout;
    }
}